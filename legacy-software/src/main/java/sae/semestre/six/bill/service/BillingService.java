package sae.semestre.six.bill.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.bill.dao.BillDaoImpl;
import sae.semestre.six.bill.enums.BillStatus;
import sae.semestre.six.bill.model.Bill;
import sae.semestre.six.bill.model.BillDetail;
import sae.semestre.six.doctor.dao.DoctorDao;
import sae.semestre.six.doctor.model.Doctor;
import sae.semestre.six.patient.dao.PatientDao;
import sae.semestre.six.patient.model.Patient;
import sae.semestre.six.service.EmailService;
import sae.semestre.six.treatment.model.Treatment;
import sae.semestre.six.treatment.service.TreatmentService;
import sae.semestre.six.utils.FileInitializer;
import sae.semestre.six.bill.model.Bill;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Service dédié à la gestion de la facturation des patients.
 * Il centralise la logique métier liée à la création d'une facture :
 * récupération des données, calcul du montant, écriture dans un fichier, envoi d'un e-mail.
 */
@Service
public class BillingService {

    @Autowired
    private PatientDao patientDao;

    @Autowired
    private DoctorDao doctorDao;

    @Autowired
    private BillDaoImpl billDao;

    @Autowired
    private TreatmentService treatmentService;

    @Value("${admin.email}")
    private String adminEmail;

    private final EmailService emailService = EmailService.getInstance();;


    /**
     * Liste des traitements et de leurs prix associés.
     */
    private final Map<String, Double> priceList = Map.of(
            "CONSULTATION", 50.0,
            "XRAY", 150.0,
            "BLOOD_TEST", 100.0
    );

    /**
     * Montant maximum avant remise
     */
    private final static int DISCOUNT_AMOUNT = 500;

    /**
     * Montant de la remise
     */
    private final static float DISCOUNT_RATE = 0.9f;


    /**
     * Traite la facturation d'un patient à partir des traitements reçus
     * et retourne une réponse JSON avec le détail de la facture.
     *
     * @param patientId  identifiant du patient
     * @param doctorNumber   identifiant du médecin
     * @param treatments liste des traitements appliqués
     * @return un objet contenant le numéro de facture, le total, les identifiants et traitements
     * @throws IOException en cas d'erreur d'écriture du fichier de facturation
     */
    @Transactional
    public String processBill(String patientId, String doctorNumber, String[] treatments) throws IOException {
        // Chargement des entités Patient et Doctor depuis la base de données
        Patient patient;
        try {
            patient = patientDao.findByPatientNumber(patientId);
        } catch (Exception e) {
            throw new NoSuchElementException("Patient non trouvé");
        }

        Doctor doctor;
        try {
            doctor = doctorDao.findByDoctorNumber(doctorNumber);
        } catch (Exception e) {
            throw new NoSuchElementException("Médecin non trouvé");
        }

        // Création de la facture
        Bill bill = Bill.createBill(patient, doctor);

        // Création des lignes de facture
        for (String treatment : treatments) {
            double price = priceList.getOrDefault(treatment, 0.0);
            bill.addBillDetails(treatment, price, DISCOUNT_AMOUNT, DISCOUNT_RATE);
        }

        double total = bill.getTotalAmount();

        bill.lock();

        // Écriture dans un fichier texte de sauvegarde
        File billFile = new File(FileInitializer.BILL_FOLDER + bill.getBillNumber() + ".txt");

        // Vérifier si le fichier existe, sinon le créer
        if (!billFile.exists()) {
            try {
                billFile.createNewFile();
            } catch (IOException e) {
                throw new IOException("Erreur lors de la création du fichier de facturation", e);
            }
        }

        try (FileWriter fw = new FileWriter(billFile, true)) {
            fw.write(bill.getBillNumber() + ": $" + total + "\n");
        }

        // Enregistrement en base et envoi d'e-mail de notification
        billDao.save(bill);

        emailService.sendEmail(
                adminEmail,
                "New Bill Generated",
                "Bill Number: " + bill.getBillNumber() + "\nTotal: $" + total
        );

        return bill.getBillNumber();
    }

    public Double getTotalRevenue() {
        Double revenue = billDao.getTotalRevenue();
        return revenue != null ? revenue : 0.0;
    }

    public List<Bill> getPendingBills() {
        return billDao.findByStatus(BillStatus.PENDING.toString());
    }

    public List<Map<String, Object>> verifyAllBillsIntegrity() {
        List<Map<String, Object>> integrityResults = new ArrayList<>();
        List<Bill> bills = billDao.findAll();

        for (Bill bill : bills) {
            Map<String, Object> result = new HashMap<>();
            result.put("billNumber", bill.getBillNumber());

            try {
                String storedHash = bill.getHash();
                String computedHash = bill.recalculateHash();

                boolean isValid = storedHash != null && storedHash.equals(computedHash);
                result.put("isValid", isValid);
            } catch (Exception e) {
                result.put("isValid", false);
            }

            integrityResults.add(result);
        }

        return integrityResults;
    }

    public double getBillTotal(String billNumber) {
        Bill bill;
        try {
            bill = billDao.findByBillNumber(billNumber);
        } catch (Exception e) {
            throw new NoSuchElementException("Facture non trouvée " + billNumber);
        }

        return bill.getTotalAmount();
    }

} 