package hu.sch.web.wicket.util;

import hu.sch.domain.Group;
import hu.sch.services.UserManagerLocal;
import javax.ejb.EJB;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 *
 * @author aldaris
 */
public class LoadableDetachableGroupModel extends LoadableDetachableModel<Group> {

    private static final long serialVersionUID = 1L;
    @EJB(name = "UserManagerBean")
    private UserManagerLocal userManager;
    private Long groupId;
    private transient Group group;

    public LoadableDetachableGroupModel(Long groupId) {
        this.groupId = groupId;
        init();
    }

    public LoadableDetachableGroupModel(Group group) {
        this.group = group;
        init();
    }

    private void init() {
        Injector.get().inject(this);
    }

    @Override
    protected Group load() {
        if (group == null) {
            group = userManager.findGroupById(groupId);
        }
        return group;
    }
}
