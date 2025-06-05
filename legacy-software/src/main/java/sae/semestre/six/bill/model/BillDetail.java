package sae.semestre.six.bill.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Représente un détail de facture, c'est-à-dire un traitement spécifique appliqué dans une facture.
 */
@Entity
@Table(name = "bill_details")
public class BillDetail {

    /** Identifiant unique du détail de facture. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Facture associée à ce détail. */
    @ManyToOne
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

    /** Nom du traitement ou service effectué. */
    @Column(name = "treatment_name")
    private String treatmentName;

    /** Quantité du traitement effectué (par défaut 1). */
    @Column(name = "quantity")
    private Integer quantity = 1;

    /** Prix unitaire du traitement. */
    @Column(name = "unit_price")
    private Double unitPrice = 0.0;

    /** Prix total de la ligne (quantité * prix unitaire). */
    @Column(name = "line_total")
    private Double lineTotal = 0.0;

    /**
     * Calcule le montant total de cette ligne en fonction de la quantité et du prix unitaire.
     */
    public void calculateLineTotal() {
        this.lineTotal = this.quantity * this.unitPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(Double lineTotal) {
        this.lineTotal = lineTotal;
    }
}