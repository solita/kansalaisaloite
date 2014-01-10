package fi.om.initiative.web;

import com.google.common.base.Strings;
import org.joda.time.DateTime;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CacheHeaderFilter implements Filter {
    
    private final int expiresMinutes; 
    private final boolean resourceFilter;
    private final boolean optimizeResources;
    
    public CacheHeaderFilter(boolean optimizeResources) {
        this.optimizeResources = optimizeResources;
        this.resourceFilter = true;
        this.expiresMinutes = -1; // not in use
    }

    public CacheHeaderFilter(boolean optimizeResources, int expiresMinutes) {
        this.optimizeResources = optimizeResources;
        this.resourceFilter = false;
        this.expiresMinutes = expiresMinutes;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    private void setOptimizedResourceHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "public, max-age=3153600");
        setExpires(response, DateTime.now().plusDays(365));
    }
    
    private static void setExpiresMinutes(HttpServletResponse response, int expiresMinutes) {
        setExpires(response, DateTime.now().plusMinutes(expiresMinutes));
    }

    private static void setExpires(HttpServletResponse response, DateTime expiresTime) {
        response.setDateHeader("Expires", expiresTime.getMillis());
    }
    
    
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (optimizeResources) {
            if (resourceFilter) {
                if (hasVersionInfo(request)) {
                    setOptimizedResourceHeaders(response);
                }
            } 
            else {
                if (expiresMinutes > 0) {
                    setExpiresMinutes(response, expiresMinutes);
                }
                else {
                    SecurityFilter.setNoCache(response);
                }
            }
        }
        else {
            SecurityFilter.setNoCache(response);
        }
        
        chain.doFilter(servletRequest, servletResponse);
    }

    private boolean hasVersionInfo(HttpServletRequest request) {
        String version = request.getParameter("version");
        return (!Strings.isNullOrEmpty(version) && !version.equals("dev") && !version.startsWith("$"));
    }

    @Override
    public void destroy() {
    }

}
