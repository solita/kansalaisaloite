package fi.om.initiative.dto;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import org.joda.time.LocalDate;
import org.junit.Test;


public class UserTest {

    @Test
    public void SSN_Validation_OK() {
        assertNull(User.validateSSN(null));
        
        assertSSNValidation("120464-126J");
        assertSSNValidation("280264-051U");
        assertSSNValidation("140457-107D");

        assertSSNValidation("012345+1234");
        assertSSNValidation("111111-123A");
        assertSSNValidation("111111A124V");
        assertSSNValidation("111111A1234");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void SSN_Validation_Empty() {
        User.validateSSN("");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void SSN_Validation_Lacking_Date_Of_Birth() {
        User.validateSSN("12345-1234");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void SSN_Validation_Invalid_Century() {
        User.validateSSN("123456X1234");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void SSN_Validation_Invalid_Date_Of_Birth() {
        User.validateSSN("12345C-0123");
    }
    
    private void assertSSNValidation(String ssn) {
        assertEquals(ssn.toUpperCase(), User.validateSSN(ssn));
    }
    
    @Test
    public void Date_Of_Birth_OK() {
        assertEquals(new LocalDate(1804, 11, 17), newUser("171104+1234").getDateOfBirth());
        assertEquals(new LocalDate(1904, 11, 17), newUser("171104-1234").getDateOfBirth());
        assertEquals(new LocalDate(2004, 11, 17), newUser("171104A1234").getDateOfBirth());

        assertEquals(new LocalDate(2000, 1, 1), newUser("010100A1234").getDateOfBirth());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void Date_Of_Birth_Invalid_Leap_Day() {
        newUser("290201A123A").getDateOfBirth(); // invalid leap day
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void Date_Of_Birth_Invalid_Date() {
        newUser("000201A123A").getDateOfBirth();
    }

    private User newUser(String ssn) {
        return new User(ssn, null, "firstName", "lastName", true, null);
    }
}
