package no.spk.pensjon.faktura.tjenesteregister;

import java.util.Optional;

/**
 * Ein tjenestereferanse er ein indirekte peikar til ein konkret implementasjon av
 * ei bestemt tjenestetype.
 * <br>
 * Sjølve tjenesta kan slåast opp frå tjenesteregisteret via {@link ServiceRegistry#getService(ServiceReference)}.
 * <br>
 * Ein tjenestereferanse vil ikkje lenger vere gyldig etter at tjenesta den peikar til har blitt avregistrert
 * frå tjenesteregisteret. Forsøk på å slå opp den tilhøyrande tjeneste frå tjenesregisteret vil i slike tilfelle
 * ikkje returnere nokon treff.
 *
 * @param <T> tjenestetypen som referansen peikar til ein instans av
 * @author Tarjei Skorgenes
 * @since 1.0.0
 */
public interface ServiceReference<T> {
    /**
     * Hentar ut verdien av egenskapen med navn lik verdien av {@code name}.
     *
     * @param name navnet på tenesteegenskapen som skal returnerast
     * @return verdien for den navngitte egenskapen, eller {@link Optional#empty()} dersom tenesta ikkje har denne
     * egenskapen
     */
    Optional<String> getProperty(String name);
}
