package sae.semestre.six.bill.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Data
public class BillDto {

    private String billNumber;
    private double totalAmount;
    private String patientId;
    private String doctorId;
    private Set<String> treatments;

    public BillDto(String billNumber, double totalAmount, String patientId, String doctorId, Set<String> treatments) {
        this.billNumber = billNumber;
        this.totalAmount = totalAmount;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.treatments = treatments;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public Set<String> getTreatments() {
        return treatments;
    }

    public void setTreatments(Set<String> treatments) {
        this.treatments = treatments;
    }
}