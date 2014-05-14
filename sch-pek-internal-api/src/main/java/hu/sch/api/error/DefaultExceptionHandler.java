package hu.sch.api.error;

import hu.sch.api.response.PekError;
import hu.sch.util.exceptions.PekException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.jboss.resteasy.spi.LoggableFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tomi
 */
@Provider
public class DefaultExceptionHandler implements ExceptionMapper<Exception>{

    private static final Logger logger = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @Override
    public Response toResponse(Exception exception) {
        logger.error("Unhandled exception", exception);

        if (exception instanceof PekException) {
            return handlePekException((PekException) exception);
        }
        if (exception.getCause() != null && exception.getCause() instanceof PekException) {
            return handlePekException((PekException) exception.getCause());
        }
        if (exception instanceof LoggableFailure) {
            return handleRestEasyBuiltInException((LoggableFailure) exception);
        }

        return buildResponse(500, PekError.unspecified(exception.getMessage()));
    }

    private Response handlePekException(PekException exception) {
        return buildResponse(500, new PekError(exception));
    }

    private Response handleRestEasyBuiltInException(LoggableFailure failure) {
        return buildResponse(failure.getErrorCode(), PekError.unspecified(failure.getMessage()));
    }

    private Response buildResponse(int status, PekError error) {
        return Response
                .status(status)
                .entity(error)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();

    }
}
