package hu.sch.api;

import hu.sch.api.response.EntityView;
import hu.sch.api.response.PekError;
import hu.sch.util.exceptions.PekErrorCode;
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

    protected Response respondWithEntityOrNotFound(Object entity) {
        if (entity != null) {
            return Response.ok(entity).build();
        }
        PekError error = new PekError(PekErrorCode.ENTITY_NOTFOUND, "entity cannot be found");
        return Response.status(Response.Status.NOT_FOUND).entity(error).build();
    }

    protected Response respondWithEntityOrNotFound(EntityView entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity cannot be null");
        }

        if (entity.hasEntity()) {
            return Response.ok(entity).build();
        }
        PekError error = new PekError(PekErrorCode.ENTITY_NOTFOUND,
                String.format("%s cannot be found", entity.getEntityName()));
        return Response.status(Response.Status.NOT_FOUND).entity(error).build();
    }

    protected Response respondWithNotFound(String cause) {
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(PekError.notFound(cause))
                .build();
    }
}
