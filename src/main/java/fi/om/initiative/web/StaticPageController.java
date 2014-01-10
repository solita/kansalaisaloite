package fi.om.initiative.web;

import com.google.common.base.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;

import static fi.om.initiative.web.Urls.*;
import static fi.om.initiative.web.Views.INDEX_VIEW;
import static fi.om.initiative.web.Views.contextRelativeRedirect;

@Controller
public class StaticPageController extends BaseController {
    
    public StaticPageController(boolean optimizeResources, String resourcesVersion, Optional<Integer> omPiwicId) {
        super(optimizeResources, resourcesVersion, omPiwicId);
    }
    
    /*
     * Front page
     */
    @RequestMapping(FRONT)
    public String frontpage() {
        return contextRelativeRedirect(Urls.FRONT_FI);
    }
    
    @RequestMapping({ FRONT_FI, FRONT_SV })
    public String frontpage(Model model, Locale locale) {
        Urls urls = Urls.get(locale);

        model.addAttribute(ALT_URI_ATTR, urls.alt().frontpage());
        addPiwicIdIfNotAuthenticated(model);

        return INDEX_VIEW;
    }

    @RequestMapping(API)
    public String api() {
        return Views.API_VIEW;
    }
}
