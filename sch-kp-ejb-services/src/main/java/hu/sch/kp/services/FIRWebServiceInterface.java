/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.kp.services;

import hu.sch.domain.ElfogadottBelepo;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author hege
 */
@Local
public interface FIRWebServiceInterface {
    List<ElfogadottBelepo> getElfogadottBelepokForSzemeszter(String szemeszter);
}
