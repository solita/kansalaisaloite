package fi.om.initiative.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class RecaptchaVerifier {

    private final Logger log = LoggerFactory.getLogger(RecaptchaVerifier.class);

    private final String verifyUrl;
    private final String secret;
    private final RestTemplate restTemplate;

    public RecaptchaVerifier(String verifyUrl, String secret, RestTemplate restTemplate) {
        this.verifyUrl = verifyUrl;
        this.secret = secret;
        this.restTemplate = restTemplate;
    }

    public void verify(String gRecaptchaResponse, BindingResult bindingResult) {
        if (!Strings.isNullOrEmpty(secret)) {
            executeVerify(gRecaptchaResponse, bindingResult);
        }
    }

    private void executeVerify(String gRecaptchaResponse, BindingResult bindingResult) {

        ResponseEntity<RecaptchaApiResponse> response =

        restTemplate.postForEntity(verifyUrl,
                createCaptchaRequest(gRecaptchaResponse),
                RecaptchaApiResponse.class);

        if (response.getStatusCode() == HttpStatus.OK
                && response.getBody().success) {
            log.info("Recaptcha verified");

        }
        else {
            bindingResult.addError(
                    new FieldError("followInitiative", "recaptcha", "", false, new String[]{"recaptcha.invalid"}, new String[]{"recaptcha.invalid"}, "recaptcha.invalid")

            );
            log.info("Recaptcha failed: " + response.toString());
        }
    }

    private HttpEntity<MultiValueMap<String, String>> createCaptchaRequest(String gRecaptchaResponse) {
        MultiValueMap<String, String> stringStringHashMap = new LinkedMultiValueMap<>();
        stringStringHashMap.add("secret", secret);
        stringStringHashMap.add("response", gRecaptchaResponse);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return new HttpEntity<>(stringStringHashMap, headers);
    }


    public static class RecaptchaApiResponse {
        public boolean success;
        public String challenge_ts;
        public String hostname;

        @JsonProperty("error-codes")
        public List<String> errorCodes;

        @Override
        public String toString() {
            return "RecaptchaApiResponse{" +
                    "success=" + success +
                    ", challenge_ts='" + challenge_ts + '\'' +
                    ", hostname='" + hostname + '\'' +
                    ", errorCodes=" + errorCodes +
                    '}';
        }
    }

}
