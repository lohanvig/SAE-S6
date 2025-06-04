package sae.semestre.six.bill.dto;

import sae.semestre.six.invoice.model.SupplierInvoice;
import sae.semestre.six.invoice.model.SupplierInvoiceDetail;

import java.time.LocalDateTime;
import java.util.Set;

public class BillHashDto {
    private String invoiceNumber;
    private String supplierName;
    private LocalDateTime invoiceDate;
    private Double totalAmount;
    private Set<SupplierInvoiceDetail> details;

    public BillHashDto(SupplierInvoice invoice) {
        this.invoiceNumber = invoice.getInvoiceNumber();
        this.supplierName = invoice.getSupplierName();
        this.invoiceDate = invoice.getInvoiceDate();
        this.totalAmount = invoice.getTotalAmount();
        this.details = invoice.getDetails();
    }

}
