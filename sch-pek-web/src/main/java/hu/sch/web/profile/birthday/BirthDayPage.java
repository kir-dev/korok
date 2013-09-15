package hu.sch.web.profile.birthday;

import hu.sch.domain.user.User;
import hu.sch.services.SearchManagerLocal;
import hu.sch.web.profile.ProfilePage;
import hu.sch.web.profile.search.PersonLinkPanel;
import hu.sch.web.wicket.util.SortablePersonDataProvider;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

/**
 *
 * @author aldaris
 */
public class BirthDayPage extends ProfilePage {

    SortablePersonDataProvider personDataProvider;

    @Inject
    private SearchManagerLocal searchManager;

    public BirthDayPage() {
        super();
        setHeaderLabelText("Szülinaposok");
        personDataProvider = new SortablePersonDataProvider(birthDaySearch());

        final DataView<User> dataView = new DataView<User>("simple", personDataProvider) {

            @Override
            public void populateItem(final Item<User> item) {
                final User user = item.getModelObject();
                item.add(new PersonLinkPanel("id", user));
            }
        };
        add(dataView);
    }

    public final List<User> birthDaySearch() {
        List<User> persons = new ArrayList<>();
        Date date = Calendar.getInstance().getTime();
        persons.addAll(searchManager.searchBirthdayUsers(date));

        if (persons.isEmpty()) {
            info("Ma senki sem ünnepel:(");
        }
        return persons;
    }
}
