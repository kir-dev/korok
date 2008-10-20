package hu.sch.kp.ejb;

import hu.sch.domain.Felhasznalo;
import hu.sch.kp.services.UserManagerLocal;
import hu.sch.kp.services.UserManagerRemote;
import hu.sch.kp.services.exceptions.GroupAlreadyExistsException;
import java.rmi.RemoteException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Adam Lantos
 */
@Stateless(mappedName = "UserManager")
public class UserManagerForRemote implements UserManagerRemote {

    @EJB()
    UserManagerLocal userManager;
    @PersistenceContext
    EntityManager em;

    public Long createUser(String firstName, String lastName, String nickName)
            throws RemoteException {
        
        Felhasznalo f = new Felhasznalo();
        f.setBecenev(nickName);
        f.setKeresztnev(firstName);
        f.setVezeteknev(lastName);
        em.persist(f);
        em.flush();
        
        return f.getId();
    }

    public void changeUser(Long userId, String firstName, String lastName, String nickName)
            throws RemoteException {
        
        Felhasznalo f = em.find(Felhasznalo.class, userId);
        f.setBecenev(nickName);
        f.setVezeteknev(lastName);
        f.setKeresztnev(firstName);
        
        em.merge(f);
    }

    public void createActiveMembership(Long userId, Long groupId) throws 
            RemoteException {

        /*
        Felhasznalo f = new Felhasznalo();
        f.setId(userId);
        Csoport cs = new Csoport();
        cs.setId(groupId);

        userManager.addUserToGroup(f, cs, new Date(), null);*/
        
        throw new UnsupportedOperationException("Not supported yet.");
        
    }

    public void inactivateMembership(Long userId, Long groupId) throws 
            RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Long createGroup(String groupName, Long parentGroupId) throws 
            GroupAlreadyExistsException,
            RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
