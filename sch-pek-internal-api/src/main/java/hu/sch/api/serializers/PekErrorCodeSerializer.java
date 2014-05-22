package hu.sch.api.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import hu.sch.util.exceptions.PekErrorCode;
import java.io.IOException;

/**
 *
 * @author tomi
 */
public final class PekErrorCodeSerializer extends JsonSerializer<PekErrorCode>{

    @Override
    public void serialize(PekErrorCode t, JsonGenerator generator, SerializerProvider sp) throws IOException, JsonProcessingException {
        generator.writeStartObject();
        generator.writeFieldName("code");
        generator.writeNumber(t.getCode());
        generator.writeFieldName("message");
        generator.writeString(t.getShortMessage());
        generator.writeEndObject();
    }

}
