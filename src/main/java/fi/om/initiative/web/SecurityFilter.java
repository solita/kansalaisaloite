package fi.om.initiative.web;

import java.io.IOException;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.util.NestedServletException;
import org.springframework.web.util.UrlPathHelper;

import com.google.common.base.Throwables;

import fi.om.initiative.service.AuthenticationRequiredException;

public class SecurityFilter implements Filter {
    
    @Resource HttpUserService userService;
    
    private UrlPathHelper urlPathHelper = new UrlPathHelper();
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public static void setNoCache(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache");                                   // HTTP 1.0
        response.setDateHeader("Expires", 0);                                       // Proxies
    }
    
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        setNoCache(response);
        
        try {
            userService.verifyCSRFToken(request);
            chain.doFilter(servletRequest, servletResponse);
        } catch (CSRFException e) {
            csrfException(e, request, response);
        } catch (NestedServletException e) {
            Throwable t = e.getCause();
            if (t instanceof AuthenticationRequiredException) {
                
                StringBuilder target = new StringBuilder(128);
                target.append(this.urlPathHelper.getOriginatingRequestUri(request));
                
                if (request.getQueryString() != null) {
                    target.append("?");
                    target.append(request.getQueryString());
                }
                response.sendRedirect(Urls.get((Locale) request.getAttribute("requestLocale")).login(target.toString()));

            } else if (t instanceof CSRFException) {
                csrfException(e, request, response);
            } else {
                propagateException(e);
            }
        }
    }

    private void csrfException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        userService.logout(request, response);
        propagateException(e);
    }
    
    private void propagateException(Exception e) throws IOException, ServletException {
        Throwables.propagateIfInstanceOf(e, IOException.class);
        Throwables.propagateIfInstanceOf(e, ServletException.class);
        Throwables.propagate(e);
    }

    @Override
    public void destroy() {
    }

}
