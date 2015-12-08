package fi.om.initiative.web;

import fi.om.initiative.dto.initiative.InitiativeInfo;
import fi.om.initiative.service.InitiativeService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import java.util.List;
import java.util.Locale;

import static fi.om.initiative.web.Urls.*;
import static fi.om.initiative.web.Views.INDEX_VIEW;

@Controller
public class StaticPageController extends BaseController {

    @Resource
    private InitiativeService initiativeService;
    
    public StaticPageController() {
        super(true);
    }
    
    /*
     * Front page
     */
    @RequestMapping(FRONT)
    public RedirectView frontpage() {
        RedirectView redirectView = new RedirectView(Urls.FRONT_FI, true, true, false);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return redirectView;
    }
    
    @RequestMapping({ FRONT_FI, FRONT_SV })
    public String frontpage(Model model, Locale locale) {
        Urls urls = Urls.get(locale);

        model.addAttribute(ALT_URI_ATTR, urls.alt().frontpage());
        addPiwicIdIfNotAuthenticated(model);

        List<InitiativeInfo> initiatives = initiativeService.getFrontPageInitiatives();
        model.addAttribute("initiatives", initiatives);

        return INDEX_VIEW;
    }

    @RequestMapping(API)
    public String api() {
        return Views.API_VIEW;
    }
}
