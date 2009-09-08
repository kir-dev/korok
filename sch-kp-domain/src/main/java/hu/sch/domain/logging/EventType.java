/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.domain.logging;

/**
 *
 * @author aldaris
 */
public enum EventType {

    JELENTKEZES("Körbe jelentkezett:\n"),
    TAGSAGTORLES("Kilépett körtagok:\n"),
    SVIE_JELENTKEZES("SVIE-be jelentkeztek:\n"),
    SVIE_TAGSAGTORLES("SVIE-ből kiléptek:\n"),
    PARTOLOVAVALAS("Rendes tagból pártoló taggá vált:\n"),
    RENDESTAGGAVALAS("Pártoló tagból rendes taggá vált:\n"),
    ELFOGADASALATT("Elfogadás alatt állapotúak:\n");
    private String name;

    private EventType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
