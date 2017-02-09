package fi.om.initiative.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Decoder;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class RecaptchaVerifierTest {

    public static final String SECRET = "secrett";
    public static final String URL = "www.example.com";

    private RecaptchaVerifier recaptchaVerifier;
    private MockRestServiceServer mockRestServer;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {

        RestTemplate restTemplate = new RestTemplate();
        mockRestServer = MockRestServiceServer.createServer(restTemplate);

        recaptchaVerifier = new RecaptchaVerifier(URL, SECRET, restTemplate);
    }

    @Test
    public void succesfull_captcha_check() {

        mockRestServer.expect(requestTo(URL))
                .andRespond(withSuccess("{\n" +
                                "  \"success\": true,\n" +
                                "  \"challenge_ts\": \"2016-01-01'T'12:12:1200\",\n" +
                                "  \"hostname\": \"some\",\n" +
                                "  \"error-codes\": []\n" +
                                "}",
                        MediaType.APPLICATION_JSON));

        MapBindingResult bindingResult = bindingResult();
        recaptchaVerifier.verify("response", bindingResult);

        assertThat(bindingResult.hasErrors(), is(false));

    }

    @Test
    public void failing_captcha_check() {

        mockRestServer.expect(requestTo(URL))
                .andRespond(withSuccess("{\n" +
                                "  \"success\": false,\n" +
                                "  \"challenge_ts\": \"2016-01-01'T'12:12:1200\",\n" +
                                "  \"hostname\": \"some\",\n" +
                                "  \"error-codes\": [\"some\"]\n" +
                                "}",
                        MediaType.APPLICATION_JSON));

        MapBindingResult bindingResult = bindingResult();
        recaptchaVerifier.verify("response", bindingResult);

        assertThat(bindingResult.getAllErrors(), hasSize(1));

        FieldError fieldError = (FieldError) bindingResult.getAllErrors().get(0);
        assertThat(fieldError.getObjectName(), is("followInitiative"));
        assertThat(fieldError.getField(), is("recaptcha"));
        assertThat(fieldError.getCode(), is("recaptcha.invalid"));

        assertThat(bindingResult.hasErrors(), is(true));

    }

    @Test
    public void network_error_throws_exception() {

        expectedException.expect(HttpServerErrorException.class);

        mockRestServer.expect(requestTo(URL))
                .andRespond(withServerError());

        recaptchaVerifier.verify("response", bindingResult());

    }

    private static MapBindingResult bindingResult() {
        return new MapBindingResult(new HashMap<>(), "");
    }

}