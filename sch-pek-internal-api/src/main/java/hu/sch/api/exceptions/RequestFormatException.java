
package hu.sch.api.exceptions;

import hu.sch.api.response.PekError;
import hu.sch.util.exceptions.PekErrorCode;
import javax.ws.rs.core.Response;

public class RequestFormatException extends PekWebException{

    public RequestFormatException(String cause) {
        super(new PekError(PekErrorCode.REQUEST_FORMAT_INVALID, cause), 400);
    }

}
