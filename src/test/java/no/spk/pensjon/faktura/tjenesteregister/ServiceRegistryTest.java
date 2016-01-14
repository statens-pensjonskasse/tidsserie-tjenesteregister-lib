package no.spk.pensjon.faktura.tjenesteregister;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import no.spk.pensjon.faktura.tjenesteregister.support.SimpleServiceRegistry;

import org.assertj.core.api.OptionalAssert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ServiceRegistryTest {
    @Rule
    public final ExpectedException e = ExpectedException.none();

    private SimpleServiceRegistry registry;

    @Before
    public void _before() {
        registry = new SimpleServiceRegistry();
    }

    @Test
    public void skal_faa_tilbake_samme_instans_som_vart_registrert() {
        final String expected = "I AM MYSELF";
        assertTjeneste(
                registry.registerService(
                        String.class,
                        expected
                )
                        .getReference()
        )
                .isEqualTo(of(expected));
    }

    @Test
    public void skal_fjerne_tenesta_ved_avregistrering() {
        registry.registerService(String.class, "I WAS HERE BUT NO MORE", new Properties()).unregister();
        assertStandardtenesteForType(String.class).isEmpty();
    }

    @Test
    public void skal_ikkje_finne_noka_tjeneste_dersom_ingen_har_registrert_noko_for_tjenestetypen() {
        assertThat(registry.getServiceReference(Object.class))
                .as("tjenesterefeanse for tjenestetype " + Object.class.getSimpleName())
                .isEmpty();
    }

    @Test
    public void skal_ikkje_finne_noka_tjeneste_dersom_ingen_har_registrert_noko_for_tjenestetypen_og_som_matchar_filter() {
        final Properties egenskapar = new Properties();
        egenskapar.setProperty("key","value");
        registry.registerService(String.class, "I AM VALUE", egenskapar);

        assertThat(registry.getServiceReference(String.class, "key=othervalue"))
                .as("tjenesterefeanse for tjenestetype " + String.class.getSimpleName())
                .isEmpty();
    }

    @Test
    public void skal_registrere_tenester_som_ikkje_angir_ranking_med_ranking_0() {
        final ServiceRegistration<String> registration = registry.registerService(String.class, "I HAVE NO RANK", new Properties());
        assertThat(registration.getReference().getProperty(Constants.SERVICE_RANKING))
                .as("service ranking")
                .isEqualTo(of("0"));
    }

    @Test
    public void skal_kopiere_egenskapane_ved_registrering() {
        final Properties egenskapar = new Properties();
        final ServiceRegistration<String> registration = registry.registerService(String.class, "I HAVE NO RANK", egenskapar);
        egenskapar.setProperty("YADA", "LOL");

        assertThat(registration.getReference().getProperty("YADA"))
                .as("YADA-egenskapen skal ikkje vere satt sidan egenskapar-instansen vart endra etter registrering")
                .isEmpty();
    }

    @Test
    public void skal_lokalisere_den_hoegast_ranka_tenesta_som_standardteneste() {
        final String expected = "BY THE POWER OF GREYSKULL, I AM THE DEFAULT!";

        final Properties egenskapar = new Properties();
        egenskapar.setProperty(Constants.SERVICE_RANKING, "10");
        registry.registerService(String.class, "BY THE POWER OF NOTHING, I AM NO DEFAULT :(", egenskapar);

        final Properties egenskapar2 = new Properties();
        egenskapar2.setProperty(Constants.SERVICE_RANKING, "1000");
        registry.registerService(String.class, expected, egenskapar2);

        assertStandardtenesteForType(String.class)
                .isEqualTo(
                        of(expected)
                );
    }

    @Test
    public void skal_faa_ut_alle_tenester_for_typen_viss_fleire_er_registrert() {
        registry.registerService(String.class, "1", new Properties());
        registry.registerService(String.class, "2", new Properties());

        assertThat(registry.getServiceReferences(String.class))
                .as("referansar for tenestetype " + String.class)
                .hasSize(2);
    }

    @Test
    public void skal_kunne_filtrere_tenester_paa_egenskap_ved_uthenting_av_mange_referansar() {
        final Properties egenskapar = new Properties();
        egenskapar.setProperty("katalog", "currentDir");
        registry.registerService(Path.class, Paths.get("."), egenskapar);

        final Properties egenskapar2 = new Properties();
        egenskapar2.setProperty("katalog", "tmpDir");
        registry.registerService(Path.class, Paths.get("/tmp"), egenskapar2);

        assertThat(
                registry
                        .getServiceReferences(
                                Path.class,
                                "katalog=currentDir"
                        )
        )
                .hasSize(1);
    }

    @Test
    public void skal_kunne_filtrere_tenester_paa_egenskap_ved_uthenting_av_standardteneste() {
        final Properties egenskapar = new Properties();
        egenskapar.setProperty("katalog", "currentDir");
        registry.registerService(Path.class, Paths.get("."), egenskapar);

        final Properties egenskapar2 = new Properties();
        egenskapar2.setProperty("katalog", "tmpDir");
        registry.registerService(Path.class, Paths.get("/tmp"), egenskapar2);

        assertThat(
                registry
                        .getServiceReference(
                                Path.class,
                                "katalog=currentDir"
                        )
                        .flatMap(registry::getService)
        )
                .isEqualTo(of(Paths.get(".")));
    }

    @Test
    public void skal_feile_paa_ugyldige_filter() {
        e.expect(UgyldigSyntaxException.class);

        registry.registerService(String.class, "HELLO");

        registry.getServiceReferences(String.class, "yada yada");
    }

    @Test
    public void skal_beholde_erliktegn_etter_foerste_erlik_som_skiller_navn_fra_verdi_i_filter() {
        final Properties egenskapar = new Properties();
        egenskapar.setProperty("yada", "=yada");

        final ServiceRegistration<String> registration = registry.registerService(
                String.class,
                "HEI",
                egenskapar
        );
        assertThat(
                registry.getServiceReference(String.class, "yada==yada")
        )
                .isEqualTo(
                        of(
                                registration.getReference()
                        )
                );
    }

    private <T> OptionalAssert<T> assertTjeneste(final ServiceReference<T> referanse) {
        return assertThat(
                ofNullable(referanse).flatMap(registry::getService)
        )
                .as("tjenesta registrert under " + referanse);
    }

    private <T> OptionalAssert<T> assertStandardtenesteForType(final Class<T> type) {
        return assertThat(
                registry.getServiceReference(type).flatMap(registry::getService)
        )
                .as("standardtenesta av tjenestene registrert under " + type.getSimpleName());
    }
}
