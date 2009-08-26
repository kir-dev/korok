/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components.customlinks;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import hu.sch.web.kp.util.ByteArrayResourceStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.IResourceStream;

/**
 *
 * @author aldaris
 */
public class PdfLink extends Link {

    private static final long serialVersionUID = 1L;

    public PdfLink(String id) {
        super(id);
    }

    public void onClick() {
        try {
            IResourceStream resourceStream = new ByteArrayResourceStream(
                    ((ByteArrayOutputStream) generatePdf()).toByteArray(),
                    "application/pdf");
            getRequestCycle().setRequestTarget(new ResourceStreamRequestTarget(resourceStream) {

                @Override
                public String getFileName() {
                    return ("export.pdf");
                }
            });
        } catch (Exception ex) {
            error("Hiba történt a PDF generálása közben!");
            ex.printStackTrace();
        }
    }

    private OutputStream generatePdf() throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        final OutputStream os = new ByteArrayOutputStream();
        PdfWriter pdfWriter = PdfWriter.getInstance(document, os);
        document.open();
        document.add(new Paragraph("some sample text"));
        document.add(Image.getInstance(new URL("http://aldaris.sch.bme.hu/stuff/feature.png")));
        document.close();
        return os;
    }
}

