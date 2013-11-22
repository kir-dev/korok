package hu.sch.ejb;

import hu.sch.services.SystemManagerLocal;
import javax.persistence.EntityManager;

/**
 * For dependency injection in testing environment.
 * 
 * @author tomi
 */
public class EjbConstructorArgument {
    private EntityManager em;
    private SystemManagerLocal systemManager;

    public EjbConstructorArgument() {
    }

    public EjbConstructorArgument(EntityManager em) {
        this.em = em;
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public SystemManagerLocal getSystemManager() {
        return systemManager;
    }

    public void setSystemManager(SystemManagerLocal systemManager) {
        this.systemManager = systemManager;
    }
}
