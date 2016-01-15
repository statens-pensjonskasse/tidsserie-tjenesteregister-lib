package no.spk.pensjon.faktura.tjenesteregister;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * {@link ServiceRegistry} er eit tjenesteregister for in-JVM tjenester.
 * <br>
 * Tjenesteregisteret kan tenkast på som sin {@code Map<Class<T>, List<T>>} på steroider, designet er skamlaust
 * kopiert/inspirert av tjenesteregister-konseptet i OSGI og OSGI sitt mikrotjeneste-konsept, sjå følgjande for meir
 * informasjon:
 * <ul>
 * <li> <a href="http://paulonjava.blogspot.no/2014/04/micro-services-vs-osgi-services.html">Micro Services vs OSGi
 * services</a></li>
 * <li><a href="http://blog.osgi.org/2010/03/services.html">µServices</a></li>
 * </ul>
 * <br>
 * Hovedoppgåva til tjenesteregisteret er å tilby ein møteplass for tjenestetilbydarar og klientar av tjenester.
 * Tjenestetilbydarane registrerer tjenester dei vil tilby andre tjenestetilbydarar og klientar innanfor samme JVM.
 * Klientane brukar tjenesteregisteret for å få tilgang til dei tjenestene dei er avhengig av på ein enkel og
 * sentralisert måte.
 * <br>
 * Tjenesteregisteret er grunnleggande dynamisk på den måten at tjenester kan blir registrert og avregistrert
 * ved runtime.
 * <br>
 * Tjenester blir registrert i tjenesteregisteret under tjenestegrensesnittet (klassa eller interfacet) som dei ønskjer
 * av klientar skal akksessere tjenesta via. Ein tjenesteinstans kan ligge registrert under fleire forskjellige
 * grensesnitt og på den måten skjule for omverda at den samme tjenesta spelar fleire roller.
 * <br>
 * Kvar tjeneste kan og ha tilhøyrande egenskapar som lar klientane søke opp tjenester som oppfyller visse krav
 * til egenskapar ut over kun kva type grensesnitt dei tilbyr. Ein egenskap er enkle property=value-par som klientane
 * kan filtrere på ved oppslag/søking etter tjenester.
 * <br>
 * For kvar tjenestetype som ligg registrert i registeret kan det vere meir enn ei tjeneste som tilbyr den aktuelle
 * tjenesta. Kvar tjeneste har ein tilhøyrande numerisk ranking som kan brukast i situasjonar der klienten kun
 * ønskjer å bruke den beste/den først implementasjonen av tjenesteypen. Dette gjer det blant anna mulig
 * å ha liggande registrert standard-implementasjonar av tjenester men samtidig tilby muligheita for andre
 * tjenestetilbydarar til å overstyre denne ved å registrere andre implementasjonar av samme tjenestetype men med ein
 * høgare ranking.
 *
 * @author Tarjei Skorgenes
 * @since 1.0.0
 */
public interface ServiceRegistry {
    /**
     * Returnerer ein referanse til den høgast ranka tjenesta som er registrert under {@code tjenestetype}.
     * <br>
     * Merk at referansen ikkje treng å forbli gyldig til evig tid, ved {@link ServiceRegistration#unregister()
     * avregistrering} vil tidligare uthenta referansar bli ugyldige og vil ikkje lenger kunne benyttast
     * mot {@link #getService(ServiceReference)} for å få tak i referansar til sjølve tjenesta.
     *
     * @param <T> tjenestetypen
     * @param tjenestetype grensesnittet for tjenesta som det skal hentast ut referanse til
     * @return ein referanse til den høgast ranka tjenesta for den aktuelle typa , eller {@link Optional#empty()}
     * dersom det ikkje eksisterer noko tjeneste av den angitte typen i tjenesteregisteret
     * @see Constants#SERVICE_RANKING
     */
    <T> Optional<ServiceReference<T>> getServiceReference(Class<T> tjenestetype);

    /**
     * Returnerer ein referanse til den høgast ranka tjenesta som er registrert under {@code tjenestetype}.
     * <br>
     * Merk at referansen ikkje treng å forbli gyldig til evig tid, ved {@link ServiceRegistration#unregister()
     * avregistrering} vil tidligare uthenta referansar bli ugyldige og vil ikkje lenger kunne benyttast
     * mot {@link #getService(ServiceReference)} for å få tak i referansar til sjølve tjenesta.
     * <br>
     * Kvart filter (som tjenesta må ha egenskapar som matchar) består av fritekst som må vere på formatet
     * <code>navn=verdi</code>.
     * <br>
     * Standardtjenesta som blir returnert må matche alle filtra, ikkje berre eit av dei.
     *
     * @param <T> tjenestetypen
     * @param tjenestetype grensesnittet for tjenesta som det skal hentast ut referanse til
     * @param filter eit variabelt antall filter som indikerer kva egenskapar standardtenesta som blir valgt må vere registrert med
     * @return ein referanse til den høgast ranka tjenesta for den aktuelle typa , eller {@link Optional#empty()}
     * dersom det ikkje eksisterer noko tjeneste av den angitte typen i tjenesteregisteret
     * @see Constants#SERVICE_RANKING
     */
    <T> Optional<ServiceReference<T>> getServiceReference(Class<T> tjenestetype, String... filter);

