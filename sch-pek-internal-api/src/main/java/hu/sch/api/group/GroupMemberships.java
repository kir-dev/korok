package hu.sch.api.group;

import hu.sch.services.MembershipManagerLocal;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("groups/{id}/memberships")
public class GroupMemberships {

    private MembershipManagerLocal membershipManager;

    @PathParam("id")
    private Long id;

    @Path("active")
    @GET
    public List<GroupMembershipView> getActiveMemberships() {
        return GroupMembershipView.fromCollection(membershipManager.findActiveMembershipsForGroup(id));
    }


    // öregtagságok
    @GET
    @Path("inactive")
    public List<GroupMembershipView> getInactiveMemberships() {
        return GroupMembershipView.fromCollection(membershipManager.findInactiveMembershipsForGroup(id));
    }

    @Inject
    public void setMembershipManager(MembershipManagerLocal membershipManager) {
        this.membershipManager = membershipManager;
    }


}
