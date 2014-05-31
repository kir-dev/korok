
package hu.sch.api.exceptions;

import hu.sch.api.response.PekError;
import hu.sch.util.exceptions.PekErrorCode;

public class AvatarNotFoundException extends PekWebException{

    public AvatarNotFoundException() {
        super(new PekError(PekErrorCode.RESOURCE_NOT_FOUND, "User doesn't have a profile picture." ), 404);
    }


}
