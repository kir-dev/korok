package hu.sch.web.idm.pages.wizard;

/**
 *
 * @author balo
 */
enum RegistrationMode {

    ACTIVE_WITH_NEPTUN_CODE("reg.mode.active.neptun", false),
    NEWBIE_WITH_NEPTUN_CODE("reg.mode.newbie.neptun", true),
    NEWBIE_WITH_OM_CODE("reg.mode.newbie.om", true);
    private String name;
    private boolean newbie;

    private RegistrationMode(final String name, final boolean newbie) {
        this.name = name;
        this.newbie = newbie;
    }

    public boolean isNewbie() {
        return newbie;
    }

    @Override
    public String toString() {
        return name;
    }
}
