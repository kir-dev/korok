/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.ejb.test.builder;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.user.User;
import java.util.Date;

/**
 *
 * @author tomi
 */
public class MembershipBuilder extends AbstractBuilder<Membership> {

    private User user;
    private Group group;
    private Date start;
    private Date end;

    public MembershipBuilder() {
        user = new UserBuilder().build();
        group = new GroupBuilder().build();
        start = new Date();
        end = null;
    }

    public MembershipBuilder withUser(User user) {
        this.user = user;
        return this;
    }

    public MembershipBuilder withGroup(Group group) {
        this.group = group;
        return this;
    }

    public MembershipBuilder withStart(Date date) {
        start = date;
        return this;
    }

    public MembershipBuilder withEnd(Date date) {
        end = date;
        return this;
    }

    @Override
    public Membership build() {
        Membership ms = new Membership();
        ms.setStart(start);
        ms.setEnd(end);
        ms.setUser(user);
        ms.setGroup(group);
        return ms;
    }
}
