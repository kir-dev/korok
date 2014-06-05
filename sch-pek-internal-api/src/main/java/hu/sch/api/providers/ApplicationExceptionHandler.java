
package hu.sch.api.providers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ApplicationExceptionHandler implements ExceptionMapper<WebApplicationException>{

    private static final Logger logger = LoggerFactory.getLogger(ApplicationExceptionHandler.class);

    @Override
    public Response toResponse(WebApplicationException exception) {
        // stacktrace is not important, these are recoverable errors
        logger.info("An application exception occured. Message: {}, type: {}", exception.getMessage(), exception.getClass());
        return exception.getResponse();
    }

}
