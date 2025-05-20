package sae.semestre.six.bill.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.bill.dao.BillDaoImpl;
import sae.semestre.six.bill.model.Bill;
import sae.semestre.six.bill.model.BillDetail;
import sae.semestre.six.doctor.dao.DoctorDao;
import sae.semestre.six.doctor.model.Doctor;
import sae.semestre.six.patient.dao.PatientDao;
import sae.semestre.six.patient.model.Patient;
import sae.semestre.six.service.EmailService;
import sae.semestre.six.utils.FileInitializer;

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
     * Représente le chiffre d'affaires total généré.
     */
    private double totalRevenue = 0.0;

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

        Patient patient = patientDao.findByPatientNumber(patientId)
                                    .orElseThrow(() -> new NoSuchElementException("Patient non trouvé"));

        Doctor doctor = doctorDao.findByDoctorNumber(doctorNumber)
                                 .orElseThrow(() -> new NoSuchElementException("Médecin non trouvé"));

        // Création de la facture
        Bill bill = new Bill();
        bill.setBillNumber("BILL" + System.currentTimeMillis());
        bill.setPatient(patient);
        bill.setDoctor(doctor);

        double total = 0.0;
        Set<BillDetail> details = new HashSet<>();

        // Création des lignes de facture
        for (String treatment : treatments) {
            double price = priceList.getOrDefault(treatment, 0.0);
            total += price;

            BillDetail detail = new BillDetail();
            detail.setBill(bill);
            detail.setTreatmentName(treatment);
            detail.setUnitPrice(price);
            details.add(detail);
        }

        // Application d'une remise si le total dépasse un montant max
        if (total > DISCOUNT_AMOUNT) {
            total *= DISCOUNT_RATE;
        }

        bill.setTotalAmount(total);
        bill.setBillDetails(details);

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
        totalRevenue += total;
        billDao.save(bill);

        emailService.sendEmail(
                adminEmail,
                "New Bill Generated",
                "Bill Number: " + bill.getBillNumber() + "\nTotal: $" + total
        );

        return bill.getBillNumber();
    }
} 