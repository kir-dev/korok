package hu.sch.api.user;

import hu.sch.api.response.AbstractEntityView;
import hu.sch.domain.Membership;
import hu.sch.domain.util.MembershipSorter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author tomi
 */
public class MembershipView extends AbstractEntityView<Membership> {



    public MembershipView(Membership entity) {
        super(entity, Membership.class);
    }

    public static List<MembershipView> fromCollection(Collection<Membership> collection) {
        MembershipSorter sorter = new MembershipSorter(collection);
        return sorter.sort()
                .stream()
                .map(ms -> new MembershipView(ms))
                .collect(Collectors.toList());
    }

    public Long getId() {
        return entity.getId();
    }

    public String getGroupName() {
        return entity.getGroup().getName();
    }

    public Date getStart() {
        return entity.getStart();
    }

    public Date getEnd() {
        return entity.getEnd();
    }

    public List<String> getPosts() {
        return entity.getAllPosts();
    }
}
