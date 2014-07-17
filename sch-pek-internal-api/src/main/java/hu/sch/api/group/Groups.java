package hu.sch.api.group;

import hu.sch.domain.Group;
import hu.sch.services.GroupManagerLocal;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("groups")
public class Groups {

    private GroupManagerLocal groupManager;

    @GET
    public List<GroupView> getAll() {
        List<Group> groups = groupManager.getAllGroups();
        return GroupView.fromCollection(groups);
    }

    @GET
    @Path("{id}")
    public GroupView getById(@PathParam("id") Long id) {
        return new GroupView(groupManager.findGroupById(id));
    }

    @Inject
    public void setGroupManager(GroupManagerLocal groupManager) {
        this.groupManager = groupManager;
    }
}
