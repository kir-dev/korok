package hu.sch.web.wicket.util;

import hu.sch.domain.Group;
import hu.sch.services.UserManagerLocal;
import javax.ejb.EJB;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 *
 * @author aldaris
 */
public class LoadableDetachableGroupModel extends LoadableDetachableModel<Group> {

    @EJB(name = "UserManagerBean")
    private UserManagerLocal userManager;
    private Long groupId;

    public LoadableDetachableGroupModel(Long groupId) {
        this.groupId = groupId;
        init();
    }

    public LoadableDetachableGroupModel(Group group) {
        this.groupId = group.getId();
        init();
    }

    private void init() {
        InjectorHolder.getInjector().inject(this);
    }

    @Override
    protected Group load() {
        return userManager.findGroupById(groupId);
    }
}
