package hu.sch.api.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author tomi
 */
public class RereadableHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private byte[] rawBody = null;
    private HttpServletRequest request;

    public RereadableHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.request = request;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (rawBody == null) {
            rawBody = IOUtils.toByteArray(request.getInputStream());
        }
        return new ByteServletInputStream(rawBody);
    }

    private static class ByteServletInputStream extends ServletInputStream {

        private ByteArrayInputStream stream;

        ByteServletInputStream(byte[] content) {
            stream = new ByteArrayInputStream(content);
        }

        @Override
        public int read() throws IOException {
            return stream.read();
        }
    }
}
