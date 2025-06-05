package sae.semestre.six.bill.dto;

import sae.semestre.six.bill.model.Bill;
import sae.semestre.six.bill.model.BillDetail;

import java.util.Date;
import java.util.Set;

/**
 * DTO utilisé pour représenter les données essentielles d'une facture (Bill) avec ses détails
 * pour le calcul ou la vérification de son hash.
 */
public class BillHashDto {

    /** Numéro unique de la facture */
    private String invoiceNumber;

    /** Nom du fournisseur (non initialisé ici, peut être ajouté si nécessaire) */
    private String supplierName;

    /** Identifiant du médecin lié à la facture */
    private long doctorId;

    /** Identifiant du patient lié à la facture */
    private long patientId;

    /** Date de la facture */
    private Date invoiceDate;

    /** Montant total de la facture */
    private Double totalAmount;

    /** Ensemble des détails de la facture */
    private Set<BillDetail> details;

    /**
     * Constructeur qui initialise ce DTO à partir d'une entité Bill.
     *
     * @param bill la facture dont on extrait les données
     */
    public BillHashDto(Bill bill) {
        this.invoiceNumber = bill.getBillNumber();
        this.doctorId = bill.getDoctor().getId();
        this.patientId = bill.getPatient().getId();
        this.invoiceDate = bill.getBillDate();
        this.totalAmount = bill.getTotalAmount();
        this.details = bill.getBillDetails();
    }
}
