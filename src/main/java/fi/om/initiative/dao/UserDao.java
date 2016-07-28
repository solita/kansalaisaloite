package fi.om.initiative.dao;

import fi.om.initiative.dto.User;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public interface UserDao {

    Long register(String ssnHash, DateTime lastLogin, String firstNames, String lastName, LocalDate dateOfBirth);

    User loginRegisteredUser(String ssnHash);

    User getRegisteredUser(String ssnHash);

    void setUserRoles(Long id, boolean vrk, boolean om);

}