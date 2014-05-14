package hu.sch.api.serializers;

import hu.sch.util.exceptions.PekErrorCode;
import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

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
