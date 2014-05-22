package hu.sch.api.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.servlet.ReadListener;
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

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
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

        @Override
        public boolean isFinished() {
            return stream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener rl) {
            throw new UnsupportedOperationException("read listener is not supported");
        }
    }
}
