package hu.sch.web.wicket.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

/**
 *
 * @author aldaris
 */
public class ByteArrayResourceStream extends AbstractResourceStream {

    private static final long serialVersionUID = 1L;
    private byte[] content = null;
    private String contentType = null;

    public ByteArrayResourceStream(byte[] content, String contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public String getContentType() {
        return (contentType);
    }

    @Override
    public Bytes length() {
        return Bytes.bytes(content.length);
    }

    @Override
    public InputStream getInputStream() throws ResourceStreamNotFoundException {
        return (new ByteArrayInputStream(content));
    }
}
