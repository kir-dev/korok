package hu.sch.web.profile.pages.community;

import hu.sch.domain.User;
import hu.sch.domain.profile.Person;
import hu.sch.services.EntitlementManagerRemote;
import hu.sch.services.MailManagerLocal;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.web.components.ConfirmationBoxRenderer;
import hu.sch.web.error.ErrorPage;
import hu.sch.web.profile.pages.show.ShowPersonPage;
import hu.sch.web.profile.pages.template.ProfilePage;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebPage;

/**
 *
 * @author hege
 */
public class CreateCommunityProfile extends ProfilePage {

    @EJB(name = "foo", mappedName = "EntitlementManager")
    EntitlementManagerRemote entitlementManager;
    @EJB(name = "MailManagerBean")
    private MailManagerLocal mailManager;
    private final String CONFIRMATION_CODE = "Megerősítő kód";
    private final String VIR_PROFILE = "VIR Email cím vagy NEPTUN kód";
    private final String NEPTUN = "NEPTUN kód";
    Person person;
    String inputData;
    String inputKey;
    Long virid;
    private final static Pattern NEPTUN_PATTERN =
            Pattern.compile("[A-Za-z0-9]{6}");
    private static final Logger log =
            Logger.getLogger(CreateCommunityProfile.class);
    private Form dataForm;
    private Component importVIRProfile;
    private Component requestNEPTUNConfirmation;
    private Component createCommunityProfile;
    private Component createProfileWithoutNEPTUN;
    private Component enterConfirmationCode;

