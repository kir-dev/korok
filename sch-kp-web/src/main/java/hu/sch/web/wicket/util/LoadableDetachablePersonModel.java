package hu.sch.web.wicket.util;

import hu.sch.domain.profile.Person;
import hu.sch.services.LdapManagerLocal;
import hu.sch.services.exceptions.PersonNotFoundException;
import javax.ejb.EJB;
import org.apache.log4j.Logger;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 *
 * @author aldaris
 */
public class LoadableDetachablePersonModel extends LoadableDetachableModel<Person> {

    private static Logger logger = Logger.getLogger(LoadableDetachablePersonModel.class);
    @EJB(name = "LdapManagerBean")
    LdapManagerLocal ldapManager;
    private String uid;

    public LoadableDetachablePersonModel(String uid) {
        this.uid = uid;
        init();
    }

    public LoadableDetachablePersonModel(Person p) {
        uid = p.getUid();
        init();
    }

    private void init() {
        InjectorHolder.getInjector().inject(this);
    }

    @Override
    protected Person load() {
        Person ret = null;
        try {
            ret = ldapManager.getPersonByUid(uid);
        } catch (PersonNotFoundException pnfe) {
            logger.error("Unable to find User with uid: " + uid, pnfe);
        }
        return ret;
    }
}
