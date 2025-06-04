package sae.semestre.six.invoice.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "supplier_invoices")
public class SupplierInvoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "invoice_number", unique = true)
    private String invoiceNumber;
    
    @Column(name = "supplier_name")
    private String supplierName;
    
    @Column(name = "invoice_date")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime invoiceDate = LocalDateTime.now();
    
    @OneToMany(mappedBy = "supplierInvoice", cascade = CascadeType.ALL)
    private Set<SupplierInvoiceDetail> details = new HashSet<>();
    
    @Column(name = "total_amount")
    private Double totalAmount = 0.0;

    @Column(name = "locked")
    private boolean locked;

    @Column(name = "hash")
    private String hash;
    
    public Set<SupplierInvoiceDetail> getDetails() {
        return details;
    }
    
    public void setDetails(Set<SupplierInvoiceDetail> details) {
        this.details = details;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public LocalDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void addDetails(SupplierInvoiceDetail details) { this.details.add(details); }

    public boolean isLocked() { return locked; }
    public void lock() {
        if (this.isLocked()) {
            throw new IllegalArgumentException("Cette facture est déjà vérouillée.");
        }

        //TODO : faire le hash le sauvegarde est vérouiller la facture

    }

    public void isInvoiceTampered() {
        //TODO : hash la facture courante et vérifie que le has soit le meme que celui enregistré
    }

    private void hashInvoice() {
        //TODO : complète en hashan toutes les infos de la facture
    }
}