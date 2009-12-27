/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.valuation;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.Valuation;
import hu.sch.domain.ValuationPeriod;
import hu.sch.domain.ValuationStatus;
import hu.sch.domain.User;
import hu.sch.web.kp.pages.entrantrequests.EntrantRequestFiling;
import hu.sch.web.kp.pages.group.GroupHierarchy;
import hu.sch.web.kp.pages.pointrequests.PointRequestFiling;
import hu.sch.web.session.VirSession;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.services.ValuationManagerLocal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author hege
 */
public class Valuations extends SecuredPageTemplate {

    @EJB(name = "ValuationManagerBean")
    ValuationManagerLocal valuationManager;
    private String selected = "";
    List<Valuation> valuationList = new ArrayList<Valuation>();
    private Long id;
    private Group group;
    private Link newValuation;
    private Label groupName;

    public Valuations() {
        setHeaderLabelText("Értékelések");
        if (id == null) {
            id = getSession().getUserId();
        }
        if (id == null) {
            error("Hiba, ismeretlen felhasználó!");
            throw new RestartResponseException(GroupHierarchy.class);
        }
        add(new FeedbackPanel("pagemessages"));
        groupName = new Label("name", "");
        groupName.setVisible(false);
        add(groupName);

        User user = userManager.findUserWithCsoporttagsagokById(id);
        if (user == null) {
            //Ez egy soha sorra nem kerulo feltetel, mivel csak korvezetonek jelenhet meg
            //az opcio, igy legalabb tuti nem szall el
            getSession().info("Nem vagy körtag, mégis értékelést szeretnél leadni? Nono...");
            throw new RestartResponseException(GroupHierarchy.class);
        }
        if (!isUserGroupLeaderInSomeGroup()) {
            getSession().info("Nem vagy sehol sem körvezető, mit csinálsz itt?");
            throw new RestartResponseException(GroupHierarchy.class);
        }
        user.sortMemberships();

        final List<Membership> ms = user.getMemberships();
        final ArrayList<String> groups = new ArrayList<String>();
        //TODO simplify this
        for (Membership m : ms) {
            if (isUserGroupLeader(m.getGroup())) {
                groups.add(m.getGroup().getName());
            }
        }

        // megkeresem mire nem adott még le értékelést vagy belépőigényt az aktuális félévben
        final ArrayList<Group> left = new ArrayList<Group>();
        for (Membership m : ms) {
            Group cs = m.getGroup();

            Valuation ert = valuationManager.findErtekeles(cs, systemManager.getSzemeszter());
            if ((ert == null || ert.getPointStatus() == ValuationStatus.NINCS ||
                    ert.getEntrantStatus() == ValuationStatus.NINCS) &&
                    isUserGroupLeader(cs) &&
                    systemManager.getErtekelesIdoszak() == ValuationPeriod.ERTEKELESLEADAS) {
                left.add(cs);
            }
        }

        // megjelenítem a még nem értékel csoportokra a figyelmeztetést, és hozzá a linkeket
        WebMarkupContainer warningContainer = new WebMarkupContainer("warningContainer");
        this.add(warningContainer);
        warningContainer.setVisible(!left.isEmpty());

        ListView<Group> leftListView = new ListView<Group>("warningList", left) {

            @Override
            protected void populateItem(ListItem<Group> item) {
                @SuppressWarnings("hiding")
                final Group group = item.getModelObject();
                final Valuation val = valuationManager.findErtekeles(group, systemManager.getSzemeszter());

                // group név kijelés
                item.add(new Label("warningGroupName", group.getName()));

                // értékelés kijelzés
                Link ertekelesLink = new Link("warningLink") {

                    @Override
                    public void onClick() {
                        // group kiválasztása (mert nem feltétlen volt legördülővel...)
                        ((VirSession) getSession()).setGroupId(group.getId());

                        if (val == null) {
                            /*
                             * ha egyáltalán nincs még értékelés az adott csoporthoz
                             * és szemeszterhez, akkor elősször szöveges értékelés kell
                             */
                            setResponsePage(NewValuation.class);
                        } else {
                            // pontigény leadása a szöveges értékelés mellé
                            setResponsePage(new PointRequestFiling(val));
                        }
                    }
                };

                if (val == null || val.getPointStatus() == ValuationStatus.NINCS) {
                    ertekelesLink.setVisible(true);
                } else {
                    ertekelesLink.setVisible(false);
                }

                item.add(ertekelesLink);

                // belépőigénylés kijelzés
                Link belepoLink = new Link("warningEntrantLink") {

                    @Override
                    public void onClick() {
                        // group kiválasztása (mert nem feltétlen volt legördülővel...)
                        ((VirSession) getSession()).setGroupId(group.getId());

                        if (val == null) {
                            /*
                             * ha egyáltalán nincs még értékelés az adott csoporthoz
                             * és szemeszterhez, akkor elősször szöveges értékelés kell
                             */
                            setResponsePage(NewValuation.class);
                        } else {
                            // belépőigény leadása a szöveges értékelés mellé
                            setResponsePage(new EntrantRequestFiling(val));
                        }
                    }
                };

                if (val == null || val.getEntrantStatus() == ValuationStatus.NINCS) {
                    belepoLink.setVisible(true);
                } else {
                    belepoLink.setVisible(false);
                }

                item.add(belepoLink);
            }
        };
        warningContainer.add(leftListView);

        // Ha mar korabban volt group kivalasztva.
        updateErtekelesList();

        DropDownChoice ddc = new DropDownChoice("groups", new PropertyModel(this, "selected"), groups) {

            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(final Object newSelection) {
                Iterator<Membership> iterator = ms.iterator();

                Group g = null;
                while (iterator.hasNext()) {
                    //TODO simplify this
                    g = (iterator.next()).getGroup();
                    if (g.getName().equals(selected)) {
                        ((VirSession) getSession()).setGroupId(g.getId());
                        updateErtekelesList();
                        if ((valuationList.isEmpty()) ||
                                (!valuationManager.isErtekelesLeadhato(group))) {
                            newValuation.setVisible(false);
                        } else {
                            newValuation.setVisible(true);
                        }

                        break;
                    }
                }

                setResponsePage(Valuations.class);
            }
        };
        add(ddc);

        WebMarkupContainer table = new WebMarkupContainer("ertekelesektabla");
        ListView<Valuation> ertekelesListView = new ListView<Valuation>("valuationList", valuationList) {

            @Override
            protected void populateItem(ListItem<Valuation> item) {
                final Valuation v = item.getModelObject();
                Link ert = new Link("valuationLink") {

                    @Override
                    public void onClick() {
                        setResponsePage(new ValuationDetails(v));
                    }
                };
                ert.add(new Label("valuationSemester", new PropertyModel(v, "semester")));
                item.add(ert);
                IModel model = new CompoundPropertyModel(v);
                item.setModel(model);

                Link uzenetekLink = new Link("messagesLink") {

                    @Override
                    public void onClick() {
                        setResponsePage(new ValuationMessages(v.getId()));
                    }
                };
                uzenetekLink.add(new Label("uzenetek", "Üzenetek"));
                item.add(uzenetekLink);

                Link pontkerelemLink = new Link("pointLink", model) {

                    @Override
                    public void onClick() {
                        if (v.getPointStatus() == ValuationStatus.ELFOGADVA ||
                                systemManager.getErtekelesIdoszak() != ValuationPeriod.ERTEKELESLEADAS) {
                            setResponsePage(new PointRequestViewer(v));
                            return;
                        } else {
                            setResponsePage(new PointRequestFiling(v));
                        }
                    }
                };
                pontkerelemLink.add(new Label("pointStatus"));
                item.add(pontkerelemLink);

                Link belepokerelemLink = new Link("entrantLink", model) {

                    @Override
                    public void onClick() {
                        if (v.getEntrantStatus() == ValuationStatus.ELFOGADVA ||
                                systemManager.getErtekelesIdoszak() != ValuationPeriod.ERTEKELESLEADAS) {
                            setResponsePage(new EntrantRequestViewer(v));
                            return;
                        } else {
                            setResponsePage(new EntrantRequestFiling((v)));
                            return;
                        }
                    }
                };
                item.add(belepokerelemLink);
                belepokerelemLink.add(new Label("entrantStatus"));

                item.add(DateLabel.forDatePattern("lastModified", "yyyy.MM.dd. kk:mm"));
                item.add(DateLabel.forDatePattern("lastConsidered", "yyyy.MM.dd. kk:mm"));
            }
        };
        table.add(ertekelesListView);
        add(table);

        newValuation = new Link("newValuation") {

            @Override
            public void onClick() {
                setResponsePage(NewValuation.class);
            }
        };
        add(newValuation);

        if ((valuationList.isEmpty()) ||
                (!valuationManager.isErtekelesLeadhato(group))) {
            newValuation.setVisible(false);
        } else {
            if (isUserGroupLeaderInSomeGroup()) {
                newValuation.setVisible(true);
            }
        }
    }

    public void updateErtekelesList() {
        group = getGroup();
        if (group != null) {
            valuationList.clear();
            valuationList.addAll(valuationManager.findErtekeles(group));
            selected = group.getName();
            setHeaderLabelText("Értékelések");
            groupName.setDefaultModel(new Model(group.getName()));
            groupName.setVisible(true);
        }
    }
}
