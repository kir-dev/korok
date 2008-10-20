package hu.sch.kp.ejb;

import hu.sch.domain.Csoport;
import hu.sch.domain.Felhasznalo;
import hu.sch.kp.services.UserManagerLocal;
import hu.sch.kp.services.UserManagerRemote;
import hu.sch.kp.services.exceptions.GroupAlreadyExistsException;
import hu.sch.kp.services.exceptions.UserAlreadyExistsException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Adam Lantos
 */
@Stateless(mappedName = "UserManager")
public class UserManagerForRemote implements UserManagerRemote {

    @EJB()
    UserManagerLocal userManager;

    public Felhasznalo saveOrAddUser(Felhasznalo user) throws
            UserAlreadyExistsException,
            RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Felhasznalo findUserById(Long userId) throws RemoteException {
        Felhasznalo f = userManager.findUserById(userId);
        if (f == null) {
            return null;
        }

        Felhasznalo f2 = new Felhasznalo();
        f2.setId(f.getId());
        f2.setBecenev(f.getBecenev());
        f2.setVezeteknev(f.getVezeteknev());
        f2.setKeresztnev(f.getKeresztnev());
        f2.setNeptunkod(f.getNeptunkod());
        f2.setEmailcim(f.getEmailcim());

        return f2;
    }

    public void addUserToGroup(Felhasznalo user, Csoport group, Date membership_start, Date membership_end)
            throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Csoport saveOrAddGroup(Csoport group) throws
            GroupAlreadyExistsException,
            RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void modifyMembership(Felhasznalo user, Csoport group, Date start, Date end)
            throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteMembership(Felhasznalo user, Csoport group) throws
            RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Felhasznalo> getCsoporttagok(Long csoportId) throws
            RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
