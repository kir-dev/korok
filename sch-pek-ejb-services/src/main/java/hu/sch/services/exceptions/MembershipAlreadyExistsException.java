package hu.sch.services.exceptions;

import hu.sch.domain.Group;
import hu.sch.domain.user.User;

/**
 *
 * @author messo
 */
public class MembershipAlreadyExistsException extends Exception {
    private final Group group;
    private final User user;

    public MembershipAlreadyExistsException(Group group, User user) {
        super("A felhasználó már tagja a körnek!");
        this.group = group;
        this.user = user;
    }
}
