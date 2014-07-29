package hu.sch.web;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;

public class SessionIdFixFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse)response;
        HttpSession session = req.getSession();

        if (session.isNew()) {
            res.sendRedirect(buildRedirectUri(req));
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean needsRedirect(HttpServletRequest request) {
        HttpSession session = request.getSession();

        return session.isNew() && !request.isRequestedSessionIdFromCookie();
    }

    private String buildRedirectUri(HttpServletRequest request) {
        String queryStr = request.getQueryString();
        if (StringUtils.isNotEmpty(queryStr)) {
            return request.getRequestURI().concat("?").concat(queryStr);
        }

        return request.getRequestURI();
    }

    @Override
    public void init(FilterConfig fc) throws ServletException {
        // do nothing
    }

    @Override
    public void destroy() {
        // do nothing
    }

}
