package fi.om.initiative.web;

import com.google.common.collect.Maps;
import fi.om.initiative.service.FooterLinkProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;

import static fi.om.initiative.web.Urls.*;
import static fi.om.initiative.web.Views.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class ErrorController {

    @Resource
    private FooterLinkProvider footerLinkProvider;

    @RequestMapping(value = {VETUMA_ERROR_FI, VETUMA_ERROR_SV}, method = GET)
    public String vetumaLoginError(HttpServletRequest request, Locale locale, Model model) {
        addModelDefaults(locale, model);
        return ERROR_VETUMA_VIEW;
    }

    @RequestMapping(ERROR_404)
    public String notFound(Locale locale, HttpServletRequest request, HttpServletResponse response, Model model) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        addModelDefaults(locale, model);
        return ERROR_404_VIEW;
    }

    @RequestMapping(ERROR_404_GLOBAL)
    public String globalNotFound(Locale locale, HttpServletResponse response, Model model) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        addModelDefaults(locale, model);
        return ERROR_404_GLOBAL_VIEW;
    }

    @RequestMapping(ERROR_500)
    public String internalServerError(Locale locale, HttpServletRequest request, HttpServletResponse response, Model model) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        addModelDefaults(locale, model);
        if (request.getAttribute(ErrorFilter.ATTR_ERROR_CASE_ID) != null) {
            // this request came through ErrorFilter
            return ERROR_500_VIEW;
        }
        else {
            // not real error, no need to show error page
            return contextRelativeRedirect(Urls.FI.frontpage());
        }
    }

    private void addModelDefaults(Locale locale, Model model) {
        Urls urls = Urls.get(locale);
        model.addAttribute("urls", urls);
        model.addAttribute("locale", urls.getLang());
        model.addAttribute("footerLinks", footerLinkProvider.getFooterLinks(locale));
        model.addAttribute("superSearchEnabled", false);

        Map<String, HelpPage> values = Maps.newHashMap();
        for (HelpPage value : HelpPage.values()) {
            values.put(value.name(), value);
        }
        model.addAttribute(HelpPage.class.getSimpleName(), values);
    }



}
