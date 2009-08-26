/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.time.Time;

/**
 *
 * @author aldaris
 */
public class ByteArrayResourceStream implements IResourceStream {

    private static final long serialVersionUID = 1L;
    private Locale locale = null;
    private byte[] content = null;
    private String contentType = null;

    public ByteArrayResourceStream(byte[] content, String contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public void close() throws IOException {
    }

    public String getContentType() {
        return (contentType);
    }

    public InputStream getInputStream() throws ResourceStreamNotFoundException {
        return (new ByteArrayInputStream(content));
    }

    public Locale getLocale() {
        return (locale);
    }

    public long length() {
        return (content.length);
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Time lastModifiedTime() {
        return null;
    }
}
