package hu.sch.services;

import hu.sch.domain.user.IMAccount;
import hu.sch.services.exceptions.EntityNotFoundException;
import javax.ejb.Local;

@Local
public interface IMAccountManager {

    /**
     * Remove an IM account from a user.
     *
     * @param userId
     * @param imId
     * @return
     * @throws EntityNotFoundException
     */
    IMAccount removeIMAccount(Long userId, Long imId) throws EntityNotFoundException;

}
