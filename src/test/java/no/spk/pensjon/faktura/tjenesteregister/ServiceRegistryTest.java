package no.spk.pensjon.faktura.tjenesteregister;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

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
        registry.registerService(String.class, "I WAS HERE BUT NO MORE").unregister();
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
        registry.registerService(String.class, "I AM VALUE", "key=value");

        assertThat(registry.getServiceReference(String.class, "key=othervalue"))
                .as("tjenesterefeanse for tjenestetype " + String.class.getSimpleName())
                .isEmpty();
    }

    @Test
    public void skal_soeke_opp_kun_tenester_som_matchar_alle_filter_ikkje_bere_nokon_av_dei() {
        final String expected = "I AM HEROES";
        registry.registerService(String.class, expected, "a=b", "c=d");
        registry.registerService(String.class, "I'M NO HERO", "a=b", "c=x");
        registry.registerService(String.class, "HEROES, WHERE?", "a=y", "c=d");

        assertThat(
                registry
                        .getServiceReferences(
                                String.class,
                                "a=b",
                                "c=d"
                        )
                        .stream()
                        .map(registry::getService)
                        .flatMap(Optionals::stream)
                        .collect(toList())
        )
                .hasSize(1)
                .contains(expected);

        assertThat(
                registry
                        .getServiceReference(String.class, "a=b", "c=d")
                        .flatMap(registry::getService)

        )
                .isEqualTo(
                        of(expected)
                );
    }

    @Test
    public void skal_ikkje_godta_ugyldig_formaterte_egenskapar_ved_registrering() {
        e.expect(UgyldigSyntaxException.class);

        registry.registerService(Object.class, new Object(), "I don't care about formating");
    }

    @Test
    public void skal_registrere_tenester_som_ikkje_angir_ranking_med_ranking_0() {
        final ServiceRegistration<String> registration = registry.registerService(String.class, "I HAVE NO RANK");
        assertThat(registration.getReference().getProperty(Constants.SERVICE_RANKING))
                .as("service ranking")
                .isEqualTo(of("0"));
    }

    @Test
    public void skal_kopiere_egenskapane_ved_registrering() {
        final String[] egenskapar = {"ABCD=EFGH"};
        final ServiceRegistration<String> registration = registry.registerService(String.class, "I HAVE NO RANK", egenskapar);
        egenskapar[0] = "YADA=LOL";

        assertThat(registration.getReference().getProperty("YADA"))
                .as("YADA-egenskapen skal ikkje vere satt sidan egenskapar-instansen vart endra etter registrering")
                .isEmpty();
    }

    @Test
    public void skal_lokalisere_den_hoegast_ranka_tenesta_som_standardteneste() {
        final String expected = "BY THE POWER OF GREYSKULL, I AM THE DEFAULT!";

        registry.registerService(String.class, "BY THE POWER OF NOTHING, I AM NO DEFAULT :(", ranking(10));

        registry.registerService(String.class, expected, Constants.SERVICE_RANKING + "=1000");

        assertStandardtenesteForType(String.class)
                .isEqualTo(
                        of(expected)
                );
    }

    @Test
    public void skal_faa_ut_alle_tenester_for_typen_viss_fleire_er_registrert() {
        registry.registerService(String.class, "1");
        registry.registerService(String.class, "2");

        assertThat(registry.getServiceReferences(String.class))
                .as("referansar for tenestetype " + String.class)
                .hasSize(2);
    }

    @Test
    public void skal_kunne_filtrere_tenester_paa_egenskap_ved_uthenting_av_mange_referansar() {
        registry.registerService(Path.class, Paths.get("."), "katalog=currentDir");
        registry.registerService(Path.class, Paths.get("/tmp"), "katalog=tmpDir");

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
        registry.registerService(Path.class, Paths.get("."), "katalog=currentDir");
        registry.registerService(Path.class, Paths.get("/tmp"), "katalog=tmpDir");

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
        final ServiceRegistration<String> registration = registry.registerService(
                String.class,
                "HEI",
                "yada=" + "=yada"
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

    private static String ranking(final int ranking) {
        return Constants.SERVICE_RANKING + "=" + ranking;
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
