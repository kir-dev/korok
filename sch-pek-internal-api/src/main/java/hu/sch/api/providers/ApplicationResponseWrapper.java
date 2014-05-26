package hu.sch.api.providers;

import hu.sch.api.response.PekResponse;
import hu.sch.api.response.PekSuccess;
import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

/**
 * Wraps a successful response in a {@link PekSuccess}.
 *
 * @author tomi
 */
@Provider
public class ApplicationResponseWrapper implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        if (!needsWrapping(responseContext)) {
            return;
        }

        Object responseEntity = responseContext.getEntity();
        responseContext.setEntity(new PekSuccess(responseEntity));
    }

    // wrap only bare (not already wrapped in a PekResponse) and
    // responses only with application/json content-type
    private boolean needsWrapping(ContainerResponseContext responseContext) {
        return !PekResponse.class.isAssignableFrom(responseContext.getEntityClass())
                && responseContext.getMediaType().equals(MediaType.APPLICATION_JSON_TYPE);
    }
}
