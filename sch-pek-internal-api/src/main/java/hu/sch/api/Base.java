package hu.sch.api;

import hu.sch.api.response.PekError;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Common base class for all api endpoints. Provides Accept and Content-Type
 * headers.
 *
 * @author tomi
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public abstract class Base {

    protected Response respondWithNotFound(String cause) {
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(PekError.notFound(cause))
                .build();
    }
}
