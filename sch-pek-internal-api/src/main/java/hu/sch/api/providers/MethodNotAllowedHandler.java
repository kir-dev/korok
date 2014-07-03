package hu.sch.api.providers;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import hu.sch.api.response.PekError;
import hu.sch.services.exceptions.PekErrorCode;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Provider
public class MethodNotAllowedHandler implements ExceptionMapper<NotAllowedException> {

    @Context
    private HttpServletRequest request;

    @Override
    public Response toResponse(NotAllowedException exception) {
        final String cause = String.format("%s is not allowed for path (%s).", request.getMethod(), request.getRequestURI());
        PekError error = new PekError(PekErrorCode.METHOD_NOT_ALLOWED, cause);
        return Response
                .fromResponse(exception.getResponse())
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
