package sae.semestre.six.bill.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.bill.dao.BillDaoImpl;
import sae.semestre.six.bill.dto.TreatmentRequestDTO;
import sae.semestre.six.bill.enums.BillStatus;
import sae.semestre.six.bill.model.Bill;
import sae.semestre.six.doctor.dao.DoctorDao;
import sae.semestre.six.doctor.model.Doctor;
import sae.semestre.six.insurance.service.InsuranceService;
import sae.semestre.six.inventory.service.InventoryService;
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
 * Il centralise la logique métier liée à la création d'une facture,
 * la sauvegarde dans un fichier, l'enregistrement en base de données
 * et l'envoi d'une notification par e-mail.
 */
@Service
public class BillingService {

    /** DAO pour accéder aux patients. */
    @Autowired
    private PatientDao patientDao;

    /** DAO pour accéder aux médecins. */
    @Autowired
    private DoctorDao doctorDao;

    @Autowired
    private InventoryService inventoryService;

    /** DAO personnalisé pour accéder aux factures. */
    @Autowired
    private BillDaoImpl billDao;

    /** Adresse e-mail de l'administrateur pour recevoir les notifications. */
    @Value("${admin.email}")
    private String adminEmail;

    /** Service d'envoi d’e-mails (singleton). */
    private final EmailService emailService = EmailService.getInstance();

    /** Seuil de déclenchement d’une remise (en euros). */
    private static final int DISCOUNT_AMOUNT = 500;

    /** Taux de remise (par exemple, 0.9 signifie 10 % de réduction). */
    private static final float DISCOUNT_RATE = 0.9f;

    @Autowired
    private InsuranceService insuranceService;

    /**
     * Traite la facturation d’un patient, génère la facture, l’enregistre,
     * crée un fichier, et envoie un e-mail à l’administrateur.
     *
     * @param patientId identifiant du patient (numéro)
     * @param doctorNumber identifiant du médecin (numéro)
     * @param treatments liste des traitements appliqués
     * @return le numéro de facture généré
     * @throws IOException en cas d’erreur d’écriture du fichier
     */
    @Transactional
    public String processBill(String patientId, String doctorNumber, List<TreatmentRequestDTO> treatments) throws IOException {
        // Récupération du patient
        Patient patient;
        try {
            patient = patientDao.findByPatientNumber(patientId);
        } catch (Exception e) {
            throw new NoSuchElementException("Patient non trouvé");
        }

        // Récupération du médecin
        Doctor doctor;
        try {
            doctor = doctorDao.findByDoctorNumber(doctorNumber);
        } catch (Exception e) {
            throw new NoSuchElementException("Médecin non trouvé");
        }

        // Création de la facture
        Bill bill = Bill.createBill(patient, doctor);

        // Ajout des traitements à la facture
        for (TreatmentRequestDTO treatment : treatments) {
            String code = treatment.getCode();
            int quantity = treatment.getQuantity();

            // Décrémente le stock et récupère le prix unitaire
            double unitPrice;
            try {
                unitPrice = inventoryService.decreaseStock(code, quantity);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Erreur de stock pour le traitement : " + code + " - " + e.getMessage());
            }

            // Ajoute les détails à la facture (quantité * prix unitaire)
            bill.addBillDetails(code, unitPrice * quantity, quantity, DISCOUNT_AMOUNT, DISCOUNT_RATE,
                                insuranceService.getByPatientId(patient.getId()));
        }

        double total = bill.getTotalAmount();
        bill.lock();

        // Création du fichier de sauvegarde
        File billFile = new File(FileInitializer.BILL_FOLDER + bill.getBillNumber() + ".txt");

        if (!billFile.exists()) {
            try {
                billFile.createNewFile();
            } catch (IOException e) {
                throw new IOException("Erreur lors de la création du fichier de facturation", e);
            }
        }

        // Écriture du contenu dans le fichier
        try (FileWriter fw = new FileWriter(billFile, true)) {
            fw.write(bill.getBillNumber() + ": $" + total + "\n");
        }

        // Enregistrement en base de données
        billDao.save(bill);

        // Notification par e-mail
        emailService.sendEmail(
                adminEmail,
                "New Bill Generated",
                "Bill Number: " + bill.getBillNumber() + "\nTotal: $" + total
        );

        return bill.getBillNumber();
    }

    /**
     * Récupère le chiffre d’affaires total généré par toutes les factures.
     *
     * @return le montant total (0.0 si aucune facture)
     */
    public Double getTotalRevenue() {
        Double revenue = billDao.getTotalRevenue();
        return revenue != null ? revenue : 0.0;
    }

    /**
     * Récupère la liste des factures en attente de paiement.
     *
     * @return liste des factures en attente
     */
    public List<Bill> getPendingBills() {
        return billDao.findByStatus(BillStatus.PENDING.toString());
    }

    /**
     * Vérifie l’intégrité de toutes les factures en comparant leur empreinte stockée
     * à une empreinte recalculée à partir des données.
     *
     * @return liste des résultats de vérification pour chaque facture
     */
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

    /**
     * Récupère le montant total d’une facture à partir de son numéro.
     *
     * @param billNumber numéro de la facture
     * @return le montant total
     * @throws NoSuchElementException si la facture n’existe pas
     */
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