package fi.om.initiative.web;

import com.google.common.base.Strings;
import fi.om.initiative.dao.UserDao;
import fi.om.initiative.dto.LocalizedString;
import fi.om.initiative.dto.User;
import fi.om.initiative.service.*;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import javax.annotation.Nullable;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;


public class HttpUserServiceImpl implements HttpUserService {
    
    private static final String CURRENT_USER_SESSION_ATTR = "CurrentUser";
    
    private static final String CSRF_TOKEN_NAME = "CSRFToken";
    
    private static final int CSRF_TOKEN_LENGTH = 24;
    
    private EncryptionService encryptionService;

    private boolean disableSecureCookie;

    private UserDao userDao;
    
    private static final User ANON = new User();


    public HttpUserServiceImpl(UserDao userDao, EncryptionService encryptionService, boolean disableSecureCookie) {
        this.userDao = userDao;
        this.encryptionService = encryptionService;
        this.disableSecureCookie = disableSecureCookie;
    }
    
    /* (non-Javadoc)
     * @see fi.om.initiative.service.UserService#login(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    // NOTE: NON-@Transactional method - not an interface method and higher level transaction is not needed
    public User login(String ssn, String firstNames, String lastName, 
            boolean finnishCitizen, LocalizedString homeMunicipality,
            HttpServletRequest request, HttpServletResponse response) {
        
        String ssnHash = encryptionService.registeredUserHash(ssn);
        
        User user = userDao.loginRegisteredUser(ssnHash);
        if (user != null) {
            user.assignSsn(ssn);
            user.assignFirstNames(firstNames);
            user.assignLastName(lastName);
            user.assignFinnishCitizen(finnishCitizen);
            user.assignHomeMunicipality(homeMunicipality);  
        } else {
            user = new User(ssn, new DateTime(), firstNames, lastName, finnishCitizen, homeMunicipality);
        }

        setCurrentUser(user, request);
        
        createCSRFToken(request, response);
        
        return user;
    }

    @Override
    public User getUserBySsn(String ssn) {
        String ssnHash = encryptionService.registeredUserHash(ssn);
        return userDao.getRegisteredUser(ssnHash);
    }

    @Override
    public void verifyCSRFToken(HttpServletRequest request) {
        if (csrfRequired(request)) {
            String sessionToken = getSessionCSRFToken(request);
            // Double Submit Cookie
            if ("POST".equalsIgnoreCase(request.getMethod())) {
                String requestToken = request.getParameter(CSRF_TOKEN_NAME);
                if (requestToken == null || !requestToken.equals(sessionToken)) {
                    throw new CSRFException("CSRFToken -request parameter missing or doesn't match session");
                }
            }
            
            request.setAttribute(CSRF_TOKEN_NAME, sessionToken);
        }
    }

    private String getSessionCSRFToken(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, CSRF_TOKEN_NAME);

        if (cookie == null) {
            throw new CSRFException("CSRF cookie missing");
        }

        String cookieToken = cookie.getValue();
        String sessionToken = (String) getExistingSession(request).getAttribute(CSRF_TOKEN_NAME);

        // Just to be sure no one has hijacked our session
        if (cookieToken == null || !cookieToken.equals(sessionToken)) {
            throw new CSRFException("CSRF session token missing or doesn't match cookie");
        }
        return sessionToken;
    }

    private boolean csrfRequired(HttpServletRequest request) {
        return getCurrentUser(false).isAuthenticated()
                || ("POST".equals(request.getMethod())
                && !((request.getRequestURI().equals(Urls.LOGIN_FI)) || request.getRequestURI().equals(Urls.LOGIN_SV)));
    }

    public String createCSRFToken(HttpServletResponse response, HttpSession existingSession) {
        String csrfToken = encryptionService.randomToken(CSRF_TOKEN_LENGTH);
        setCookie(CSRF_TOKEN_NAME, csrfToken, response);
        existingSession.setAttribute(CSRF_TOKEN_NAME, csrfToken);
        return csrfToken;
    }

    private String createCSRFToken(HttpServletRequest request, HttpServletResponse response) {
        HttpSession existingSession = getExistingSession(request);
        return createCSRFToken(response, existingSession);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = getOptionalSession(request);
        if (session != null) {
            session.invalidate();
        }
    }

    @Override
    public String getOrCreateCSRFToken(HttpSession session, HttpServletResponse response) {

        String csrfAttribute =  (String) session.getAttribute(CSRF_TOKEN_NAME);
        if (csrfAttribute != null) {
            return csrfAttribute;
        } else {
            return createCSRFToken(response, session);
        }
    }

    private void setCookie(String name, String value, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setSecure(!disableSecureCookie);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    @Override
    public HttpSession prepareForLogin(HttpServletRequest request) {
        // Create new session
        return request.getSession(true);
    }
    
    /* (non-Javadoc)
     * @see fi.om.initiative.service.UserService#getUser()
     */
    @Override
    public @NotNull User getCurrentUser() {
        return getCurrentUser(true);
    }
    
    @Override
    public @NotNull User getCurrentUser(boolean verifyCSRF) {
        User user = null;
        HttpServletRequest request = getRequest();
        HttpSession session = getOptionalSession(request);
        if (session != null) {
            user = (User) session.getAttribute(CURRENT_USER_SESSION_ATTR);
        }
        if (user != null) {
            if (verifyCSRF) {
                String csrfToken = (String) request.getAttribute(CSRF_TOKEN_NAME);
                if (Strings.isNullOrEmpty(csrfToken)) {
                    throw new CSRFException("CSRF - unverified request");
                }
            }
            return user;
        } else {
            return ANON;
        }
    }
    
    /* 
     * NOTE: Transaction propagation for this method is REQUIRES_NEW so that 
     * possible outer transaction cannot rollback implicit registration -
     * as of this point currentUser is actually registered.
     * 
     * @see fi.om.initiative.service.UserService#currentAsRegisteredUser()
     */
    @Override
    @Transactional(readOnly=false, propagation=Propagation.REQUIRES_NEW)
    public User currentAsRegisteredUser() {
        User user = getUserInRole(Role.AUTHENTICATED);
        if (!user.isRegistered()) {
            String ssnHash = encryptionService.registeredUserHash(user.getSsn());
            Long id = userDao.register(ssnHash, user.getLastLogin(), user.getFirstNames(), user.getLastName(), user.getDateOfBirth());
            
            user = user.withId(id);
            setCurrentUser(user, getRequest());
            return user;
        }
        return user;
    }
    
    @Override
    public User getUserInRole(Role... roles) {
        User user = getCurrentUser();
        if (user.isAuthenticated()) {
            for (Role role : roles) {
                if (user.hasRole(role)) {
                    return user;
                }
            }
            throw new AccessDeniedException();
        } else {
            throw new AuthenticationRequiredException();
        }
    }
    @Override
    public void requireUserInRole(Role... roles) {
        getUserInRole(roles);
    }
    
    private void setCurrentUser(User user, HttpServletRequest request) {
        getExistingSession(request).setAttribute(CURRENT_USER_SESSION_ATTR, user);
    }
    
    private @NotNull HttpSession getExistingSession(HttpServletRequest request) {
        HttpSession session = getOptionalSession(request);
        if (session == null) {
            throw new CookiesRequiredException();
        } else {
            return session;
        }
    }
    
    private @Nullable HttpSession getOptionalSession(HttpServletRequest request) {
        return getRequest().getSession(false);
    }
    
    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }
    
}
