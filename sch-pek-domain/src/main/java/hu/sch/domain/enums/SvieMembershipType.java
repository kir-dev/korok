package hu.sch.domain.enums;

/**
 *
 * @author aldaris
 */
public enum SvieMembershipType {

    NEMTAG("nem tag"),
    PARTOLOTAG("pártoló tag"),
    RENDESTAG("rendes tag");
    private final String name;

    private SvieMembershipType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
