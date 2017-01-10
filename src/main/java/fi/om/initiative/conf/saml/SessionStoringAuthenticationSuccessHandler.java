package fi.om.initiative.conf.saml;

import fi.om.initiative.dto.LocalizedString;
import fi.om.initiative.web.HttpUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class SessionStoringAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private HttpUserService userService;

    private String baseUri;

    public SessionStoringAuthenticationSuccessHandler(String baseUri) {
        this.baseUri = baseUri;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        SamlUser user = (SamlUser) authentication.getPrincipal();

        userService.login(
                user.getSsn(),
                user.getFirstNames(),
                user.getLastName(),
                true,
                new LocalizedString(user.getMunicipalityNameFi(), user.getMunicipalityNameSv()),
                request, response
        );

        new DefaultRedirectStrategy()
                .sendRedirect(request, response, baseUri + TargetStoringFilter.popCookieTarget(request, response));
    }
}
