package sae.semestre.six.bill.model;

import jakarta.persistence.*;
import sae.semestre.six.bill.enums.BillStatus;
import sae.semestre.six.doctor.model.Doctor;
import sae.semestre.six.patient.model.Patient;
import sae.semestre.six.patient.model.PatientHistory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "bills")
public class Bill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "bill_number", unique = true)
    private String billNumber;
    
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    
    @Column(name = "bill_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date billDate = new Date();
    
    @Column(name = "total_amount")
    private Double totalAmount = 0.0;
    
    @Column(name = "status")
    private String status = BillStatus.PENDING.toString();
    
    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<BillDetail> billDetails = new HashSet<>();
    
    
    @Column(name = "created_date")
    private Date createdDate = new Date();
    
    @Column(name = "last_modified")
    private Date lastModified = new Date();

    @Column(name = "hash")
    private String hash;

    @ManyToOne
    private PatientHistory patientHistory;
    
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getBillNumber() { return billNumber; }

    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }
    
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    
    public Date getBillDate() { return billDate; }
    public void setBillDate(Date billDate) { this.billDate = billDate; }
    
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { 
        this.status = status;
        this.lastModified = new Date(); 
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void lock() {
        if (this.hash != null) {
            throw new IllegalArgumentException("Cette facture est déjà vérouillée.");
        }

        this.hash = hashInvoice(); // Génére et sauvegarde le hash
    }

    public String recalculateHash() {
        return hashInvoice();
    }

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

    public Set<BillDetail> getBillDetails() { return billDetails; }
    public void setBillDetails(Set<BillDetail> billDetails) { this.billDetails = billDetails; }

    /**
     * Creates a new bill for the given patient and doctor.
     * @param patient the patient for whom the bill is being created
     * @param doctor the doctor associated with the bill
     * @return a new instance with the provided patient and doctor
     */
    public static Bill createBill(Patient patient, Doctor doctor) {
        Bill bill = new Bill();
        bill.setBillNumber("BILL" + System.currentTimeMillis());
        bill.setPatient(patient);
        bill.setDoctor(doctor);

        return bill;
    }

    /**
     * Applies a discount to the bill if the total amount exceeds a certain threshold.
     * @param discountAmount the threshold amount for applying the discount
     * @param discountRate the rate at which to apply the discount
     */
    private void applyDiscount(float discountAmount, float discountRate) {
        if (this.getTotalAmount() > discountAmount) {
            this.setTotalAmount(this.getTotalAmount() * discountRate);
        }
    }

    /**
     * Factory method to create a BillDetail instance.
     * @param treatment The treatment name.
     * @param price The unit price of the treatment.
     * @return A new BillDetail instance.
     */
    public void addBillDetails(String treatment, double price, float discountAmount, float discountRate) {
        BillDetail detail = new BillDetail();
        detail.setBill(this);
        detail.setTreatmentName(treatment);
        detail.setUnitPrice(price);
        this.billDetails.add(detail);

        this.setTotalAmount(this.getTotalAmount() + detail.getUnitPrice());
        this.applyDiscount(discountAmount, discountRate);
    }
}