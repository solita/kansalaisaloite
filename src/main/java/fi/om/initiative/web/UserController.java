package fi.om.initiative.web;

import static fi.om.initiative.web.Urls.MY_ACCOUNT_FI;
import static fi.om.initiative.web.Urls.MY_ACCOUNT_SV;
import static fi.om.initiative.web.Views.contextRelativeRedirect;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import fi.om.initiative.dto.User;
import fi.om.initiative.service.Role;
import fi.om.initiative.service.UserService;

@Controller
public class UserController extends BaseController {

    @Resource
    UserService userService;
    
    public UserController(boolean optimizeResources, String resourcesVersion) {
        super(optimizeResources, resourcesVersion);
    }
    
    @RequestMapping(value={MY_ACCOUNT_FI, MY_ACCOUNT_SV}, method=POST)
    public String registerPost(Model model, Locale locale) {
        Urls urls = Urls.get(locale);
        
        // Get or *CREATE* registered user
        userService.currentAsRegisteredUser();
        return contextRelativeRedirect(urls.myAccount());
    }
    
    @RequestMapping(value={MY_ACCOUNT_FI, MY_ACCOUNT_SV}, method=GET)
    public String registerGet(Model model, Locale locale) {
        Urls urls = Urls.get(locale);
        model.addAttribute(ALT_URI_ATTR, urls.alt().myAccount());
        
        User user = userService.getUserInRole(Role.AUTHENTICATED);
        model.addAttribute("currentUser", user);
        return Views.REGISTERED_USER;
    }
    
}
