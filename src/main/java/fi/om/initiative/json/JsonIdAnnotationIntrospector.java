package fi.om.initiative.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

import java.io.IOException;

public class JsonIdAnnotationIntrospector extends JacksonAnnotationIntrospector {
    
    private final String baseUrl; 

    public JsonIdAnnotationIntrospector(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public Object findSerializer(Annotated a) {
        final JsonId ann = a.getAnnotation(JsonId.class);
        if (ann != null) {
            return new JsonSerializer<Long>() {
    
                @Override
                public void serialize(Long value, JsonGenerator jgen,
                        SerializerProvider provider) throws IOException,
                        JsonProcessingException {
                    jgen.writeString(baseUrl + ann.path().replace("{id}", value.toString()));
                }
            };
        } else {
            return super.findSerializer(a);
        }
    }

}
