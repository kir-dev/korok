/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components.customlinks;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import hu.sch.domain.SvieMembershipType;
import hu.sch.domain.User;
import hu.sch.domain.config.Configuration;
import hu.sch.domain.profile.Person;
import hu.sch.services.LdapManagerLocal;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.web.kp.pages.user.ShowUser;
import hu.sch.web.kp.util.ByteArrayResourceStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.ejb.EJB;
import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.IResourceStream;

/**
 *
 * @author aldaris
 */
public class SvieRegPdfLink extends Panel {

    @EJB(name = "LdapManagerBean")
    LdapManagerLocal ldapManager;
    private static Logger log = Logger.getLogger(SvieRegPdfLink.class);
    private static final long serialVersionUID = 1L;
    private static Image schLogo;
    private final User user;
    private Person person;
    private String cachedmsType;
    private static StringBuilder sb;
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

            sb = new StringBuilder(150);
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
        } catch (Exception ex) {
            log.warn("Error while creating static content for PDF", ex);
        }
    }

    public SvieRegPdfLink(String id, User user2) {
        super(id);
        this.user = user2;
        cachedmsType = user.getSvieMembershipType().toString();
        add(new Link<Void>("pdfLink") {

            public void onClick() {
                try {
                    person = ldapManager.getPersonByVirId(user.getId().toString());
                } catch (PersonNotFoundException ex) {
                    getSession().error("Hiba a PDF generálása közben.");
                    throw new RestartResponseException(ShowUser.class);
                }
                try {
                    IResourceStream resourceStream = new ByteArrayResourceStream(
                            ((ByteArrayOutputStream) generatePdf()).toByteArray(),
                            "application/pdf");
                    getRequestCycle().setRequestTarget(new ResourceStreamRequestTarget(resourceStream) {

                        @Override
                        public String getFileName() {
                            return ("export_" + person.getNeptun() + ".pdf");
                        }
                    });
                } catch (Exception ex) {
                    getSession().error("Hiba történt a PDF generálása közben!");
                    ex.printStackTrace();
                }
            }
        });
    }

    private OutputStream generatePdf() throws DocumentException, IOException {
        Document document = new Document(PageSize.A4, 72, 72, 56, 56);
        final OutputStream os = new ByteArrayOutputStream();
        PdfWriter pdfWriter = PdfWriter.getInstance(document, os);
        document.open();
        Image logo = getImage();
        logo.setAlignment(Image.ALIGN_CENTER);
        logo.scalePercent(33f);
        document.add(logo);

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

        sb = new StringBuilder(200);
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
        pos = pdfWriter.getVerticalPosition(true);
        cb.setFontAndSize(arialUnicode, 12f);
        cb.moveTo(130, pos - 25);
        cb.showText("........................................");
        cb.moveTo(350, pos - 25);
        cb.showText("........................................");
        cb.stroke();
        cb.newlineText();
        cb.moveTo(180, pos - 40);
        cb.showText("név");
        cb.moveTo(400, pos - 40);
        cb.showText("név");
        cb.stroke();
        cb.newlineText();
        cb.moveTo(130, pos - 65);
        cb.showText("........................................");
        cb.moveTo(350, pos - 65);
        cb.showText("........................................");
        cb.stroke();
        cb.newlineText();
        cb.moveTo(180, pos - 80);
        cb.showText("lakcím");
        cb.moveTo(400, pos - 80);
        cb.showText("lakcím");
        cb.stroke();

        document.close();
        return os;
    }

    private String createUserInfo() {
        sb = new StringBuilder(300);
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

    private Image getImage() throws IOException, DocumentException {
        if (schLogo != null) {
            return schLogo;
        }
        InputStream schLogoStream = getClass().getResourceAsStream("resources/schlogo.png");

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] bytes = new byte[512];
        int readBytes = 0;
        while ((readBytes = schLogoStream.read(bytes)) > 0) {
            output.write(bytes, 0, readBytes);
        }
        schLogo = Image.getInstance(output.toByteArray());
        schLogoStream.close();
        return schLogo;
    }
}