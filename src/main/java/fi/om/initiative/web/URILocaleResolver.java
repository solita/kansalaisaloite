package fi.om.initiative.web;

import fi.om.initiative.util.Locales;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class URILocaleResolver implements LocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale requestLocale = (Locale)request.getAttribute("requestLocale");
        
        if (requestLocale != null) { // locale already resolved for this request
            return requestLocale;
        } 
        
        else {

            // Iframe has different uri to prevent usage of securityFilter, so it's locale must be checked here separatedly.
            if (request.getRequestURI().startsWith(Urls.IFRAME_SV_BASE)) {
                return Locales.LOCALE_SV;
            }
            else if (request.getRequestURI().startsWith(Urls.IFRAME_FI_BASE)) {
                return Locales.LOCALE_FI;
            }

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
