package hu.sch.api.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.sch.api.providers.ObjectMapperFactory;
import hu.sch.api.response.PekError;
import hu.sch.util.config.Configuration;
import hu.sch.services.exceptions.PekErrorCode;
import java.io.IOException;
import java.io.StringWriter;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tomi
 */
public class RequestSignatureFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestSignatureFilter.class);
    private static final String SIGNATURE_KEY = "X-Pek-Signature";
    private static final String SIGNATURE_TIMESTAMP_KEY = "X-Pek-Timestamp";
    @Inject
    private Configuration config;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // nothing to do
    }

    @Override
    public void destroy() {
        // nothing to do
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (config.skipRequestSignature()) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String secret = config.getInternalApiSecret();

        long timestamp = 0;
        try {
            String timestampString = req.getHeader(SIGNATURE_TIMESTAMP_KEY);
            if (timestampString == null) {
                // no header is present for timestamp
                sendSignatureError(res, new PekError(PekErrorCode.INVALID_REQUEST_TIMESTAMP, "Timestamp is missing."));
                return;
            }

            timestamp = Long.parseLong(timestampString);
        } catch (NumberFormatException ex) {
            logger.warn("Invalid timestamp format: {} on path: {}", req.getHeader(SIGNATURE_TIMESTAMP_KEY), req.getRequestURI());
            sendSignatureError(res, new PekError(PekErrorCode.INVALID_REQUEST_TIMESTAMP, ex.getMessage()));
            return;
        }

        RereadableHttpServletRequestWrapper wrappedRequest = new RereadableHttpServletRequestWrapper(req);
        RequestSignature sig = new RequestSignature(getUrl(req), wrappedRequest.getRawBody(), req.getHeader(SIGNATURE_KEY), timestamp, secret);
        RequestSignatureResult result = sig.checkSignature();

        if (result != RequestSignatureResult.OK) {
            logger.warn("Invalid request signature: {}", result);
            sendSignatureError(res, new PekError(PekErrorCode.INVALID_REQUEST_SIGNATURE, "Invalid signature."));
        } else {
            chain.doFilter(wrappedRequest, response);
        }
    }

    private String readBody(HttpServletRequest request) throws IOException {
        StringWriter bodyWriter = new StringWriter();
        IOUtils.copy(request.getReader(), bodyWriter);

        return bodyWriter.toString();
    }

    private String getUrl(HttpServletRequest request) {
        StringBuilder url = new StringBuilder(request.getRequestURI());
        if (request.getQueryString() != null) {
            url.append('?').append(request.getQueryString());
        }

        return url.toString();

    }

    private void sendSignatureError(HttpServletResponse res, PekError error) {
        try {
            res.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.setCharacterEncoding("UTF-8");

            ObjectMapper m = new ObjectMapperFactory(config).createMapper();
            m.writeValue(res.getWriter(), error);
        } catch (IOException ex) {
            logger.warn("Could not send signature error.", ex);
        }
    }
}
