package fi.om.initiative.web;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import fi.om.initiative.validation.JSONPCallbackValidator;

public class JsonpMessageConverter<T> extends AbstractHttpMessageConverter<JsonpObject<T>> {
    
    private final HttpMessageConverter<T> jsonConverter;
    
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    
    private final MediaType jsonMediaType = new MediaType("application", "json", DEFAULT_CHARSET);
    
    private static byte[] OPEN_BRACKET = "(".getBytes(DEFAULT_CHARSET);
    
    private static byte[] MESSAGE_END = ");".getBytes(DEFAULT_CHARSET);
            
    public JsonpMessageConverter(HttpMessageConverter<T> jsonConverter) {
        super(new MediaType("application", "javascript", DEFAULT_CHARSET));
        this.jsonConverter = jsonConverter;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return JsonpObject.class.equals(clazz);
    }

    @Override
    protected JsonpObject<T> readInternal(Class<? extends JsonpObject<T>> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void writeInternal(JsonpObject<T> t, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        String callback = t.getCallback();
        if (JSONPCallbackValidator.isValidJSONPCallback(callback)) {
            OutputStream out = outputMessage.getBody();
            out.write(callback.getBytes(DEFAULT_CHARSET));
            out.write(OPEN_BRACKET);
            
            jsonConverter.write(t.getObject(), jsonMediaType, outputMessage);
            
            out.write(MESSAGE_END);
        } else {
            throw new IllegalArgumentException("\"" + callback + "\" is not a valid JS identifier.");
        }
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        // NOTE It is impossible to check whether jsonConverter supports the actual object type 
        // of given JsonObject due to limitations of Java's generics (i.e. type erasure)
        return super.canWrite(clazz, mediaType);
    }

}
