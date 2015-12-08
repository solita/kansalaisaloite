package fi.om.initiative.web;

import fi.om.initiative.dto.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

import static fi.om.initiative.util.Locales.asLocalizedString;
import static fi.om.initiative.web.Urls.*;
import static fi.om.initiative.web.Views.DUMMY_LOGIN_VIEW;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@Profile("dev")
public class DevController extends BaseLoginController {

    public DevController(String baseUrl, boolean optimizeResources, String resourcesVersion) {
        super(baseUrl);
    }
    
    /*
     * Login
     */
    @RequestMapping(value={LOGIN_FI, LOGIN_SV}, method=GET)
    public String loginGet(@RequestParam(required=false) String target, Model model, HttpServletRequest request, HttpServletResponse response) {
        SecurityFilter.setNoCache(response);
        response.setContentType("text/html;charset=ISO-8859-1");
        User user = userService.getCurrentUser(false);
        if (user.isAuthenticated()) {
            return Views.contextRelativeRedirect(Urls.FRONT);
        } else {
            userService.prepareForLogin(request);
            model.addAttribute("target", target);
            return DUMMY_LOGIN_VIEW;
        }
    }

    @RequestMapping(value={LOGIN_FI, LOGIN_SV}, method=POST)
    public View loginPost(
            @RequestParam(required=true) String ssn, 
            @RequestParam(required=true) String firstName, 
            @RequestParam(required=true) String lastName, 
            @RequestParam(required=true) String homeMunicipality, 
            @RequestParam(required=false, defaultValue="false") boolean finnishCitizen,
            @RequestParam(required=false) String target, 
            Model model, 
            Locale locale,
            HttpServletRequest request,
            HttpServletResponse response) {
        Urls urls = Urls.get(locale);
        target = getValidLoginTarget(target, urls);
        userService.login(ssn, firstName, lastName, finnishCitizen, asLocalizedString(homeMunicipality, homeMunicipality), request, response);
        return redirect(target);
    }
    
    @RequestMapping(value={LOGOUT_FI, LOGOUT_SV}, method=GET)
    public String logout(Locale locale, HttpServletRequest request, HttpServletResponse response) {
        Urls urls = Urls.get(locale);
        userService.logout(request, response);
        return redirectWithMessage(urls.frontpage(), RequestMessage.LOGOUT, request);
    }



    
}
