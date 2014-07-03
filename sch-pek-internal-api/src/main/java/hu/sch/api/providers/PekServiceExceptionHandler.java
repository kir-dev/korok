package hu.sch.api.providers;

import hu.sch.api.response.PekError;
import hu.sch.services.exceptions.PekException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class PekServiceExceptionHandler implements ExceptionMapper<PekException> {

    private static final Logger logger = LoggerFactory.getLogger(PekServiceExceptionHandler.class);

    @Override
    public Response toResponse(PekException ex) {
        logger.info("Application exception occured", ex);
        // TODO: set status properly.
        return Response
                .status(500)
                .type(MediaType.APPLICATION_JSON)
                .entity(new PekError(ex))
                .build();
    }

}
