/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.kp.ejb;

import hu.sch.domain.ElfogadottBelepo;
import hu.sch.domain.Szemeszter;
import hu.sch.kp.services.ErtekelesManagerLocal;
import hu.sch.kp.services.FIRWebServiceInterface;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;

/**
 *
 * @author hege
 */
@WebService
@Stateless
public class FIRWebService implements FIRWebServiceInterface {
    @EJB(name = "ErtekelesManagerBean")
    private ErtekelesManagerLocal ertekelesManager;

    public List<ElfogadottBelepo> getElfogadottBelepokForSzemeszter(
            final String szemeszter) {

        Szemeszter sz = new Szemeszter();
        sz.setId(szemeszter);
        return ertekelesManager.findElfogadottBelepoIgenyekForSzemeszter(sz);
    }
}
