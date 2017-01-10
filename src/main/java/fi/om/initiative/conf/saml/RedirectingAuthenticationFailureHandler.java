package fi.om.initiative.conf.saml;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

public class RedirectingAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private String baseUrl;

    public RedirectingAuthenticationFailureHandler(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String targetUri = TargetStoringFilter.popCookieTarget(request, response);

        // Strip get parameters from redirect on failure to prevent re-login-loop
        // when users cancels login on eg. voting

        // IDP Currently does not tell us if the user has cancelled the authentication or there were failures during it.
        // Currently we just have to trust that IDP shows some nice error for the user if the authentication fails,
        // because we do not have any way to tell if the authentication was failed or cancelled.

        String path = new URL(baseUrl + targetUri).getPath();

        new DefaultRedirectStrategy()
                .sendRedirect(request, response, baseUrl + path);

    }
}
