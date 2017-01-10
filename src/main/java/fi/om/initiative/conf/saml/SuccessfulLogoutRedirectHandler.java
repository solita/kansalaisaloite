package fi.om.initiative.conf.saml;

import fi.om.initiative.web.Urls;
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
        String targetUri = TargetStoringFilter.popCookieTarget(request, response);

        // The first idea was to redirect the user to the previous page after logout.
        // But the problem are pages that are not visible for unauthenticated users. The user would end up to 403 page after logout.
        // Best solution would be just be to redirect user to frontpage if the default target page would give 403,
        // but unfortunately there is no time for that now so let's just get the user to localized frontpage after logout.

        // Redirect to default logout page that's responsible for setting the logout success message
        String localizedFrontPageUri =
                targetUri.startsWith(Urls.FRONT_SV) ? Urls.LOGOUT_SV : Urls.LOGOUT_FI;

        new DefaultRedirectStrategy()
                .sendRedirect(request, response, baseUri + localizedFrontPageUri);

    }
}
