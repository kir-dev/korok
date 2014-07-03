package hu.sch.api.providers;

import hu.sch.api.response.PekError;
import hu.sch.services.exceptions.PekErrorCode;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author tomi
 */
@Provider
public class NotFoundExceptionHandler implements ExceptionMapper<NotFoundException> {

    private static final Logger logger = LoggerFactory.getLogger(NotFoundExceptionHandler.class);

    @Override
    public Response toResponse(NotFoundException e) {
        logger.warn("Resource was not found: {}", e.getMessage());
        return Response
                .fromResponse(e.getResponse())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new PekError(PekErrorCode.RESOURCE_NOT_FOUND, e.getMessage()))
                .build();
    }

}
