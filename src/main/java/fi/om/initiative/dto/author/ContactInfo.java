package fi.om.initiative.dto.author;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fi.om.initiative.json.EmailJsonSerializer;
import fi.om.initiative.validation.group.CurrentAuthor;

import javax.validation.constraints.Pattern;

public class ContactInfo {
    
    /**
     *  Switched using @Pattern instead of @Email, because hibernate's email validation was quite not good enough.
     *  Hibernate's Email validator passes emails like: "address@domain", "address@domain.com."
     *  
     *  Regexp is from: http://www.mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/
     *  - Added char '+' for the original regexp.
     *  
     *  NOTE: JavaScript uses different regexp ( aloitepalvelu.js:validateEmail() ). Should or could we use same pattern? 
     */
    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-\\+]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    @Pattern(regexp = EMAIL_PATTERN, groups=CurrentAuthor.class)
    private String email;
    
    private String phone;
    
    private String address;
    
    public ContactInfo() {}

    public ContactInfo(String email, String phone, String address) {
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    @JsonSerialize(using=EmailJsonSerializer.class)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
