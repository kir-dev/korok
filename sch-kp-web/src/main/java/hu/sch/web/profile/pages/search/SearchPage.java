package hu.sch.web.profile.pages.search;

import hu.sch.web.components.FocusOnLoadBehavior;
import hu.sch.web.profile.pages.template.ProfilePage;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author konvergal
 */
public class SearchPage extends ProfilePage {

    public class SearchForm extends Form {

        public String searchString;

        public SearchForm(String componentName) {
            super(componentName);
            TextField<String> sf = new TextField<String>("searchString",
                    new PropertyModel<String>(this, "searchString"));
            sf.add(new FocusOnLoadBehavior());
            add(sf);
        }

        @Override
        protected void onSubmit() {
            setResponsePage(new SearchResultPage(searchString));
        }
    }

    public SearchPage() {
        super();

        setHeaderLabelText("Keresés");
        add(new SearchForm("searchForm"));

        /*        DataView personsDataView = new DataView("personsDataView", new ListDataProvider(persons)) {

        @Override
        protected void populateItem(Item item) {
        Person person = (Person) item.getModelObject();
        item.add(new Label("nickName", person.getNickName()));
        BookmarkablePageLink bpl = new BookmarkablePageLink("profilePageLink", HomePage.class, new PageParameters("uid=" + person.getUid()));
        bpl.add(new Label("fullName", person.getFullName()));
        item.add(new Label("mail", person.getMail()));
        item.add(new Label("roomNumber", person.getRoomNumber()));
        item.add(bpl);
        }
        };
        add(personsDataView);*/
    }

    public SearchPage(PageParameters params) {
        //TODO:  process page parameters
    }
}

