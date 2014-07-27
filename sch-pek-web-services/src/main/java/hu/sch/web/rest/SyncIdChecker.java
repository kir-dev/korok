package hu.sch.web.rest;

import hu.sch.util.PatternHolder;
import java.util.UUID;

public class SyncIdChecker {

    public static Type check(String id) {
        return new SyncIdChecker().checkId(id);
    }

    public Type checkId(String id) {
        if (checkUUID(id)) {
            return Type.AUTH_SCH_ID;
        }

        if (checkNeptun(id)) {
            return Type.NEPTUN;
        }

        return Type.NONE;
    }

    private boolean checkUUID(String id) {
        try {
            UUID.fromString(id);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private boolean checkNeptun(String id) {
        return PatternHolder.NEPTUN_PATTERN.matcher(id).matches();
    }

    public enum Type {

        NEPTUN, AUTH_SCH_ID, NONE
    }
}
