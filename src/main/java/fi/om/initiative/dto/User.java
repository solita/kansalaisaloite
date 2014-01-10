package fi.om.initiative.dto;

import com.mysema.commons.lang.Assert;
import fi.om.initiative.dto.author.AuthorRole;
import fi.om.initiative.service.Role;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Years;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User implements Serializable {

    private static final long serialVersionUID = -3946537770706906538L;
    
    public static final String SSN_REGEX = "(\\d{2})(\\d{2})(\\d{2})([+-A])\\d{3}[0-9A-Z]";

    public static final Pattern SSN_PATTERN = Pattern.compile(SSN_REGEX);
    
    private static final int LEGAL_AGE = 18;

    private final Long id;
    
    /**
     * Keyed hash of ssn (henkilötunnus/hetu)
     */
    private String ssn;
    
    private String firstNames;

    private String lastName;

    private LocalizedString homeMunicipality;
    
    private boolean finnishCitizen; 
    
    private final boolean om;
    
    private final boolean vrk;
    
    private DateTime lastLogin;
    
    private LocalDate dateOfBirth;
    
    private User(Long id, String ssn, DateTime lastLogin, String firstNames, String lastName, LocalDate dateOfBirth, boolean finnishCitizen, LocalizedString homeMunicipality, boolean vrk, boolean om) {
        this.id = id;
        this.ssn = validateSSN(ssn);
        this.lastLogin = lastLogin;
        this.firstNames = firstNames;
        this.lastName = lastName;
        this.finnishCitizen = finnishCitizen;
        this.homeMunicipality = homeMunicipality;
        this.vrk = vrk;
        this.om = om;
        
        if (ssn != null) {
            this.dateOfBirth = parseDateOfBirth(ssn);
        } else {
            this.dateOfBirth = dateOfBirth;
        }
    }
    
    public static String validateSSN(String ssn) {
        if (ssn == null) {
            return null;
        } else {
            ssn = ssn.toUpperCase();
            if (SSN_PATTERN.matcher(ssn).matches()) {
                return ssn;
            } else {
                throw new IllegalArgumentException("Invalid SSN");
            }
        }
    }

    /**
     * Anonymous user
     */
    public User() {
        this(null, null, null, null, null, null, false, null, false, false);
    }
    
    /**
     * Registered user from DB.
     * 
     * @param id
     * @param lastLogin
     * @param vrk
     * @param om
     */
    public User(Long id, DateTime lastLogin, String firstNames, String lastName, LocalDate dateOfBirth, boolean vrk, boolean om) {
        this(id, null, lastLogin, firstNames, lastName, dateOfBirth, false, null, vrk, om);
    }
    
    /**
     * Authenticated, unregistered user: from login.
     * 
     * @param ssn
     * @param lastLogin
     * @param firstNames
     * @param lastName
     * @param homeMunicipality
     */
    public User(String ssn, DateTime lastLogin, String firstNames, String lastName, boolean finnishCitizen, LocalizedString homeMunicipality) {
        this(null, ssn, lastLogin, firstNames, lastName, null, finnishCitizen, homeMunicipality, false, false);
    }
    
    public boolean hasRole(Role role) {
        switch (role) {
        case AUTHENTICATED: return isAuthenticated();
        case REGISTERED: return isRegistered();
        case VRK: return isVrk();
        case OM: return isOm();
        default: return false;
        }
    }

    public Long getId() {
        return id;
    }

    public String getSsn() {
        return ssn;
    }

    public String getFirstNames() {
        return firstNames;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalizedString getHomeMunicipality() {
        return homeMunicipality;
    }
    
    public boolean isRegistered() {
        return id != null;
    }

    public User withId(Long id) {
        return new User(id, ssn, lastLogin, firstNames, lastName, dateOfBirth, finnishCitizen, homeMunicipality, vrk, om);
    }
    
    public DateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(DateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public boolean isVrk() {
        return vrk;
    }
    
    public boolean isAnonymous() {
        return !isAuthenticated();
    }
    
    public boolean isAuthenticated() {
        return ssn != null;
    }

    public boolean isOm() {
        return om;
    }
    
    public void assignFirstNames(String firstName) {
        this.firstNames = firstName;
    }

    public void assignLastName(String lastName) {
        this.lastName = lastName;
    }

    public void assignHomeMunicipality(LocalizedString homeMunicipality) {
        this.homeMunicipality = homeMunicipality;
    }
    
    public void assignSsn(String ssn) {
        this.ssn = validateSSN(ssn);
    }

    public boolean isFinnishCitizen() {
        return finnishCitizen;
    }

    public void assignFinnishCitizen(boolean finnishCitizen) {
        this.finnishCitizen = finnishCitizen;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public boolean isAdult() {
        return isAdult(new DateTime());
    }

    public boolean isAllowedToVote() {
        return isAllowedToVote(new DateTime());
    }
    public boolean isAllowedToVote(DateTime now) {
        return isFinnishCitizen() && isAdult(now);
    }
    
    public boolean isAdult(DateTime now) {
        return Years.yearsBetween(getDateOfBirth(), now.toLocalDate()).getYears() >= LEGAL_AGE;
    }
    
    public boolean isAllowRole(AuthorRole role, DateTime now) {
        if (!isAdult(now)) {
            return false;
        } else {
            switch (role) {
            case INITIATOR:
                return isFinnishCitizen(); 
            case REPRESENTATIVE:
                return true;
            case RESERVE:
                return true;
            default:
                throw new IllegalArgumentException("Unrecognized AuthorRole: " + role);
            }
        }
    }
    
    public boolean isAllowRoleInitiator() {
        return isAllowRole(AuthorRole.INITIATOR, new DateTime());
    }
    
    public boolean isAllowRoleRepresentative() {
        return isAllowRole(AuthorRole.REPRESENTATIVE, new DateTime());
    }
    
    public boolean isAllowRoleReserve() {
        return isAllowRole(AuthorRole.RESERVE, new DateTime());
    }
    
    
    private static LocalDate parseDateOfBirth(String ssn) {
        Assert.notNull(ssn, "ssn");

        Matcher m = SSN_PATTERN.matcher(ssn);
        if (m.matches()) {
            int dd = Integer.parseInt(m.group(1));
            int mm = Integer.parseInt(m.group(2));
            int yy = Integer.parseInt(m.group(3));
            int yyyy;
            char c = ssn.charAt(6); 
            switch (c) {
            case '+':
                yyyy = 1800 + yy;
                break;
            case '-':
                yyyy = 1900 + yy;
                break;
            case 'A':
                yyyy = 2000 + yy;
                break;
            default:
                throw new IllegalArgumentException("Invalid SSN");
            }
            return new LocalDate(yyyy, mm, dd);
        } else {
            throw new IllegalStateException("Invalid SSN");
        }
    }

}
