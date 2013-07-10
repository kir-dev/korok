package hu.sch.ejb.test.builder;

/**
 *
 * @author tomi
 */
public interface Builder<TDomain> {

    /**
     * Builds the domain class.
     * @return
     */
    TDomain build();

}