    public CreateCommunityProfile(WebPage referer) {
        if (referer == null) {
            getSession().error("Hiba történt.");
            setResponsePage(ErrorPage.class);
            return;
        }
        setHeaderLabelText("Közösségi profil létrehozása");
        try {
            person = ldapManager.getPersonByUid(getUid());
        } catch (PersonNotFoundException ex) {
            getSession().error("Hiba az adatok betöltésekor");
            setResponsePage(ErrorPage.class);
            return;
        }
        add(new FeedbackPanel("feedbackPanel"));

        dataForm = new Form("dataForm") {

            @Override
            protected void onSubmit() {
                super.onSubmit();

                //ha megerősítő kódot írt be
                if (getInputKey().equals(CONFIRMATION_CODE)) {
                    try {
                        //megkeressük a usert, újra (TODO: kell ez?)
                        Person p = ldapManager.getPersonByUid(getUid());
                        if (!p.getConfirmationCode().equals(inputData)) {
                            error("Hibás megerősítő kód");
                            importVIRProfile.setVisible(true);
                            return;
                        }
                        //kinullozzuk a confirmkódot ha jó
                        p.setConfirmationCode(null);
                        String[] conf = inputData.split(":");
                        //ha neptunos megerősítő kód jött
                        if (conf[0].equals("NEPTUN")) {
                            String neptun = conf[1];
                            p.setNeptun(neptun);
                            log.info("Neptun beállítása: " + neptun);
                            info("Neptun kód beállítva");
                            ldapManager.update(p);
                            person = p;
                            //eltűnik minden
                            createProfileWithoutNEPTUN.setVisible(false);
                            requestNEPTUNConfirmation.setVisible(false);
                            dataForm.setVisible(false);
                            enterConfirmationCode.setVisible(false);
                            importVIRProfile.setVisible(false);
                            //csak ez a link marad
                            createCommunityProfile.setVisible(true);
                        }
                        //ha vir-es megerősítő kód jött
                        if (conf[0].equals("VIR")) {
                            String viridStr = conf[1];
                            Long virid = Long.parseLong(viridStr);
                            p.setVirId(virid);
                            log.info("VIRID beállítása: " + virid);
                            String neptun = getNeptunForVirID(virid);
                            if (neptun != null) {
                                info("NEPTUN beállítása: " + neptun);
                                p.setNeptun(neptun);
                            }
                            ldapManager.update(p);
                            setResponsePage(ShowPersonPage.class);
                        }
                    } catch (PersonNotFoundException e) {
                        log.error(e);
                        error("Hiba történt: nem található a felhasználó");
                    }
                    return;
                }
                // ha nem confirmation code jött
                try {
                    if (getInputKey().equals(NEPTUN)) {
                        //nagybetűsítjük a neptun kódot
                        setInputData(getInputData().toUpperCase());
                        if (!NEPTUN_PATTERN.matcher(getInputData()).matches()) {
                            error("Hibás a megadott NEPTUN-kód formátuma");

                            return;
                        }
                    }
                    String cc = generateConfirmationCode();
                    if (cc == null) {
                        error("Hibás email cím vagy NEPTUN kód, vagy nem létező VIR felhasználó");

                        return;
                    }
                    log.debug("Megerősítő kód beírása adatbázisba");
                    person.setConfirmationCode(cc);
                    ldapManager.update(person);
                    String emailAddr = sendConfirmationCode(cc);
                    info("A megerősítő kód kiküldve " + emailAddr + " címre! Egyes szolgáltatóknál a kézbesítés akár egy órát is igénybe vehet!\nAmennyiben ezután se kapod meg, nézd meg a postafiókod SPAM mappáját, ha ott sincs, írj egy e-mailt a kir-dev@sch.bme.hu -ra.");

                    //ez azért kell hogy kitörlődjön a user input
                    inputData = "";
                    //ezek a linkek eltűnnek
                    requestNEPTUNConfirmation.setVisible(false);
                    importVIRProfile.setVisible(false);
                    inputKey = CONFIRMATION_CODE;
                    createProfileWithoutNEPTUN.setVisible(false);
                } catch (Exception ex) {
                    log.error(ex);
                    error("Hiba történt");
                }
            }
        };

        dataForm.setVisible(false);

        importVIRProfile =
                new Link("importVIRProfile") {

                    @Override
                    public void onClick() {
                        setInputKey(VIR_PROFILE);
                        importVIRProfile.setVisible(false);
                        dataForm.setVisible(true);
                        if (person.getConfirmationCode() == null && person.getNeptun() == null) {
                            requestNEPTUNConfirmation.setVisible(true);
                            createProfileWithoutNEPTUN.setVisible(true);
                        }
                        enterConfirmationCode.setVisible(true);
                    }
                }.setVisible(false);
        requestNEPTUNConfirmation =
                new Link("requestNEPTUNConfirmation") {

                    @Override
                    public void onClick() {
                        setInputKey(NEPTUN);
                        dataForm.setVisible(true);
                        requestNEPTUNConfirmation.setVisible(false);
                        importVIRProfile.setVisible(true);
                        createProfileWithoutNEPTUN.setVisible(false);
                        enterConfirmationCode.setVisible(true);
                    }
                }.setVisible(false);
        createCommunityProfile =
                new Link("createCommunityProfile") {

                    @Override
                    public void onClick() {
                        createCommunityProfile(person);
                    }
                }.setVisible(false);
        createProfileWithoutNEPTUN =
                new Link("createProfileWithoutNEPTUN") {

                    @Override
                    public void onClick() {
                        createCommunityProfile(person);
                    }
                }.setVisible(false);

        createProfileWithoutNEPTUN.add(new ConfirmationBoxRenderer("Biztos, hogy külsős vagy, és nincs NEPTUN-kódod?"));
        enterConfirmationCode =
                new Link("enterConfirmationCode") {

                    @Override
                    public void onClick() {
                        setInputKey(CONFIRMATION_CODE);
                        dataForm.setVisible(true);
                    }
                }.setVisible(true);

        dataForm.add(new TextField("inputData",
                new PropertyModel(this, "inputData")).setRequired(true));
        dataForm.add(
                new Label("inputKey", new PropertyModel(this, "inputKey")));

        if (person.getConfirmationCode() != null) {
            enterConfirmationCode.setVisible(true);
        }
        if (person.getNeptun() == null) {
            requestNEPTUNConfirmation.setVisible(true);
        }
        if (person.getVirId() == null) {
            importVIRProfile.setVisible(true);
            requestNEPTUNConfirmation.setVisible(true);
            createProfileWithoutNEPTUN.setVisible(true);
        }
        if (person.getVirId() == null && person.getNeptun() != null) {
            createCommunityProfile.setVisible(true);
        }
        if (person.getVirId() == null && person.getNeptun() == null) {
            createProfileWithoutNEPTUN.setVisible(true);
        }

        add(createCommunityProfile);
        add(createProfileWithoutNEPTUN);
        add(importVIRProfile);
        add(requestNEPTUNConfirmation);
        add(enterConfirmationCode);
        add(dataForm);
        String code = person.getConfirmationCode();
        if (code != null) {
            if (code.startsWith("VIR:")) {
                createProfileWithoutNEPTUN.setVisible(false);
                requestNEPTUNConfirmation.setVisible(false);
            } else if (code.startsWith("NEPTUN:")) {
                createProfileWithoutNEPTUN.setVisible(false);
            }
        } else {
            enterConfirmationCode.setVisible(false);
        }
        if (person.getNeptun() != null) {
            requestNEPTUNConfirmation.setVisible(false);
            createProfileWithoutNEPTUN.setVisible(false);
        }
    }

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public String getInputKey() {
        return inputKey;
    }

