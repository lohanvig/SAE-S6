package sae.semestre.six.bill.dto;

import java.util.List;

public class BillRequestDto {
    private String patientId;
    private String doctorNumber;
    private List<TreatmentRequestDTO> treatments;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorNumber() {
        return doctorNumber;
    }

    public void setDoctorNumber(String doctorNumber) {
        this.doctorNumber = doctorNumber;
    }

    public List<TreatmentRequestDTO> getTreatments() {
        return treatments;
    }

    public void setTreatments(List<TreatmentRequestDTO> treatments) {
        this.treatments = treatments;
    }
}
