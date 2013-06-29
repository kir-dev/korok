package hu.sch.web.wicket.components;

import hu.sch.domain.Membership;
import hu.sch.domain.interfaces.MembershipTableEntry;
import hu.sch.web.wicket.components.tables.SelectableEntry;

/**
 * {@link Membership} objektumokhoz egy wrapper, hogy el tudjuk tárolni, hogy ki
 * van-e jelölve a listában vagy nincs. Származtatni bonyolult lenne, mert akkor
 * meg kéne írni a downcastot.
 *
 * @author      messo
 * @since       2.3.1
 */
public class SelectableMembership implements MembershipTableEntry, SelectableEntry {

    private Membership membership;
    private boolean selected;

    public SelectableMembership(Membership membership) {
        this.membership = membership;
    }

    @Override
    public Membership getMembership() {
        return membership;
    }

    public void setMembership(Membership membership) {
        this.membership = membership;
    }

    @Override
    public boolean getSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean isSelected) {
        this.selected = isSelected;
    }
}
