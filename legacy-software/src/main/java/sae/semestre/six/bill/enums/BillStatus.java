package sae.semestre.six.bill.enums;

/**
 * Enumération représentant les statuts possibles d'une facture.
 * Actuellement, seule la valeur {@code PENDING} est définie.
 */
public enum BillStatus {

    /**
     * Statut indiquant que la facture est en attente de paiement ou de traitement.
     */
    PENDING("PENDING");

    private final String value;

    /**
     * Constructeur de l'enum avec une valeur textuelle associée.
     *
     * @param value la représentation textuelle du statut
     */
    BillStatus(String value) {
        this.value = value;
    }

    /**
     * Retourne la représentation textuelle du statut.
     *
     * @return la valeur du statut sous forme de chaîne
     */
    @Override
    public String toString() {
        return value;
    }
}