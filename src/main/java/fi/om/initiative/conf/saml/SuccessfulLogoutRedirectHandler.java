package fi.om.initiative.conf.saml;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SuccessfulLogoutRedirectHandler implements LogoutSuccessHandler {

    private String baseUri;

    public SuccessfulLogoutRedirectHandler(String baseUri) {
        this.baseUri = baseUri;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUri = TargetStoringFilter.popTarget(request, response);

        new DefaultRedirectStrategy()
                .sendRedirect(request, response, baseUri + targetUri);

    }
}
