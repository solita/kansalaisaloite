package fi.om.initiative.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.om.initiative.dto.LocalizedString;
import fi.om.initiative.dto.User;
import fi.om.initiative.service.UserService;

public interface HttpUserService extends UserService {

    User login(String ssn, String firstName, String lastName,
            boolean finnishCitizen, LocalizedString homeMunicipality,
            HttpServletRequest request, HttpServletResponse response);

    void verifyCSRFToken(HttpServletRequest request);

    void prepareForLogin(HttpServletRequest request);

    void logout(HttpServletRequest request, HttpServletResponse response);

}
