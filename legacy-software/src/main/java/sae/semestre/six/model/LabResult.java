package sae.semestre.six.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import sae.semestre.six.patient.model.PatientHistory;

import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "lab_results")
public class LabResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "patient_history_id")
    private PatientHistory patientHistory;
    
    @Column(name = "test_name")
    private String testName;
    
    @Column(name = "result_value")
    private String resultValue;
    
    @Column(name = "test_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date testDate;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;


}