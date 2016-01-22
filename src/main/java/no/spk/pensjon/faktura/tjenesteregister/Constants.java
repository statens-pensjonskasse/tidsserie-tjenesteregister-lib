package no.spk.pensjon.faktura.tjenesteregister;

/**
 * Definerer standard navn for egenskapar som er felles på tvers av alle typer tenester i tenesteregisteret.
 *
 * @author Tarjei Skorgenes
 * @since 1.0.0
 */
public class Constants {
    /**
     * Tjenesteegenskap som identifiserer ei tjeneste sitt ranking-tall.
     * <br>
     * Denne egenskapen kan bli inkludert i egenskapane som blir sendt til
     * {@link ServiceRegistry#registerService(Class, Object, String...)}.
     * <br>
     * Verdien av egenskapen må vere ein tekst som inneheld eit heiltall.
     * <br>
     * Rankingen blir brukt av tenesteregisteret til å determinere den naturlige rekkefølga for tenester som er
     * registrert under samme tenestetype.
     * <br>
     * Dette blir blant anna brukt ved oppslag av standard tenesta for ein bestemt tenestetype ved kall til
     * {@link ServiceRegistry#getServiceReference(Class)}.
     * <br>
     * Standardranking for tenester som ikke angir ranking er {@code 0}. Ei teneste med ranking {@link
     * Integer#MAX_VALUE} er svært sannyligvis
     * den som vil bli returnert som standardtenesta, medan ei teneste med ranking {@link Integer#MIN_VALUE} er svært
     * usannsynlig at vil bli returnert som standardtenesta.
     */
    public static final String SERVICE_RANKING = "service.ranking";
}
