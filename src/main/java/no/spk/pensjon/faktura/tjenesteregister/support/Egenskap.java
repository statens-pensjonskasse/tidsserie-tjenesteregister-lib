package no.spk.pensjon.faktura.tjenesteregister.support;

import java.util.Properties;

import no.spk.pensjon.faktura.tjenesteregister.ServiceReference;
import no.spk.pensjon.faktura.tjenesteregister.UgyldigSyntaxException;

class Egenskap {
    private final String name;
    private final String value;

    private Egenskap(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    static Egenskap parse(final String text) {
        if (!erGyldig(text)) {
            throw new UgyldigSyntaxException(
                    text
                            + " filter er ikkje syntaktisk gyldige, "
                            + "kvart filter må vere på formatet egenskap=verdi.\n"
                            + "Ugyldige filter:\n\t- " + text
            );
        }
        final int firstIndex = text.indexOf("=");
        return new Egenskap(
                text.substring(0, firstIndex),
                text.substring(firstIndex + 1)
        );
    }

    static boolean erGyldig(final String filter) {
        return filter.matches("^[^=]+=.+$");
    }

    boolean match(final ServiceReference<?> reference) {
        return reference.getProperty(name).filter(value::equals).isPresent();
    }

    void put(final Properties egenskapar) {
        egenskapar.setProperty(name, value);
    }
}
