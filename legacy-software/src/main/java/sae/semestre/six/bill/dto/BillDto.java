package sae.semestre.six.bill.dto;

import lombok.Data;

import java.util.Set;

/**
 * DTO (Data Transfer Object) représentant une facture simplifiée à échanger entre les couches de l'application.
 * Il contient les informations essentielles d'une facture sans inclure de logique métier.
 */
@Data
public class BillDto {

    /** Numéro unique de la facture. */
    private String billNumber;

    /** Montant total de la facture. */
    private double totalAmount;

    /** Identifiant du patient concerné par la facture. */
    private String patientId;

    /** Identifiant du médecin ayant prescrit les traitements. */
    private String doctorId;

    /** Ensemble des traitements appliqués dans la facture. */
    private Set<String> treatments;

    /**
     * Constructeur complet permettant de créer une facture DTO.
     *
     * @param billNumber le numéro unique de la facture
     * @param totalAmount le montant total de la facture
     * @param patientId l'identifiant du patient
     * @param doctorId l'identifiant du médecin
     * @param treatments l'ensemble des traitements appliqués
     */
    public BillDto(String billNumber, double totalAmount, String patientId, String doctorId, Set<String> treatments) {
        this.billNumber = billNumber;
        this.totalAmount = totalAmount;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.treatments = treatments;
    }

    // Les getters et setters sont générés automatiquement par Lombok (@Data),
    // mais réécrits ici manuellement pour une éventuelle personnalisation.

    /**
     * Retourne le numéro de la facture.
     * @return le numéro de facture
     */
    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    /**
     * Retourne le montant total.
     * @return le montant total
     */
    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Retourne l'identifiant du patient.
     * @return l'ID du patient
     */
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    /**
     * Retourne l'identifiant du médecin.
     * @return l'ID du médecin
     */
    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    /**
     * Retourne la liste des traitements.
     * @return les traitements
     */
    public Set<String> getTreatments() {
        return treatments;
    }

    public void setTreatments(Set<String> treatments) {
        this.treatments = treatments;
    }
}
