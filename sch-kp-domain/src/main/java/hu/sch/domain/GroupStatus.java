package hu.sch.domain;

/**
 * @author hege
 */
public enum GroupStatus {

    akt("aktív"), old("öreg");
    private final String name;

    private GroupStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
