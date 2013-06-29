package hu.sch.web.wicket.components.customlinks;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import hu.sch.domain.SvieMembershipType;
import hu.sch.domain.User;
import hu.sch.domain.config.Configuration;
import hu.sch.domain.profile.Person;
import hu.sch.services.LdapManagerLocal;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.web.kp.user.ShowUser;
import hu.sch.web.wicket.util.ByteArrayResourceStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.ejb.EJB;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.IResourceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aldaris
 */
public class SvieRegPdfLink extends LinkPanel<User> {

    @EJB(name = "LdapManagerBean")
    LdapManagerLocal ldapManager;
    private static final Logger logger = LoggerFactory.getLogger(SvieRegPdfLink.class);
    private static final long serialVersionUID = 1L;
    private static Image schLogo;
    private static Image signImage;
    private final User user;
    private Person person;
    private String cachedmsType;
    private static BaseFont arialUnicode;
    private static Font font;
    private static Paragraph firstStatement;
    private static Paragraph secondStatement;
    private static Paragraph thirdStatement;
    private static Paragraph obeyStatement;
    private static Paragraph fourthStatement;
    private static Paragraph permissionStatement;

    static {
        try {
            arialUnicode =
                    BaseFont.createFont(Configuration.getFontPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            font = new Font(arialUnicode, 12);

            StringBuilder sb = new StringBuilder(150);
            sb.append("A Budapesti Műszaki Egyetem Villamosmérnöki és Informatikai ");
            sb.append("Karának nappali tagozatos hallgatója vagyok, egyben tagsággal ");
            sb.append("bírok egy, a Schönherz Zoltán Kollégiumban működő Egyesületi Körben.");
            firstStatement = new Paragraph(sb.toString(), font);
            firstStatement.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            firstStatement.setSpacingBefore(15f);

            secondStatement = new Paragraph(
                    "E tagsági minőséggel kapcsolatos jogaimat illetve kötelezettségeimet megismertem.",
                    font);
            secondStatement.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            secondStatement.setSpacingBefore(15f);

            sb = new StringBuilder(300);
            sb.append("Tudomásul veszem, hogy amennyiben az Egyesületi Kör, melyben tagsággal ");
            sb.append("bírok megszűnik, és egyidejűleg más Egyesületi Körhöz nem csatlakozom, a ");
            sb.append("Választmány rendes tagságomat automatikusan pártoló tagsággá minősíti.\n\n");
            sb.append("Tudomásul veszem, hogy kizárólag egy Egyesületi Körben regisztráltathatom ");
            sb.append("magam szavazatra jogosult rendes tagként, küldöttválasztás során, kizárólag ");
            sb.append("egy Egyesületi Kör tekintetében rendelkezem szavazati jogosultsággal.");
            thirdStatement = new Paragraph(sb.toString(), font);
            thirdStatement.setAlignment(Paragraph.ALIGN_JUSTIFIED);

            sb = new StringBuilder(300);
            sb.append("Az Egyesület Alapszabályát illetve Szervezeti Működési Szabályzatát ");
            sb.append("magamra nézve kötelezőnek ismerem el, azt betartom.\n\nKijelentem, hogy ");
            sb.append("sem jogszabályban, sem az Egyesület Alapszabályában meghatározott kizáró ok ");
            sb.append("velem szemben nem áll fenn.\n\nTudomásul veszem, hogy jelen belépési nyilatkozat ");
            sb.append("aláírásán kívül az Egyesületbe való belépésem érvényességi feltétele az ");
            sb.append("Egyesület Választmányának jóváhagyó döntése.");
            obeyStatement = new Paragraph(sb.toString(), font);
            obeyStatement.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            obeyStatement.setSpacingBefore(15f);

            sb = new StringBuilder(300);
            sb.append("Tudomásul veszem, hogy az Egyesületbe történő belépésem a Választmány jóváhagyó ");
            sb.append("döntését követően a jóváhagyás időpontjában érvényes belépési tagsági díj ");
            sb.append("megfizetésével válik hatályossá. Tudomásul veszem, hogy a vonatkozó jogszabályokban, ");
            sb.append("és az Egyesület alapszabályában biztosított jogaimat ezen tagdíj megfizetésétől gyakorolhatom.");
            fourthStatement = new Paragraph(sb.toString(), font);
            fourthStatement.setAlignment(Paragraph.ALIGN_JUSTIFIED);

            sb = new StringBuilder(150);
            sb.append("Egyúttal hozzájárulok ahhoz, hogy jelen nyilatkozatban megadott adataimat ");
            sb.append("az Egyesület, működésének keretein belül szabadon felhasználhatja, azokat ");
            sb.append("kezelheti.");
            permissionStatement = new Paragraph(sb.toString(), font);
            permissionStatement.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            permissionStatement.setSpacingBefore(15f);
            permissionStatement.setSpacingAfter(15f);
        } catch (DocumentException ex) {
            logger.warn("Error while creating static content for PDF", ex);
        } catch (IOException ex) {
            logger.warn("Error while creating static content for PDF", ex);
        }
    }

    public SvieRegPdfLink(String id, User user2) {
        super(id, user2);
        this.user = user2;
        cachedmsType = user.getSvieMembershipType().toString();
        try {
            if (schLogo == null) {
                getSchLogo();
                getSigninArea();
            }
        } catch (Exception ex) {
            getSession().error("Hiba a PDF generálása közben.");
            throw new RestartResponseException(ShowUser.class);
        }
        add(new Link<Void>("pdfLink") {

            @Override
            public void onClick() {
                try {
                    person = ldapManager.getPersonByVirId(user.getId().toString());
                } catch (PersonNotFoundException ex) {
                    getSession().error("Hiba a PDF generálása közben.");
                    throw new RestartResponseException(ShowUser.class);
                }

                //rendes tag és nincs elsődleges kör elmentve
                if (user.getSvieMembershipType().equals(SvieMembershipType.RENDESTAG)
                        && user.getSviePrimaryMembership() == null) {

                    getSession().error("Előbb válaszd ki és mentsd el az elsődleges köröd!");
                    return;
                }

                try {
                    IResourceStream resourceStream = new ByteArrayResourceStream(
                            ((ByteArrayOutputStream) generatePdf()).toByteArray(),
                            "application/pdf");
                    getRequestCycle().scheduleRequestHandlerAfterCurrent(
                            new ResourceStreamRequestHandler(resourceStream, "export_" + person.getNeptun() + ".pdf"));
                } catch (Exception ex) {
                    getSession().error("Hiba történt a PDF generálása közben!");
                    logger.error("Could not generate svieregpdf", ex);
                }
            }
        });
    }

    private OutputStream generatePdf() throws DocumentException, IOException {
        Document document = new Document(PageSize.A4, 72, 72, 56, 56);
        final OutputStream os = new ByteArrayOutputStream();
        PdfWriter pdfWriter = PdfWriter.getInstance(document, os);
        document.open();
        schLogo.setAlignment(Image.ALIGN_CENTER);
        schLogo.scalePercent(33f);
        document.add(schLogo);

        font.setSize(14f);
        font.setStyle(Font.BOLD);
        Paragraph title = new Paragraph("Tagfelvételi kérelem\n" + cachedmsType + "sághoz", font);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);

        font.setSize(12f);
        font.setStyle(Font.UNDEFINED);
        Paragraph firstParagraph = new Paragraph(createUserInfo(), font);
        firstParagraph.setAlignment(Paragraph.ALIGN_JUSTIFIED);
        firstParagraph.setSpacingBefore(20f);
        document.add(firstParagraph);

        if (user.getSvieMembershipType().equals(SvieMembershipType.RENDESTAG)) {
            Paragraph primaryGroupParagraph = new Paragraph(createPrimaryGroupInfo(), font);
            primaryGroupParagraph.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            document.add(primaryGroupParagraph);

            document.add(firstStatement);
        }
        document.add(secondStatement);
        if (user.getSvieMembershipType().equals(SvieMembershipType.RENDESTAG)) {
            document.add(thirdStatement);
        }
        document.add(obeyStatement);
        if (user.getSvieMembershipType().equals(SvieMembershipType.RENDESTAG)) {
            document.add(fourthStatement);
        }
        document.add(permissionStatement);

        StringBuilder sb = new StringBuilder(200);
        sb.append("Budapest, ");
        sb.append(new SimpleDateFormat("yyyy. MMMM d.", new Locale("hu")).format(new Date()));
        sb.append("\n\n");
        document.add(new Paragraph(sb.toString(), font));

        PdfContentByte cb = pdfWriter.getDirectContent();
        float pos = pdfWriter.getVerticalPosition(true);
        cb.moveTo(220, pos - 10);
        cb.lineTo(380, pos - 10);
        cb.stroke();

        sb = new StringBuilder(130);
        sb.append("Tanúsítjuk, hogy jelen belépési nyilatkozatot a Nyilatkozó jelenlétünkben ");
        sb.append("írta alá, illetve aláírását a sajátjának ismerte el.");
        Paragraph testify = new Paragraph(sb.toString(), font);
        testify.setSpacingBefore(15f);
        document.add(testify);
        signImage.setAlignment(Image.ALIGN_CENTER);
        signImage.scalePercent(33f);
        document.add(signImage);

        document.close();
        return os;
    }

