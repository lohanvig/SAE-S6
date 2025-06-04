package sae.semestre.six.bill.enums;

public enum BillStatus {
    PENDING("PENDING");

    private final String value;

    BillStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}