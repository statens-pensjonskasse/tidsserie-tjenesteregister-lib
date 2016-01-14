package no.spk.pensjon.faktura.tjenesteregister.support;

import no.spk.pensjon.faktura.tjenesteregister.ServiceReference;
import no.spk.pensjon.faktura.tjenesteregister.UgyldigSyntaxException;

class EgenskapFilter {
    private final String name;
    private final String value;

    private EgenskapFilter(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    static EgenskapFilter parse(final String text) {
        if (!erGyldig(text)) {
            throw new UgyldigSyntaxException(
                    text
                            + " filter er ikkje syntaktisk gyldige, "
                            + "kvart filter må vere på formatet egenskap=verdi.\n"
                            + "Ugyldige filter:\n\t- " + text
            );
        }
        final int firstIndex = text.indexOf("=");
        return new EgenskapFilter(
                text.substring(0, firstIndex),
                text.substring(firstIndex + 1)
        );
    }

    static boolean erGyldig(final String filter) {
        return filter.matches("^[a-zA-Z]+=.+$");
    }

    boolean match(final ServiceReference<?> reference) {
        return reference.getProperty(name).filter(value::equals).isPresent();
    }
}
