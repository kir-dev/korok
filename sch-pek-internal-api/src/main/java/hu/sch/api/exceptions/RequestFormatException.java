
package hu.sch.api.exceptions;

import hu.sch.api.response.PekError;
import hu.sch.services.exceptions.PekErrorCode;
import javax.ws.rs.core.Response;

public class RequestFormatException extends PekWebException{

    public RequestFormatException(String cause) {
        super(new PekError(PekErrorCode.MISSING_CONTENT, cause), 400);
    }

}
