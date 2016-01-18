package no.spk.pensjon.faktura.tjenesteregister;

import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.Test;

public class ServiceLoaderTest {
    /**
     * Verifiserer at applikasjonar som ønskjer å opprette nye tjenesteregister kan instansiere
     * standardimplementasjonen via java sin innebygde {@link ServiceLoader} API.
     */
    @Test
    public void skal_vere_tilgjengelig_via_service_loader() {
        assertThat(load())
                .as("ServiceRegistry-instans lasta frå java.util.ServiceLoader")
                .hasSize(1);
    }

    /**
     * Verifiserer at ServiceLoader ikkje gjennbruker tidligare lasta instansar av tjenesteregisteret.
     */
    @Test
    public void skal_laste_ny_instans_for_kvart_kall() {
        ServiceLoader.load(Object.class);
        assertThat(load())
                .as("ServiceRegistry-instans lasta frå java.util.ServiceLoader")
                .hasSize(1)
                .doesNotContainAnyElementsOf(load())
        ;
    }

    private static List<ServiceRegistry> load() {
        return stream(ServiceLoader.load(ServiceRegistry.class).iterator())
                .collect(toList());
    }

    private static Stream<ServiceRegistry> stream(final Iterator<ServiceRegistry> i) {
        return StreamSupport.stream(
                spliteratorUnknownSize(i, Spliterator.NONNULL),
                false
        );
    }
}
