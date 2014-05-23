package hu.sch.api.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import hu.sch.util.exceptions.PekErrorCode;
import java.io.IOException;

/**
 * Serializer for {@link PekErrorCode}. It dumps all the info into the host
 * object, does not create an enclosing object for the values.
 *
 * It must be used in cunjunction with JsonUnwrapped annotation.
 *
 * For example:
 *
 *    @JsonUnwrapped
 *    @JsonSerialize(using = PekErrorCodeSerializer.class)
 *    public PekErrorCode getError() {
 *        return error;
 *    }
 *
 * @author tomi
 */
public final class PekErrorCodeSerializer extends JsonSerializer<PekErrorCode> {

    @Override
    public void serialize(PekErrorCode t, JsonGenerator generator, SerializerProvider sp) throws IOException, JsonProcessingException {
        // dump values into host object
        generator.writeFieldName("code");
        generator.writeNumber(t.getCode());
        generator.writeFieldName("message");
        generator.writeString(t.getShortMessage());
    }

    @Override
    public boolean isUnwrappingSerializer() {
        return true;
    }
}
