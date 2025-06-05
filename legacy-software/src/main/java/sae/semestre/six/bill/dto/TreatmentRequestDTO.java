package sae.semestre.six.bill.dto;

import org.springframework.stereotype.Component;

@Component
public class TreatmentRequestDTO {
    private String code;
    private int quantity;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
