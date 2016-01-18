package no.spk.pensjon.faktura.tjenesteregister;

/**
 * Blir kasta dersom {@link ServiceRegistry#getServiceReferences(Class, String...)} eller
 * {@link ServiceRegistry#getServiceReference(Class, String...)} blir kalla med eit eller fleire filter
 * som er syntaktisk ugyldige.
 *
 * @author Tarjei Skorgenes
 */
public class UgyldigSyntaxException extends RuntimeException {
    private final static long serialVersionUID = 1;

    public UgyldigSyntaxException(final String message) {
        super(message);
    }
}
