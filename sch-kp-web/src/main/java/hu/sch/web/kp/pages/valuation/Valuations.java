/**
 * Copyright (c) 2009-2010, Peter Major
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the Peter Major nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *  * All advertising materials mentioning features or use of this software
 * must display the following acknowledgement:
 * This product includes software developed by the Kir-Dev Team, Hungary
 * and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.services.ValuationManagerLocal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
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
//TODO: újraírni az egészet
public class Valuations extends SecuredPageTemplate {

    @EJB(name = "ValuationManagerBean")
    ValuationManagerLocal valuationManager;
    private static Logger log = Logger.getLogger(Valuations.class);
    private String selected = "";
    List<Valuation> valuationList = new ArrayList<Valuation>();
    private Long id;
    private Group group;
    private Link newValuation;
    private Label groupName;

    public Valuations() {
        setHeaderLabelText("Értékelések");
        init();
    }

    public Valuations(PageParameters params) {
        try {
            Long groupId = params.getLong("id");
            group = userManager.findGroupById(groupId);
            if (!isUserGroupLeader(group)) {
                log.warn("Paraméterátírásos próbálkozás! " + getUser().getId());
                getSession().error("Nincs jogod a művelethez! A próbálkozásod naplózásra került!");
                throw new RestartResponseException(getApplication().getHomePage());
            }
        } catch (Exception ex) {
            log.error("Hiba a paraméter feldolgozása közben: " + params.toString(), ex);
        }
        init();
    }

    public void init() {
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

        User user = userManager.findUserWithMembershipsById(id);
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
            if ((ert == null || ert.getPointStatus() == ValuationStatus.NINCS
                    || ert.getEntrantStatus() == ValuationStatus.NINCS)
                    && isUserGroupLeader(cs)
                    && systemManager.getErtekelesIdoszak() == ValuationPeriod.ERTEKELESLEADAS) {
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
                        if (val == null) {
                            // ha egyáltalán nincs még értékelés az adott csoporthoz
                            // és szemeszterhez, akkor elősször szöveges értékelés kell
                            setResponsePage(NewValuation.class, new PageParameters("id=" + group.getId()));
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
                        if (val == null) {
                            /*
                             * ha egyáltalán nincs még értékelés az adott csoporthoz
                             * és szemeszterhez, akkor elősször szöveges értékelés kell
                             */
                            setResponsePage(NewValuation.class, new PageParameters("id=" + group.getId()));
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
                        updateErtekelesList();
                        if ((valuationList.isEmpty())
                                || (!valuationManager.isErtekelesLeadhato(group))) {
                            newValuation.setVisible(false);
                        } else {
                            newValuation.setVisible(true);
                        }

                        break;
                    }
                }

                setResponsePage(Valuations.class, new PageParameters("id=" + g.getId()));
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
                    protected boolean getStatelessHint() {
                        return false;
                    }

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
                        if (v.getPointStatus() == ValuationStatus.ELFOGADVA
                                || systemManager.getErtekelesIdoszak() != ValuationPeriod.ERTEKELESLEADAS) {
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
                        if (v.getEntrantStatus() == ValuationStatus.ELFOGADVA
                                || systemManager.getErtekelesIdoszak() != ValuationPeriod.ERTEKELESLEADAS) {
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

        if ((valuationList.isEmpty())
                || (!valuationManager.isErtekelesLeadhato(group))) {
            newValuation.setVisible(false);
        } else {
            if (isUserGroupLeaderInSomeGroup()) {
                newValuation.setVisible(true);
            }
        }
    }

    public void updateErtekelesList() {
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
