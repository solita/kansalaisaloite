package fi.om.initiative.web;

import com.google.common.base.Strings;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.regex.Pattern;

public abstract class BaseLoginController extends BaseController {
    
    private final String baseUrl;
    
    /**
     * Only relative (local) URI's allowed.
     */
    private static final Pattern ILLEGAL_TARGET = Pattern.compile(":|//");

    public BaseLoginController(String baseUrl) {
        super(false);
        this.baseUrl = baseUrl;
    }
    
    protected String getValidLoginTarget(String target, Urls urls) {
        if (!Strings.isNullOrEmpty(target) 
                && (!ILLEGAL_TARGET.matcher(target).find() || target.startsWith(baseUrl))
                && !urls.isLoginPage(target)) {
            return target;
        } else {
            return urls.frontpage();
        }
    }

    protected View redirect(String target) {
        return new RedirectView(target, false, true, false);
    }

    @RequestMapping(value = "/session", method = RequestMethod.POST)
    public @ResponseBody String createSession(HttpServletRequest request, HttpServletResponse response) {
        HttpSession httpSession = userService.prepareForLogin(request);
        return userService.getOrCreateCSRFToken(httpSession, response);
    }
    
}
