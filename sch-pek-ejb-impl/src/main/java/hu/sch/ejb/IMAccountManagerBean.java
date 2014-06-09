package hu.sch.ejb;

import hu.sch.domain.user.IMAccount;
import hu.sch.domain.user.User;
import hu.sch.services.IMAccountManager;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.exceptions.EntityNotFoundException;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class IMAccountManagerBean implements IMAccountManager {

    @Inject
    private UserManagerLocal userManager;

    @Override
    public IMAccount removeIMAccount(Long userId, Long imId) throws EntityNotFoundException {
        User user = findUser(userId);

        Optional<IMAccount> imAcc = user.getImAccounts().stream().filter(im -> im.getId().equals(imId)).findFirst();
        if (imAcc.isPresent()) {
            final IMAccount imEntity = imAcc.get();
            user.getImAccounts().remove(imEntity);
            return imEntity;
        }
        throw new EntityNotFoundException(IMAccount.class, imId);
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

}
