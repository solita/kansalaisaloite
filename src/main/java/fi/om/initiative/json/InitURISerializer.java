package fi.om.initiative.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.om.initiative.dto.InitURI;

import java.io.IOException;

public class InitURISerializer extends JsonSerializer<InitURI> {

    @Override
    public void serialize(InitURI value, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        jgen.writeString(value.toString());
    }
}
