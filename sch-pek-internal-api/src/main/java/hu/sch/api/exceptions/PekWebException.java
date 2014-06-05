package hu.sch.api.exceptions;

import hu.sch.api.response.PekError;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This exception is for recoverable errors in the application.
 *
 * Throwing this exception means something went wrong, but not terribly wrong
 * and some action has to be taken on the client side.
 *
 * To create a new type of exception subclass this and set up a response to send
 * back to the client.
 *
 * @author tomi
 */
public class PekWebException extends WebApplicationException {

    public PekWebException(Response response) {
        super(prepareResponse(response));
    }

    public PekWebException(String message, Response response) {
        super(message, prepareResponse(response));
    }

    public PekWebException(PekError error, int status) {
        super(preparePekErrorResponse(error, status));
    }

    private static Response prepareResponse(Response response) {
        return Response
                .fromResponse(response)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

    private static Response preparePekErrorResponse(PekError error, int status) {
        return Response
                .status(status)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(error)
                .build();
    }
}
