package hu.sch.web.wicket.util;

import hu.sch.domain.Group;
import hu.sch.services.GroupManagerLocal;
import javax.inject.Inject;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 *
 * @author aldaris
 */
public class LoadableDetachableGroupModel extends LoadableDetachableModel<Group> {

    private static final long serialVersionUID = 1L;
    @Inject
    private GroupManagerLocal groupManager;
    private Long groupId;
    private transient Group group;

    public LoadableDetachableGroupModel(Long groupId) {
        this.groupId = groupId;
    }

    public LoadableDetachableGroupModel(Group group) {
        this.group = group;
    }

    @Override
    protected Group load() {
        if (group == null) {
            group = groupManager.findGroupById(groupId);
        }
        return group;
    }
}
