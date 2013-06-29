package hu.sch.web.wicket.components.customlinks;

import hu.sch.domain.Membership;
import hu.sch.web.wicket.behaviors.ConfirmationBehavior;
import org.apache.wicket.markup.html.link.Link;

/**
 * Egy olyan {@link LinkPanel}, ami az öregtaggá válást segíti elő, de előtte
 * megbizonyosodik afelől, hogy a felhasználó tényleg ezt akarja.
 *
 * @author  messo
 * @since   2.3.1
 */
public class OldBoyLinkPanel extends LinkPanel<Membership> {

    public OldBoyLinkPanel(String id, Membership ms) {
        super(id, ms);

        Link<Void> link = new Link<Void>("link") {

            @Override
            public void onClick() {
                if (column != null) {
                    column.onClick(obj);
                }
            }
        };
        link.add(new ConfirmationBehavior("Biztosan öregtaggá szeretnél válni?"));
        add(link);
    }
}
