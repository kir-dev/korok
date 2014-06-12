package hu.sch.services;

import hu.sch.domain.user.IMAccount;
import hu.sch.services.exceptions.EntityNotFoundException;
import javax.ejb.Local;

@Local
public interface IMAccountManager {

    /**
     * Remove an IM account from a user.
     *
     * @param id
     * @return
     * @throws EntityNotFoundException
     */
    IMAccount removeIMAccount(Long id) throws EntityNotFoundException;

    IMAccount createAccount(Long userId, IMAccount account) throws EntityNotFoundException;

    public IMAccount updateIMAccount(Long imId, IMAccount imAcc) throws EntityNotFoundException;

}
