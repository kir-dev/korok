package hu.sch.domain.enums;

/**
 *
 * @author aldaris
 */
public enum SvieStatus {

    NEMTAG("nem tag"),
    FELDOLGOZASALATT("feldolgozás alatt"),
    ELFOGADASALATT("elfogadás alatt"),
    ELFOGADVA("elfogadva");
    private String name;

    private SvieStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
