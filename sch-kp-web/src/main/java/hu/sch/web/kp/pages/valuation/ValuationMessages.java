/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.valuation;

import hu.sch.domain.Valuation;
import hu.sch.domain.ValuationMessage;
import hu.sch.web.components.UserLink;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.services.ValuationManagerLocal;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author hege
 */
public class ValuationMessages extends SecuredPageTemplate {

    @EJB(name = "ValuationManagerBean")
    ValuationManagerLocal valuationManager;

    public ValuationMessages(final Long valuationId) {
        setHeaderLabelText("Üzenetek");
        Valuation valuation = valuationManager.getErtekelesWithUzenetek(valuationId);
        valuation.sortMessages();
        List<ValuationMessage> messages = valuation.getMessages();
        if (messages.isEmpty()) {
            info(getLocalizer().getString("info.NincsUzenet", this));
        }

        ListView uzenetekView = new ListView("uzenetek", messages) {

            @Override
            protected void populateItem(ListItem item) {
                ValuationMessage u = (ValuationMessage) item.getModelObject();
                item.setModel(new CompoundPropertyModel(u));
                item.add(new UserLink("sender", u.getSender()));
                item.add(DateLabel.forDatePattern("date", "yyyy.MM.dd. kk:mm"));
                item.add(new MultiLineLabel("message"));
            }
        };
        add(uzenetekView);

        Link ujuzenet = new Link("newMessageLink") {

            @Override
            public void onClick() {
                setResponsePage(new NewMessage(valuationId));
            }
        };
        ujuzenet.setVisible(systemManager.getSzemeszter().equals(valuation.getSemester()));
        add(ujuzenet);
    }
}
