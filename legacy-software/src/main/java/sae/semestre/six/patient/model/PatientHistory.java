package sae.semestre.six.patient.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import sae.semestre.six.bill.model.Bill;
import sae.semestre.six.appointment.model.Appointment;
import sae.semestre.six.model.LabResult;
import sae.semestre.six.prescription.model.Prescription;
import sae.semestre.six.treatment.model.Treatment;

import java.util.*;

@Setter
@Getter
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setAppointments(Set<Appointment> appointments) {
        this.appointments = appointments;
    }

    public Set<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(Set<Prescription> prescriptions) {
        this.prescriptions = prescriptions;
    }

    public Set<Treatment> getTreatments() {
        return treatments;
    }

    public void setTreatments(Set<Treatment> treatments) {
        this.treatments = treatments;
    }

    public Set<Bill> getBills() {
        return bills;
    }

    public void setBills(Set<Bill> bills) {
        this.bills = bills;
    }

    public Set<LabResult> getLabResults() {
        return labResults;
    }

    public void setLabResults(Set<LabResult> labResults) {
        this.labResults = labResults;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}