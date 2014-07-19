package hu.sch.api.group;

import hu.sch.domain.Group;
import hu.sch.domain.enums.GroupStatus;
import java.util.Collection;
import java.util.List;
import static java.util.stream.Collectors.toList;

public class GroupView {

    private final Group group;

    public GroupView(Group group) {
        this.group = group;
    }

    public static List<GroupView> fromCollection(Collection<Group> groups) {
        return groups.stream().map(g->new GroupView(g)).collect(toList());
    }

    public Long getId() {
        return group.getId();
    }

    public String getName() {
        return group.getName();
    }

    public String getType() {
        return group.getType();
    }

    public GroupStatus getStatus() {
        return group.getStatus();
    }

    public String getIntroduction() {
        return group.getIntroduction();
    }

    public String getWebPage() {
        return group.getWebPage();
    }

    public String getMailingList() {
        return group.getMailingList();
    }

    public String getHead() {
        return group.getHead();
    }

    public Integer getFounded() {
        return group.getFounded();
    }

    public Boolean getIsSvie() {
        return group.getIsSvie();
    }

    public Integer getDelegateNumber() {
        return group.getDelegateNumber();
    }

    public boolean getUsersCanApply() {
        return group.getUsersCanApply();
    }


}
