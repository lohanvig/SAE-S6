package sae.semestre.six.bill.dto;

import sae.semestre.six.bill.model.Bill;
import sae.semestre.six.bill.model.BillDetail;
import sae.semestre.six.invoice.model.SupplierInvoice;
import sae.semestre.six.invoice.model.SupplierInvoiceDetail;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

public class BillHashDto {
    private String invoiceNumber;
    private String supplierName;
    private long doctorId;
    private long patientId;
    private Date invoiceDate;
    private Double totalAmount;
    private Set<BillDetail> details;

    public BillHashDto(Bill bill) {
        this.invoiceNumber = bill.getBillNumber();
        this.doctorId = bill.getDoctor().getId();
        this.patientId = bill.getPatient().getId();
        this.invoiceDate = bill.getBillDate();
        this.totalAmount = bill.getTotalAmount();
        this.details = bill.getBillDetails();
    }

}
