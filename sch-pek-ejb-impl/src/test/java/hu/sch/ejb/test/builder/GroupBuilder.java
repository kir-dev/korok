/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.ejb.test.builder;

import hu.sch.domain.Group;
import hu.sch.domain.GroupStatus;

/**
 *
 * @author tomi
 */
public class GroupBuilder extends AbstractBuilder<Group> {

    private String name = "groupX";
    private String type = "test";
    private GroupStatus status = GroupStatus.akt;
    private Boolean isSvie = Boolean.TRUE;
    private Boolean usersCanApply = Boolean.TRUE;

    public GroupBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public GroupBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public GroupBuilder withStatus(GroupStatus status) {
        this.status = status;
        return this;
    }

    @Override
    public Group build() {
        Group g = new Group();
        g.setType(type);
        g.setName(name);
        g.setStatus(status);
        g.setIsSvie(isSvie);
        g.setUsersCanApply(usersCanApply);
        return g;
    }

}