    public void setInputKey(String inputKey) {
        this.inputKey = inputKey;
    }

    protected void createCommunityProfile(Person person) {
        if (person.getVirId() == null) {
            //ejb hívás
            log.info("Közösségi profil létrehozása");
            log.info("VIRID: " + person.getVirId());
            User f = new User();
            f.setNickName(person.getNickName());
            f.setNeptunCode(person.getNeptun());
            f.setFirstName(person.getFirstName());
            f.setLastName(person.getLastName());
            f.setEmailAddress(person.getMail());
            f =
                    getEntitlementManager().createUserEntry(f);
            if (f.getId() == null) {
                throw new RuntimeException("A közösségi profil létrehozás után null az ID");
            }

            if (f.getId() != person.getVirId()) {
                log.info("A VIRID megváltozott, mentés lokálisan");
                log.info("Új VIRID: " + f.getId());
                person.setVirId(f.getId());
                if (person.getNeptun() == null && f.getNeptunCode() !=
                        null) {
                    log.info("NEPTUN kód beállítása: " +
                            f.getNeptunCode());
                    person.setNeptun(f.getNeptunCode());
                }

                ldapManager.update(person);
                getSession().info("Sikeres közösségi profil létrehozás");
            }


        }

        setResponsePage(ShowPersonPage.class,
                new PageParameters("uid=" + person.getUid().toString()));

    }

    public EntitlementManagerRemote getEntitlementManager() {
        return entitlementManager;
    }

    private String generateConfirmationCode()
            throws Exception {

        StringBuilder sb = new StringBuilder(100);
        if (inputKey.equals(NEPTUN)) {
            sb.append("NEPTUN:");
            sb.append(inputData);
        } else if (inputKey.equals(VIR_PROFILE)) {
            sb.append("VIR:");
            virid =
                    getVirID(inputData);
            if (virid != 0) {
                sb.append(virid);
            } else {
                return null;
            }

        }
        sb.append(":");
        sb.append(RandomStringUtils.randomAlphanumeric(16));
        return sb.toString();
    }

    private Long getVirID(String neptunOrEmail) throws Exception {
        User user = entitlementManager.findUser(neptunOrEmail, neptunOrEmail);

        return user.getId();
    }

    private String getNeptunForVirID(Long virid) {
        User user = entitlementManager.findUser(virid);

        if (user != null) {
            return user.getNeptunCode();
        }

        return null;
    }

    private String getEmailForVirID(Long virid) {
        User user = entitlementManager.findUser(virid);
        if (user != null) {
            return user.getEmailAddress();
        }

        return null;
    }

    private String sendConfirmationCode(String confirmationCode) {

        String to;
        String subject;
        if (inputKey.equals(VIR_PROFILE)) {
            to = getEmailForVirID(virid);
            subject = new String("VIR Profil megerősítő kód");
        } else if (inputKey.equals(NEPTUN)) {
            to = inputData + "@nc.hszk.bme.hu";
            subject = new String("VIR NEPTUN kód megerősítése");
        } else {
            throw new RuntimeException("Hibás kulcs a formban");
        }
        StringBuilder message = new StringBuilder(500);
        message.append("Kedves ");
        message.append(person.getFullName());
        message.append("!\n\n");
        message.append("Erről az e-mail címről közösségi profil létrehozását kezdeményezték.");
        message.append("Amennyiben Te voltál, az ehhez szükséges megerősítő kód:\n");
        message.append(confirmationCode);
        message.append("\n\n");
        message.append("FIGYELEM!\n");
        message.append("A fentebbi sor egésze a megerősítő kód, NEM csak a kettőspont utáni rész!\n");
        message.append("Ha időközben új kódot igényeltél, akkor a legutoljára kért kódod lesz az érvényes.\n\n\n");
        message.append("Amennyiben nem Te regisztráltál, tekintsd levelünket tárgytalannak!\n\n");
        message.append("Üdvözlettel:\n");
        message.append("Kir-Dev fejlesztői csapat");
        if (!mailManager.sendEmail(to, subject, message.toString())) {
            getSession().error("Nem sikerült elküldeni az e-mailt a " + to + " címre!");
            throw new RestartResponseException(ShowPersonPage.class);
        }
        return to;
    }
}