    private String createUserInfo() {
        StringBuilder sb = new StringBuilder(300);
        sb.append("Alulírott ").append(user.getName());
        sb.append(" (lakcím: ").append(person.getHomePostalAddress());
        sb.append(", anyja neve: ").append(person.getMothersName());
        sb.append(", e-mail cím: ").append(person.getMail());
        sb.append(") jelen nyilatkozat aláírásával kifejezem belépési szándékom ");
        sb.append("a Schönherzes Villamosmérnökök és Informatikusok Egyesületébe ");
        sb.append("(székhely: 1115 Budapest, Bartók Béla út 152/H. Kelen Irodaház, ");
        sb.append("fszt./a., továbbiakban Egyesület). Kijelentem, hogy az Egyesületbe ");
        sb.append(cachedmsType).append("ként kívánok belépni.");
        return sb.toString();
    }

    private String createPrimaryGroupInfo() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("Az elsődleges köröm, melyet az idp.sch.bme.hu-n adtam meg: ");
        sb.append(user.getSviePrimaryMembershipText());
        return sb.toString();
    }

    private Image getSchLogo() throws IOException, DocumentException {
        if (schLogo != null) {
            return schLogo;
        }

        schLogo = Image.getInstance(getClass().getResource("resources/schlogo.png"));
        return schLogo;
    }

    private Image getSigninArea() throws IOException, DocumentException {
        if (signImage != null) {
            return signImage;
        }

        signImage = Image.getInstance(getClass().getResource("resources/signingarea.png"));
        return signImage;
    }
}
