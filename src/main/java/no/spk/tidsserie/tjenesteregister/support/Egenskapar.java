package no.spk.tidsserie.tjenesteregister.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

class Egenskapar {
    static final Egenskapar TOM = new Egenskapar(Stream.empty());

    private final List<Egenskap> egenskapar = new ArrayList<>();

    Egenskapar(final Stream<Egenskap> egenskapar) {
        egenskapar.forEach(this.egenskapar::add);
    }

    Egenskapar plus(final Egenskap other) {
        return new Egenskapar(
                Stream.concat(
                        egenskapar.stream(),
                        Stream.of(other)
                )
        );
    }

    static Egenskapar kombiner(final Egenskapar a, final Egenskapar b) {
        return new Egenskapar(
                Stream.concat(
                        a.egenskapar.stream(),
                        b.egenskapar.stream()
                )
        );
    }

    Properties toProperties() {
        final Properties tmp = new Properties();
        this.egenskapar.forEach(e -> e.put(tmp));
        return tmp;
    }
}
