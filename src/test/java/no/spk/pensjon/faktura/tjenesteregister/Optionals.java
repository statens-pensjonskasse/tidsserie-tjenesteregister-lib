package no.spk.pensjon.faktura.tjenesteregister;

import java.util.Optional;
import java.util.stream.Stream;

final class Optionals {
    static Stream<String> stream(Optional<String> o) {
        return o.map(Stream::of).orElse(Stream.empty());
    }
}
