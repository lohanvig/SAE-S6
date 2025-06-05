package sae.semestre.six.bill.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import sae.semestre.six.bill.enums.BillStatus;
import sae.semestre.six.doctor.model.Doctor;
import sae.semestre.six.insurance.model.Insurance;
import sae.semestre.six.insurance.service.InsuranceService;
import sae.semestre.six.patient.model.Patient;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Représente une facture associée à un patient et un médecin, contenant plusieurs traitements.
 */
@Entity
@Table(name = "bills")
public class Bill {

    /** Identifiant unique de la facture (clé primaire). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Numéro unique de la facture. */
    @Column(name = "bill_number", unique = true)
    private String billNumber;

    /** Patient concerné par la facture. */
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    /** Médecin émetteur de la facture. */
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    /** Date de la facture. */
    @Column(name = "bill_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date billDate = new Date();

    /** Montant total de la facture. */
    @Column(name = "total_amount")
    private Double totalAmount = 0.0;

    /** Statut de la facture (ex : EN_ATTENTE, PAYÉE). */
    @Column(name = "status")
    private String status = BillStatus.PENDING.toString();

    /** Ensemble des traitements liés à la facture. */
    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<BillDetail> billDetails = new HashSet<>();

    /** Date de création de la facture. */
    @Column(name = "created_date")
    private Date createdDate = new Date();

    /** Date de dernière modification de la facture. */
    @Column(name = "last_modified")
    private Date lastModified = new Date();

    /** Hash SHA-256 de la facture, utilisé pour la verrouiller. */
    @Column(name = "hash")
    private String hash;

    private static InsuranceService insuranceService;

    public static void setInsuranceService(InsuranceService service) {
        insuranceService = service;
    }

    /**
     * Verrouille la facture en générant un hash.
     * @throws IllegalArgumentException si la facture est déjà verrouillée.
     */
    public void lock() {
        if (this.hash != null) {
            throw new IllegalArgumentException("Cette facture est déjà vérouillée.");
        }
        this.hash = hashInvoice();
    }

    /**
     * Recalcule le hash SHA-256 de la facture (sans le sauvegarder).
     * @return le hash calculé
     */
    public String recalculateHash() {
        return hashInvoice();
    }

    /**
     * Calcule le hash SHA-256 de la facture à partir de ses données essentielles.
     * @return hash en hexadécimal
     */
    private String hashInvoice() {
        StringBuilder sb = new StringBuilder();
        sb.append(billNumber != null ? billNumber : "");
        sb.append(patient != null ? patient.getId() : "");
        sb.append(doctor != null ? doctor.getId() : "");
        sb.append(billDate != null ? billDate.getTime() : "");
        sb.append(String.format("%.2f", totalAmount));
        sb.append(status != null ? status : "");

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du calcul du hash", e);
        }
    }

    /**
     * Crée une nouvelle facture associée à un patient et un médecin.
     * @param patient patient concerné
     * @param doctor médecin émetteur
     * @return une nouvelle instance de facture
     */
    public static Bill createBill(Patient patient, Doctor doctor) {
        Bill bill = new Bill();
        bill.setBillNumber("BILL" + System.currentTimeMillis());
        bill.setPatient(patient);
        bill.setDoctor(doctor);
        return bill;
    }

    /**
     * Applique une remise si le montant dépasse un certain seuil.
     * @param discountAmount seuil à partir duquel appliquer une réduction
     * @param discountRate taux de réduction à appliquer
     */
    private void applyDiscount(float discountAmount, float discountRate) {
        if (this.getTotalAmount() > discountAmount) {
            this.setTotalAmount(this.getTotalAmount() * discountRate);
        }
    }

    /**
     * Ajoute un traitement à la facture, met à jour le montant total et applique une éventuelle remise.
     * @param treatment nom du traitement
     * @param quantity quantité
     * @param price prix unitaire du traitement
     * @param discountAmount seuil de remise
     * @param discountRate taux de remise
     * @param insurance assurance du patient pour le calcul de la couverture
     */
    public void addBillDetails(String treatment, double price, int quantity, float discountAmount, float discountRate,
                               Insurance insurance) {
        BillDetail detail = new BillDetail();
        detail.setBill(this);
        detail.setTreatmentName(treatment);
        detail.setUnitPrice(price);
        detail.setQuantity(quantity);
        this.billDetails.add(detail);

        this.setTotalAmount(this.getTotalAmount() + detail.getUnitPrice() * quantity);
        this.setTotalAmount(this.getTotalAmount() - insurance.calculateCoverage(this.getTotalAmount()));
        this.applyDiscount(discountAmount, discountRate);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<BillDetail> getBillDetails() {
        return billDetails;
    }

    public void setBillDetails(Set<BillDetail> billDetails) {
        this.billDetails = billDetails;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}