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

package hu.sch.web.profile.pages.show;

import hu.sch.domain.profile.IMAccount;
import hu.sch.domain.profile.Person;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.web.kp.pages.user.ShowUser;
import hu.sch.web.wicket.components.ImageResource;
import hu.sch.web.profile.pages.admin.AdminPage;
import hu.sch.web.profile.pages.community.CreateCommunityProfile;
import hu.sch.web.profile.pages.template.ProfilePageTemplate;
import hu.sch.web.wicket.components.customlinks.SearchLink;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

/**
 * Homepage
 */
public class ShowPersonPage extends ProfilePageTemplate {

    //private static final Logger log = Logger.getLogger(ShowPersonPage.class);
    private Person person;

    public void setPerson(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    private void bindPerson() {
        setDefaultModel(new CompoundPropertyModel<Person>(person));
        setHeaderLabelText(person.getFullName());
        //add(new Label("uid"));
        //add(new Label("fullName"));

        if (person.getVirId() != null) {
            add(new ExternalLink("simpleView", "/korok/showuser/id/" + person.getVirId()));
            add(new ExternalLink("detailView", "/korok/userhistory/id/" + person.getVirId()));
            add(new Label("createCommunityProfile").setVisible(false));
        } else {
            add(new ExternalLink("simpleView", "/korok/showuser").setVisible(false));
            add(new ExternalLink("detailView", "/korok/userhistory").setVisible(false));
            //hogy ne lehessen könyvjelzőzni a linket
            Link pageLink = new Link("createCommunityProfile") {

                @Override
                public void onClick() {
                    setResponsePage(new CreateCommunityProfile(new ShowPersonPage()));
                    return;
                }
            };

            add(pageLink);
            //Ha nem a saját profilunkat nézzük, akkor ne jelenjen meg a készítős link
            if (!person.getUid().equalsIgnoreCase(getRemoteUser())) {
                pageLink.setVisible(false);
            }
        }

        Link<AdminPage> adminLink =
                new BookmarkablePageLink<AdminPage>("adminLink", AdminPage.class,
                new PageParameters("uid=" + person.getUid()));
        if (!isCurrentUserAdmin()) {
            adminLink.setVisible(false);
        }
        add(adminLink);

        WebMarkupContainer mailWMC = new WebMarkupContainer("mailWMC");

        mailWMC.add(new SmartLinkLabel("mail"));
        mailWMC.setVisible(
                !person.isPrivateAttribute("mail")
                && person.getMail() != null);
        add(mailWMC);

        add(new ListView<IMAccount>("ims", person.getIMAccounts()) {

            @Override
            protected void populateItem(ListItem<IMAccount> item) {
                final IMAccount acc = item.getModelObject();
                item.add(new Label("imProtocol",
                        new PropertyModel<IMAccount>(acc, "protocol")));
                item.add(new Label("imPresenceID",
                        new PropertyModel<IMAccount>(acc, "presenceID")));
            }
        });

        WebMarkupContainer mobileWMC = new WebMarkupContainer("mobileWMC");

        mobileWMC.add(new Label("mobile"));
        mobileWMC.setVisible(
                !person.isPrivateAttribute("mobile")
                && person.getMobile() != null);
        add(mobileWMC);

        WebMarkupContainer homePhoneWMC = new WebMarkupContainer("homePhoneWMC");

        homePhoneWMC.add(new Label("homePhone"));
        homePhoneWMC.setVisible(
                !person.isPrivateAttribute("homePhone")
                && person.getHomePhone() != null);
        add(homePhoneWMC);

        WebMarkupContainer webpageWMC = new WebMarkupContainer("webpageWMC");

        webpageWMC.add(new SmartLinkLabel("webpage"));
        webpageWMC.setVisible(
                !person.isPrivateAttribute("labeledURI")
                && person.getWebpage() != null);
        add(webpageWMC);

        WebMarkupContainer roomNumberWMC =
                new WebMarkupContainer("roomNumberWMC");

        roomNumberWMC.add(new SearchLink("roomNumber", SearchLink.USER_TYPE, person.getRoomNumber()));
        roomNumberWMC.setVisible(
                !person.isPrivateAttribute("roomNumber")
                && person.getRoomNumber() != null);
        add(roomNumberWMC);

        WebMarkupContainer nickNameWMC = new WebMarkupContainer("nickNameWMC");

        nickNameWMC.add(new Label(
                "nickName"));
        nickNameWMC.setVisible(person.getNickName() != null);
        add(nickNameWMC);

        WebMarkupContainer homePostalAddressWMC =
                new WebMarkupContainer("homePostalAddressWMC");

        homePostalAddressWMC.add(new Label("homePostalAddress"));
        homePostalAddressWMC.setVisible(
                !person.isPrivateAttribute("homePostalAddress")
                && person.getHomePostalAddress() != null);
        add(homePostalAddressWMC);

        Label dateOfBirth = new Label("dateOfBirth");
        Date dob;
        if (person.getDateOfBirth() != null) {
            try {
                dob = new SimpleDateFormat("yyyyMMdd").parse(person.getDateOfBirth());
                dateOfBirth = new Label("dateOfBirth", new SimpleDateFormat("yyyy. MMMM dd.",
                        new Locale("hu")).format(dob));
            } catch (ParseException ex) {
            }
        }
        WebMarkupContainer dateOfBirthWMC = new WebMarkupContainer("dateOfBirthWMC");

        dateOfBirthWMC.add(dateOfBirth);

        dateOfBirthWMC.setVisible(
                !person.isPrivateAttribute("schacDateOfBirth")
                && person.getDateOfBirth() != null);
        add(dateOfBirthWMC);

        NonCachingImage photo = new NonCachingImage("photo", new AbstractReadOnlyModel<ImageResource>() {

            @Override
            public ImageResource getObject() {
                return new ImageResource(person.getPhoto(), "png");
            }
        });
        photo.setVisible(person.getPhoto() != null);
        add(photo);
    }

    public ShowPersonPage() {
        add(new FeedbackPanel("feedbackPanel"));
        try {
            setPerson(ldapManager.getPersonByUid(getRemoteUser()));
            bindPerson();
        } catch (PersonNotFoundException e) {
        }
    }

    public ShowPersonPage(PageParameters params) {
        add(new FeedbackPanel("feedbackPanel"));
        String uid = params.getString("uid");
        String virid = params.getString("virid");

        if (uid == null && virid == null) {
            getSession().error("A felhasználó nem található!");
            setResponsePage(getApplication().getHomePage());
            return;
        } else if (uid != null && virid == null) {
            try {
                setPerson(ldapManager.getPersonByUid(uid));
            } catch (PersonNotFoundException e) {
                getSession().error("A felhasználó nem található!");
                setResponsePage(getApplication().getHomePage());
                return;
            }
        } else if (uid == null && virid != null) {
            try {
                setPerson(ldapManager.getPersonByVirId(virid));
            } catch (PersonNotFoundException e) {
                getSession().error("A felhasználó nem található!");
                setResponsePage(getApplication().getHomePage());
                return;
            }
        } else {
            getSession().error("A felhasználó nem található!");
            setResponsePage(getApplication().getHomePage());
            return;
        }
        bindPerson();
    }
}
