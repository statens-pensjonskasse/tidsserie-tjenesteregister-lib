package no.spk.pensjon.faktura.tjenesteregister;

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
 */
public interface ServiceRegistry {
}
