package hu.sch.api.filter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import hu.sch.services.config.Configuration;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
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
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String secret = config.getInternalApiSecret();

        long timestamp = 0;
        try {
            timestamp = Long.parseLong(req.getHeader(SIGNATURE_TIMESTAMP_KEY));
        } catch (NumberFormatException ex) {
            logger.warn("Invalid timestamp format: {} on path: {}", req.getHeader(SIGNATURE_TIMESTAMP_KEY), req.getRequestURI());
            sendSignatureError(res, "invalid timestamp");
            return;
        }

        RereadableHttpServletRequestWrapper wrappedRequest = new RereadableHttpServletRequestWrapper(req);
        RequestSignature sig = new RequestSignature(getUrl(req), readBody(req), req.getHeader(SIGNATURE_KEY), timestamp, secret);
        RequestSignatureResult result = sig.checkSignature();

        if (result != RequestSignatureResult.OK) {
            logger.warn("Invalid request signature: {}", result);
            sendSignatureError(res, "invalid signature");
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

    private void sendSignatureError(HttpServletResponse res, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpServletResponse.SC_BAD_REQUEST);
        response.put("errorMessage", message);

        try {
            res.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.setCharacterEncoding("UTF-8");

            Gson gson = new Gson();
            gson.toJson(response, Map.class, new JsonWriter(res.getWriter()));
        } catch (IOException ex) {
            logger.warn("Could not send signature error.", ex);
        }
    }
}
