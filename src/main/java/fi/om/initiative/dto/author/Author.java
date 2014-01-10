package fi.om.initiative.dto.author;

import com.google.common.collect.Sets;
import com.mysema.commons.lang.Assert;
import fi.om.initiative.dto.LocalizedString;
import fi.om.initiative.dto.User;
import fi.om.initiative.validation.ValidAuthorRole;
import fi.om.initiative.validation.ValidContactInfo;
import fi.om.initiative.validation.ValidRoles;
import fi.om.initiative.validation.group.CurrentAuthor;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.util.Set;

@ValidContactInfo(groups=CurrentAuthor.class)
public class Author {

    private Long userId;

    private DateTime created;
    
    @NotEmpty(groups=CurrentAuthor.class)
    private String firstNames;
    
    @NotEmpty(groups=CurrentAuthor.class)
    private String lastName;
    
    private LocalDate dateOfBirth;

    @NotNull(groups=CurrentAuthor.class)
    private LocalizedString homeMunicipality;
    
    private DateTime confirmed;
    
    private DateTime confirmationRequestSent;

    @ValidAuthorRole(groups=CurrentAuthor.class, role=AuthorRole.INITIATOR)
    private boolean initiator;
    
    @ValidAuthorRole(groups=CurrentAuthor.class, role=AuthorRole.REPRESENTATIVE)
    private boolean representative;
    
    @ValidAuthorRole(groups=CurrentAuthor.class, role=AuthorRole.RESERVE)
    private boolean reserve;
    
    @Valid
    @NotNull(groups=CurrentAuthor.class)
    private ContactInfo contactInfo = new ContactInfo();
    
    public Author() {
        userId = null;
        dateOfBirth = null;
    }
    
    public Author(User user) {
        this(user.getId(), user.getFirstNames(), user.getLastName(), user.getDateOfBirth(), user.getHomeMunicipality());
    }
    
    public Author(Long userId, String firstNames, String lastName, @Nullable LocalDate dateOfBirth, LocalizedString homeMunicipality) {
        Assert.notNull(firstNames, "firstNames");
        Assert.notNull(lastName, "lastNames");
        Assert.notNull(homeMunicipality, "homeMunicipality");
        
        this.userId = userId;
        this.firstNames = firstNames;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.homeMunicipality = homeMunicipality;
    }

    public Long getUserId() {
        return userId;
    }

    public DateTime getCreated() {
        return created;
    }

    public void assignCreated(DateTime created) {
        this.created = created;
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

    public void assignLastName(String lastNames) {
        this.lastName = lastNames;
    }

    public LocalizedString getHomeMunicipality() {
        return homeMunicipality;
    }

    public void assignHomeMunicipality(LocalizedString homeMunicipality) {
        this.homeMunicipality = homeMunicipality;
    }

    public DateTime getConfirmed() {
        return confirmed;
    }
    
    public boolean isUnconfirmed() {
        return confirmed == null;
    }

    public void assignConfirmed(DateTime confirmed) {
        this.confirmed = confirmed;
    }

    public boolean isInitiator() {
        return initiator;
    }

    public void setInitiator(boolean initiator) {
        this.initiator = initiator;
    }

    public boolean isRepresentative() {
        return representative;
    }

    public void setRepresentative(boolean representative) {
        this.representative = representative;
    }

    public boolean isReserve() {
        return reserve;
    }

    public void setReserve(boolean reserve) {
        this.reserve = reserve;
    }
    
    @ValidRoles(groups=CurrentAuthor.class)
    public Set<AuthorRole> getRoles() {
        Set<AuthorRole> roles = Sets.newHashSet();
        if (isInitiator()) {
            roles.add(AuthorRole.INITIATOR);
        }
        if (isRepresentative()) {
            roles.add(AuthorRole.REPRESENTATIVE);
        }
        if (isReserve()) {
            roles.add(AuthorRole.RESERVE);
        }
        return roles;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    public void assignAddress(String address) {
        contactInfo.setAddress(address);
    }

    public void assignEmail(String email) {
        contactInfo.setEmail(email);
    }

    public void assignPhone(String phone) {
        contactInfo.setPhone(phone);
    }

    public DateTime getConfirmationRequestSent() {
        return confirmationRequestSent;
    }

    public void assignConfirmationRequestSent(DateTime confirmationRequestSent) {
        this.confirmationRequestSent = confirmationRequestSent;
    }
    
    public boolean isRequiresConfirmationReminder() {
        return confirmed == null && confirmationRequestSent == null && contactInfo.getEmail() != null;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void assignUserId(Long userId) {
        this.userId = userId;
    }

}
