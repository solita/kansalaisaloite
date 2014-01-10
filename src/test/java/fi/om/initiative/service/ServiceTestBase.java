package fi.om.initiative.service;

import fi.om.initiative.dao.InitiativeDaoTest;
import fi.om.initiative.dto.InitiativeSettings;
import fi.om.initiative.dto.User;
import fi.om.initiative.dto.initiative.InitiativeManagement;
import fi.om.initiative.dto.initiative.InitiativePublic;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import static fi.om.initiative.util.Locales.asLocalizedString;

public class ServiceTestBase {
    
    final LocalDate DOB = new LocalDate().minusYears(20);
    
    final User REGISTERED_USER = new User(456l, new DateTime(), "Registered", "User", DOB, false, false);
    {
        REGISTERED_USER.assignFirstNames("Registered");
        REGISTERED_USER.assignLastName("User");
        REGISTERED_USER.assignSsn("010170-1234");
        REGISTERED_USER.assignFinnishCitizen(true);
        REGISTERED_USER.assignHomeMunicipality(asLocalizedString("Helsinki", "Helsingfors"));
    }
    
    final User RESERVE_USER = new User(457l, new DateTime(), "Reserve", "User", DOB, false, false);
    {
        RESERVE_USER.assignFirstNames("Reserve");
        RESERVE_USER.assignLastName("User");
        RESERVE_USER.assignSsn("010170-1236");
        RESERVE_USER.assignFinnishCitizen(true);
        RESERVE_USER.assignHomeMunicipality(asLocalizedString("Helsinki", "Helsingfors"));
    }
    
    final User OM_USER = new User(345l, new DateTime(), "OM", "User", DOB, false, true);
    {
        OM_USER.assignFirstNames("Om");
        OM_USER.assignLastName("User");
        OM_USER.assignSsn("010170-1235");
        OM_USER.assignFinnishCitizen(true);
        OM_USER.assignHomeMunicipality(asLocalizedString("Helsinki", "Helsingfors"));
    }
        
    final InitiativeManagement INITIATIVE_MANAGEMENT = InitiativeDaoTest.createNotEndedInitiative(123l);
    
    final InitiativePublic INITIATIVE_PUBLIC = new InitiativePublic(INITIATIVE_MANAGEMENT);

    final InitiativeSettings INITIATIVE_SETTINGS = new InitiativeSettings(1, 1, 2, Days.days(1), Days.days(3), Days.days(4), Days.days(5), Days.days(6), Days.days(7));
}