    /**
     * Hentar ut alle tjenester som ligg registrert i tjenesteregisteret under den angitte tjenestetypen.
     * <br>
     * Tjenestene blir lista ut sortert basert på {@link Constants#SERVICE_RANKING ranking}.
     * <br>
     * Merk at referansane ikkje treng å forbli gyldige til evig tid, ved {@link ServiceRegistration#unregister()
     * avregistrering} vil tidligare uthenta referansar bli ugyldige og vil ikkje lenger kunne benyttast
     * mot {@link #getService(ServiceReference)} for å få tak i referansar til sjølve tjenesta.
     *
     * @param <T> tjenestetypen
     * @param tjenestetype tjenestetypen som dei returnerte tjenestereferansane skal vere tilknytta
     * @return alle tjenester som er registrert for tjenestetypen
     */
    <T> List<ServiceReference<T>> getServiceReferences(Class<T> tjenestetype);

    /**
     * /**
     * Hentar ut alle tjenester som ligg registrert i tjenesteregisteret under den angitte tjenestetypen.
     * <br>
     * I motsetning til {@link #getServiceReference(Class)} kan ein her filtrere kva tjenester som blir lista ut
     * basert på egenskapane tjenestene er registrert med.
     * <br>
     * Tjenestene blir lista ut sortert basert på {@link Constants#SERVICE_RANKING ranking}.
     * <br>
     * Merk at referansane ikkje treng å forbli gyldige til evig tid, ved {@link ServiceRegistration#unregister()
     * avregistrering} vil tidligare uthenta referansar bli ugyldige og vil ikkje lenger kunne benyttast
     * mot {@link #getService(ServiceReference)} for å få tak i referansar til sjølve tjenesta.
     * <br>
     * Kvart filter (som tjenestene må ha egenskapar som matchar) består av fritekst som må vere på formatet
     * <code>navn=verdi</code>.
     * <br>
     * Tjenester som blir returnert må matche alle filtra, ikkje berre eit av dei.
     *
     * @param <T> tjenestetypen
     * @param tjenestetype tjenestetypen som dei returnerte tjenestereferansane skal vere tilknytta
     * @param filter eit variabelt antall filter som indikerer kva egenskapar dei utlista tjenestene må vere registrert med
     * @return alle tjenester som matchar alle filter og som er registrert for tjenestetypen
     */
    <T> List<ServiceReference<T>> getServiceReferences(Class<T> tjenestetype, String... filter);

    /**
     * Registrerer tjenesta under den angitte tjenestetypen i tjenesteregisteret.
     * <br>
     * Den returnerte registreringa er privat for klienten/tjenestetilbydaren som registrerer tjenesta og bør ikkje
     * delast med andre.
     * <br>
     * Andre klientar kan søke opp tjenesta via {@link #getServiceReference(Class)},
     * {@link #getServiceReferences(Class)} eller liknande metoder.
     * <br>
     * Egenskapar som tjenesta blir registrert med, er fritekst som må vere på formatet <code>navn=verdi</code>.
     * Navnet på egenskapen kan inneholde alle tegn utanom mellomrom.
     *
     * @param <T> tjenestetypen
     * @param tjenestetype grensesnittet so tjenesta skal registrerast under
     * @param tjeneste tjenesta som skal leggast inn i tjenesteregisteret
     * @param egenskapar inneheld metadata om tjenestas og dens egenskapar, egen
     * @return ei registrering som lar tjenestetilbydaren endre på egenskapane, referere til eller avregistrere tjenesta
     * på eit seinare tidspunkt
     * @throws UgyldigSyntaxException dersom ein eller fleire av egenskapane ikkje er på formatet <code>navn=verdi</code>
     */
    <T> ServiceRegistration<T> registerService(Class<T> tjenestetype, T tjeneste, String... egenskapar);

    /**
     * Kallar {@link #registerService(Class, Object, String...)} med eit tomt sett med egenskapar.
     *
     * @param <T> tjenestetypen
     * @param tjenestetype grensesnittet so tjenesta skal registrerast under
     * @param tjeneste tjenesta som skal leggast inn i tjenesteregisteret
     * @return ei registrering som lar tjenestetilbydaren endre på egenskapane, referere til eller avregistrere tjenesta
     * på eit seinare tidspunkt
     * @see #registerService(Class, Object, String...)
     */
    default <T> ServiceRegistration<T> registerService(final Class<T> tjenestetype, final T tjeneste) {
        return registerService(tjenestetype, tjeneste, new String[0]);
    }

    /**
     * Returnerer tenesteinstansen for tenesta som {@code reference} er tilknytta.
     *
     * @param <T> tjenestetypen
     * @param reference ein indirekte referanse til ei tidligare registrert tjeneste
     * @return tjenesta som referansen er tilknytta, eller {@link Optional#empty()} dersom tjenesta har blitt
     * avregistrert sidan referansen vart henta ut
     * @see ServiceRegistration#unregister()
     */
    <T> Optional<T> getService(ServiceReference<T> reference);
}
