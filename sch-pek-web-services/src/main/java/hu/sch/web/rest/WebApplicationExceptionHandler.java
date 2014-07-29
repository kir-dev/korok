package hu.sch.web.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class WebApplicationExceptionHandler implements ExceptionMapper<WebApplicationException> {

    private static final Logger logger = LoggerFactory.getLogger(WebApplicationExceptionHandler.class);
    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(WebApplicationException ex) {
        if (ex.getResponse().getStatus() == 404) {
            logger.info("URL or resource cannot be found: {}", uriInfo.getPath());
        } else {
            logger.warn("Application exception occured", ex);
        }

        return ex.getResponse();
    }

}
