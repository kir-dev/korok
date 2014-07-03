
package hu.sch.api.exceptions;

import hu.sch.api.response.PekError;
import hu.sch.services.exceptions.PekErrorCode;

public class AvatarNotFoundException extends PekWebException{

    public AvatarNotFoundException() {
        super(new PekError(PekErrorCode.FILE_NOT_FOUND, "User doesn't have a profile picture." ), 404);
    }


}
