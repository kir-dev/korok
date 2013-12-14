package hu.sch.web.rest.dto;

import hu.sch.domain.user.User;

/**
 *
 * @author balo
 */
public class ProfileResult {

    private final String uid;
    private final long virid;
    private final String neptun;
    private final String fullName;
    private final String nick;
    private final String email;

    public ProfileResult(User user) {
        uid = user.getScreenName();
        virid = user.getId();
        neptun = user.getNeptunCode();
        fullName = user.getFullName();
        nick = user.getNickName();
        email = user.getEmailAddress();
    }

    public String getUid() {
        return uid;
    }

    public long getVirid() {
        return virid;
    }

    public String getNeptun() {
        return neptun;
    }

    public String getFullName() {
        return fullName;
    }

    public String getNick() {
        return nick;
    }

    public String getEmail() {
        return email;
    }

}
