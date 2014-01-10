package fi.om.initiative.web;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.LocaleResolver;

import fi.om.initiative.util.Locales;

public class URILocaleResolver implements LocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale requestLocale = (Locale)request.getAttribute("requestLocale");
        
        if (requestLocale != null) { // locale already resolved for this request
            return requestLocale;
        } 
        
        else {
            String uri = request.getRequestURI();
            
            if (uri.startsWith(request.getContextPath() + Urls.FRONT_SV)) {
                requestLocale = Locales.LOCALE_SV;
            } 
            else if (uri.startsWith(request.getContextPath() + Urls.FRONT_FI)) {
                requestLocale = Locales.LOCALE_FI;
            } 
            else {
                // if it is not possible to resolve from current uri, use referer 
                String ref = request.getHeader("Referer");
                if (ref != null && ref.startsWith(Urls.SV.frontpage())) {
                    requestLocale = Locales.LOCALE_SV;
                }
                else {
                    requestLocale = Locales.LOCALE_FI;
                }
            }
            request.setAttribute("requestLocale", requestLocale);
            return requestLocale;
        }
        
    }

    @Override
    public void setLocale(HttpServletRequest request,
            HttpServletResponse response, Locale locale) {
        throw new UnsupportedOperationException("Cannot change HTTP Request URI");
    }

}
