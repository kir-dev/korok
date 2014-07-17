package hu.sch.api.group;

import hu.sch.domain.Membership;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

class GroupMembershipView {

    private final Membership membership;

    public GroupMembershipView(Membership membership) {
        this.membership = membership;
    }

    public static List<GroupMembershipView> fromCollection(Collection<Membership> memberships) {
        return memberships.stream().map(ms -> new GroupMembershipView(ms)).collect(Collectors.toList());
    }

    public String getUserFullname() {
        return membership.getUser().getFullName();
    }

    public Long getId() {
        return membership.getId();
    }

    public List<String> getPosts() {
        return membership.getAllPosts();
    }

    public Date getStart() {
        return membership.getStart();
    }

    public Date getEnd() {
        return membership.getEnd();
    }
}
