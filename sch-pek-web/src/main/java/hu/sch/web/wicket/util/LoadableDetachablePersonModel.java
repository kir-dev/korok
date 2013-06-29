package hu.sch.web.wicket.util;

import hu.sch.domain.profile.Person;
import hu.sch.services.LdapManagerLocal;
import hu.sch.services.exceptions.PersonNotFoundException;
import javax.ejb.EJB;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aldaris
 */
public class LoadableDetachablePersonModel extends LoadableDetachableModel<Person> {

    private static final long serialVersionUID = 1L;
    private static Logger logger = LoggerFactory.getLogger(LoadableDetachablePersonModel.class);
    @EJB(name = "LdapManagerBean")
    LdapManagerLocal ldapManager;
    private transient Person person = null;
    private String uid;

    public LoadableDetachablePersonModel(Person p) {
        person = p;
        uid = p.getUid();
        init();
    }

    private void init() {
        Injector.get().inject(this);
    }

    @Override
    protected Person load() {
        if (person != null) {
            return person;
        }

        try {
            person = ldapManager.getPersonByUid(uid);
        } catch (PersonNotFoundException pnfe) {
            logger.error("Unable to find User with uid: " + uid, pnfe);
        }
        return person;
    }
}
