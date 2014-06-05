package hu.sch.api.providers;

import com.fasterxml.jackson.databind.JsonMappingException;
import hu.sch.api.response.PekError;
import hu.sch.util.exceptions.PekErrorCode;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author tomi
 */
@Provider
public class BadJSONFormatHandler implements ExceptionMapper<JsonMappingException> {

    @Override
    public Response toResponse(JsonMappingException exception) {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(fromException(exception))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

    private PekError fromException(JsonMappingException exception) {
        return new PekError(PekErrorCode.INVALID_JSON_FORMAT, exception.getMessage());
    }

}
