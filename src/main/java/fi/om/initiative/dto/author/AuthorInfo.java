package fi.om.initiative.dto.author;

import fi.om.initiative.dto.LocalizedString;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class AuthorInfo {
    
    @NotEmpty
    private String firstNames;
    
    @NotEmpty
    private String lastName;

    @NotNull
    private LocalizedString homeMunicipality;
    
    public AuthorInfo(Author author) {
        firstNames = author.getFirstNames();
        lastName = author.getLastName();
        homeMunicipality = author.getHomeMunicipality();
    }

    public String getFirstNames() {
        return firstNames;
    }

    public void assignFirstNames(String firstNames) {
        this.firstNames = firstNames;
    }

    public String getLastName() {
        return lastName;
    }

    public void assignLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalizedString getHomeMunicipality() {
        return homeMunicipality;
    }

    public void assignHomeMunicipality(LocalizedString homeMunicipality) {
        this.homeMunicipality = homeMunicipality;
    }

}
