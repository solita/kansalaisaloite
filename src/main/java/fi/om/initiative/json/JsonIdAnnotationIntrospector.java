package fi.om.initiative.json;

import java.io.IOException;
import java.lang.annotation.Annotation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

public class JsonIdAnnotationIntrospector extends JacksonAnnotationIntrospector {
    
    private final String baseUrl; 

    public JsonIdAnnotationIntrospector(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public boolean isHandled(Annotation ann) {
        Class<? extends Annotation> acls = ann.annotationType();
        return acls.getAnnotation(JsonId.class) != null || super.isHandled(ann);
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
