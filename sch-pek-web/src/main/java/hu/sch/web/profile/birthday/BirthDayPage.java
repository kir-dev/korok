package hu.sch.web.profile.birthday;

import hu.sch.domain.profile.Person;
import hu.sch.web.profile.ProfilePage;
import hu.sch.web.profile.search.PersonLinkPanel;
import hu.sch.web.wicket.util.SortablePersonDataProvider;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

/**
 *
 * @author aldaris
 */
public class BirthDayPage extends ProfilePage {

    SortablePersonDataProvider personDataProvider;

    public BirthDayPage() {
        super();
        setHeaderLabelText("Szülinaposok");
        personDataProvider = new SortablePersonDataProvider(birthDaySearch());

        final DataView<Person> dataView = new DataView<Person>("simple", personDataProvider) {

            @Override
            public void populateItem(final Item<Person> item) {
                final Person user = item.getModelObject();
                item.add(new PersonLinkPanel("id", user));
            }
        };
        add(dataView);
    }

    public final List<Person> birthDaySearch() {
        List<Person> persons = new ArrayList<Person>();
        Date date = Calendar.getInstance().getTime();
        String date2 = new SimpleDateFormat("MMdd").format(date);
        persons.addAll(ldapManager.getPersonsWhoHasBirthday(date2));
        if (persons.isEmpty()) {
            info("Ma senki se ünnepel:(");
        }
        return persons;
    }
}
