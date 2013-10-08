package hu.sch.web.common;

import hu.sch.web.kp.search.SearchResultsPage;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Minden {@link PekPage} sablonnak implementálnia kell a {@link PekPage#getHeaderPanel(java.lang.String)}
 * metódust, amennyiben szükség lesz navigációs "linksorra", célszerű ebből a
 * panelből származtatni a visszaadott {@link Panel}t, és a konstruktor végén
 * meghívni a {@link HeaderPanel#createLinks()} metódust. (Azért kell explicit
 * meghívni, mert az egyes linkek láthatósága még nem ismert az ősosztály
 * konstruktorának meghívásakor.)
 *
 * @author messo
 * @since 2.4
 */
public abstract class HeaderPanel extends Panel {

    private String searchTerm;
    private String searchType = "felhasználó";

    public HeaderPanel(String id) {
        super(id);
        createSearchBar();
    }

    private void createSearchBar() {
        final Form<Void> searchForm = new StatelessForm<Void>("searchForm") {

            @Override
            protected void onSubmit() {
                if (searchType == null || searchTerm == null) {
                    super.getSession().error("Hibás keresési feltétel!");
                    throw new RestartResponseException(getApplication().getHomePage());
                }
                if (searchTerm.length() < 3) {
                    super.getSession().error("Túl rövid keresési feltétel!");
                    throw new RestartResponseException(getApplication().getHomePage());
                }
                PageParameters params = new PageParameters();
                params.add("type", ((searchType.equals("felhasználó")) ? "user" : "group"));
                params.add("key", searchTerm);
                setResponsePage(SearchResultsPage.class, params);
            }
        };
        DropDownChoice<String> searchTypeDdc = new DropDownChoice<String>("searchDdc",
                new PropertyModel<String>(this, "searchType"),
                new LoadableDetachableModel<List<? extends String>>() {

                    @Override
                    protected List<? extends String> load() {
                        List<String> ret = new ArrayList<String>();
                        ret.add("felhasználó");
                        ret.add("kör");
                        return ret;
                    }
                });
        searchTypeDdc.setNullValid(false);
        searchForm.add(searchTypeDdc);
        searchForm.add(new TextField<String>("searchField", new PropertyModel<String>(this, "searchTerm")));
        add(searchForm);
    }

    /**
     * Létrehozza a navigációs linksort, meghívva az implementált {@link HeaderPanel#getHeaderLinks()}
     * metódust.
     */
    protected void createLinks() {
        add(new ListView<HeaderLink>("menu", getHeaderLinks()) {

            @Override
            protected void populateItem(ListItem<HeaderLink> item) {
                HeaderLink hl = item.getModelObject();
                item.add(new BookmarkablePageLink("menuLink", hl.getPageClass()).add(new Label("linkText", hl.getText())));
            }
        });
    }

    protected abstract List<HeaderLink> getHeaderLinks();
}
