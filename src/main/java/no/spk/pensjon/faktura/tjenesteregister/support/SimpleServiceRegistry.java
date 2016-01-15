package no.spk.pensjon.faktura.tjenesteregister.support;

import static java.util.Arrays.asList;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static no.spk.pensjon.faktura.tjenesteregister.Constants.SERVICE_RANKING;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import no.spk.pensjon.faktura.tjenesteregister.ServiceReference;
import no.spk.pensjon.faktura.tjenesteregister.ServiceRegistration;
import no.spk.pensjon.faktura.tjenesteregister.ServiceRegistry;
import no.spk.pensjon.faktura.tjenesteregister.UgyldigSyntaxException;

public class SimpleServiceRegistry implements ServiceRegistry {
    private static final int DEFAULT_RANKING = 0;

    private final Map<Class<?>, List<ServiceEntry<?>>> services = new HashMap<>();

    @Override
    public <T> Optional<ServiceReference<T>> getServiceReference(final Class<T> tjenestetype) {
        return referencesFor(tjenestetype).findFirst();
    }

    @Override
    public <T> Optional<ServiceReference<T>> getServiceReference(final Class<T> tjenestetype, final String... filter) {
        return getServiceReferences(tjenestetype, filter)
                .stream()
                .findFirst();
    }

    @Override
    public <T> List<ServiceReference<T>> getServiceReferences(final Class<T> tjenestetype) {
        return referencesFor(tjenestetype).collect(toList());
    }

    @Override
    public <T> List<ServiceReference<T>> getServiceReferences(final Class<T> tjenestetype, final String... filter) {
        return getServiceReferences(tjenestetype)
                .stream()
                .filter(r -> matchAll(r, asList(filter)))
                .collect(toList());
    }

    @Override
    public <T> Optional<T> getService(final ServiceReference<T> reference) {
        return entryFor(reference).map(ServiceEntry::service);
    }

    @Override
    public <T> ServiceRegistration<T> registerService(final Class<T> tjenestetype, final T tjeneste, final String... egenskapar) {
        return newEntry(
                tjenestetype,
                tjeneste,
                konverter(asList(egenskapar))
                        .reduce(
                                Egenskapar.TOM,
                                Egenskapar::plus,
                                Egenskapar::kombiner
                        )
                        .toProperties()
        )
                .registerWith(entriesFor(tjenestetype));
    }

    private <T> boolean matchAll(final ServiceReference<T> reference, final List<String> filters) {
        final Predicate<String> erGyldig = Egenskap::erGyldig;
        final List<String> ugyldig = filters
                .stream()
                .filter(erGyldig.negate())
                .collect(toList());
        if (!ugyldig.isEmpty()) {
            throw new UgyldigSyntaxException(
                    ugyldig
                            + " filter er ikkje syntaktisk gyldige, "
                            + "kvart filter må vere på formatet egenskap=verdi.\n"
                            + "Ugyldige filter:\n"
                            + ugyldig
                            .stream()
                            .map(filter -> "\t- " + filter)
                            .collect(joining("\n"))
            );
        }

        return konverter(filters)
                .map(filter -> filter.match(reference))
                .reduce((a, b) -> a && b)
                .orElse(false);
    }

    private static Stream<Egenskap> konverter(final List<String> filters) {
        return filters
                .stream()
                .map(Egenskap::parse);
    }

    private <T> ServiceEntry<T> newEntry(final Class<T> tjenestetype, final T tjeneste, final Properties egenskapar) {
        return new ServiceEntry<>(
                this,
                tjenestetype,
                tjeneste,
                egenskapar
        );
    }

    @SuppressWarnings("unchecked")
    private <T> Stream<ServiceReference<T>> referencesFor(final Class<T> tjenestetype) {
        return entriesFor(tjenestetype)
                .stream()
                .map(e -> (ServiceEntry<T>) e)
                .sorted(orderByRanking())
                .map(ServiceEntry::getReference);
    }

    private <T> Optional<ServiceEntry<T>> entryFor(final ServiceReference<T> reference) {
        return ofNullable(reference)
                .filter(r -> r instanceof ServiceEntry)
                .map(r -> (ServiceEntry<T>) r);
    }

    private <T> List<ServiceEntry<?>> entriesFor(final Class<T> tjenestetype) {
        return services.computeIfAbsent(
                tjenestetype,
                ignore -> new ArrayList<>()
        );
    }

    private void remove(final ServiceEntry<?> entry) {
        services.computeIfPresent(entry.tjenestetype, (type, entries) -> {
            entries.remove(entry);
            return entries;
        });
    }

    private static Comparator<ServiceEntry<?>> orderByRanking() {
        return Comparator.<ServiceEntry<?>, Integer>comparing(entry -> of(entry)
                .flatMap(e -> e.getProperty(SERVICE_RANKING))
                .map(Integer::parseInt)
                .orElse(DEFAULT_RANKING)
        )
                .reversed();
    }

    private static class ServiceEntry<T> implements ServiceRegistration<T>, ServiceReference<T> {
        private final Properties egenskapar = new Properties();
        private final SimpleServiceRegistry parent;
        private final Class<T> tjenestetype;
        private final T tjeneste;

        ServiceEntry(final SimpleServiceRegistry parent, final Class<T> tjenestetype, final T tjeneste, final Properties egenskapar) {
            this.parent = parent;
            this.tjenestetype = tjenestetype;
            this.tjeneste = tjeneste;
            this.egenskapar.putAll(egenskapar);

            if (!egenskapar.containsKey(SERVICE_RANKING)) {
                this.egenskapar.setProperty(SERVICE_RANKING, Integer.toString(DEFAULT_RANKING));
            }
        }

        ServiceRegistration<T> registerWith(final List<ServiceEntry<?>> entries) {
            entries.add(this);
            return this;
        }

        T service() {
            return tjeneste;
        }

        @Override
        public ServiceReference<T> getReference() {
            return this;
        }

        @Override
        public void unregister() {
            parent.remove(this);
        }

        @Override
        public Optional<String> getProperty(final String name) {
            return of(name).map(egenskapar::getProperty);
        }

        @Override
        public String toString() {
            return "tjeneste " + tjeneste + ", egenskapar: " + egenskapar;
        }
    }
}
