package hu.sch.ejb;

import hu.sch.domain.user.IMAccount;
import hu.sch.domain.user.User;
import hu.sch.services.IMAccountManager;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.exceptions.EntityNotFoundException;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class IMAccountManagerBean implements IMAccountManager {

    @Inject
    private UserManagerLocal userManager;

    @PersistenceContext
    private EntityManager em;

    @Override
    public IMAccount removeIMAccount(Long id) throws EntityNotFoundException {
        IMAccount imAcc = em.find(IMAccount.class, id);
        if (imAcc == null) {
            throw new EntityNotFoundException(IMAccount.class, id);
        }
        em.remove(imAcc);
        return imAcc;
    }

    @Override
    public IMAccount createAccount(Long userId, IMAccount account) throws EntityNotFoundException {
        User user = findUser(userId);
        user.getImAccounts().add(account);
        return account;
    }

    private User findUser(Long id) throws EntityNotFoundException {
        User user = userManager.findUserByIdWithIMAccounts(id);
        if (user == null) {
            throw new EntityNotFoundException(User.class, id);
        }

        return user;
    }

    @Override
    public IMAccount updateIMAccount(Long imId, IMAccount imAcc) throws EntityNotFoundException {
        IMAccount acc = em.find(IMAccount.class, imId);
        if (acc == null) {
            throw new EntityNotFoundException(IMAccount.class, imId);
        }

        acc.setAccountName(imAcc.getAccountName());
        acc.setProtocol(imAcc.getProtocol());

        return acc;
    }

}
