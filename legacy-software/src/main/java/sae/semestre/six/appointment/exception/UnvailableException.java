package sae.semestre.six.appointment.exception;

/**
 * Exception levée lorsqu'une ressource (ex : un créneau horaire ou un médecin)
 * est indisponible pour une opération demandée.
 */
public class UnvailableException extends RuntimeException {

    /**
     * Constructeur avec message personnalisé.
     *
     * @param message Le message d'erreur à afficher.
     */
    public UnvailableException(String message) {
        super(message);
    }
}
