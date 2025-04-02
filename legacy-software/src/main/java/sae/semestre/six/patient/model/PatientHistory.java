package sae.semestre.six.patient.model;

import jakarta.persistence.*;
import sae.semestre.six.bill.model.Bill;
import sae.semestre.six.appointment.model.Appointment;
import sae.semestre.six.model.LabResult;
import sae.semestre.six.prescription.model.Prescription;
import sae.semestre.six.model.Treatment;

import java.util.*;

@Entity
@Table(name = "patient_history")
public class PatientHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "patient_id")
    private Patient patient;
    
    @OneToMany(mappedBy = "patientHistory", fetch = FetchType.EAGER) 
    private Set<Appointment> appointments = new HashSet<>();
    
    @OneToMany(fetch = FetchType.EAGER) 
    private Set<Prescription> prescriptions = new HashSet<>();
    
    @OneToMany(fetch = FetchType.EAGER) 
    private Set<Treatment> treatments = new HashSet<>();
    
    @OneToMany(fetch = FetchType.EAGER) 
    private Set<Bill> bills = new HashSet<>();
    
    @OneToMany(fetch = FetchType.EAGER) 
    private Set<LabResult> labResults = new HashSet<>();
    
    @Column(name = "visit_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date visitDate;
    
    @Column(columnDefinition = "TEXT")
    private String diagnosis;
    
    @Column(columnDefinition = "TEXT")
    private String symptoms;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    
    public Set<Appointment> getAppointments() {
        
        return new TreeSet<>(appointments);
    }
    
    public List<Bill> getBillsSorted() {
        
        List<Bill> sortedBills = new ArrayList<>(bills);
        Collections.sort(sortedBills, (b1, b2) -> b2.getBillDate().compareTo(b1.getBillDate()));
        return sortedBills;
    }
    
    
    public Double getTotalBilledAmount() {
        return bills.stream()
            .mapToDouble(Bill::getTotalAmount)
            .sum();
    }
} 