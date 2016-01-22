package no.spk.pensjon.faktura.tjenesteregister;

import java.util.Properties;

/**
 * Ei registrert teneste.
 * <br>
 * Registreringa blir generert ved kall til {@link ServiceRegistry#registerService(Class, Object)} /
 * {@link ServiceRegistry#registerService(Class, Object, String...)} og er privat for tjenesteleverandøren
 * som registrerer tjenesta i tjenesteregisteret. Den bør ikkje delast med andre.
 *
 * @param <T> tjenestetypen for den registrerte tjenesta
 * @author Tarjei Skorgenes
 * @since 1.0.0
 */
public interface ServiceRegistration<T> {
    /**
     * Returnerer ein indirekte referanse til tjenesta.
     * <br>
     * Referansen kan benyttast til å hente ut tjenesteinstansen frå tjenesteregisteret dersom
     * tjenesta ikkje har blitt {@link #unregister() avregistrert} sidan registreringa vart oppretta.
     *
     * @return ein indirekte referanse til tjenesta
     */
    ServiceReference<T> getReference();

    /**
     * Fjernar den registrerte tjenesta frå tjenesteregisteret.
     * <br>
     * Som følge av dette blir det ikkje lenger mulig for klientar å slå opp nye indirekte eller direkte referansar til
     * tjenesta via tjenesteregisteret. Alle tidligare uthenta {@link #getReference() referansar} til tjenesta blir
     * ugyldige straks fjerninga returnerer.
     * <br>
     * Merk at klientar som held på ein direkte referanse til instansen for tjenesta frå tidligare oppslag via
     * {@link ServiceRegistry#getService(ServiceReference)} ikkje lenger kan stole på at desse instansane fungerer
     * og dei bør derfor slutte å bruke tenesta.
     */
    void unregister();
}
