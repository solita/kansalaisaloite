package fi.om.initiative.dto;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import fi.om.initiative.dto.author.AuthorRole;
import fi.om.initiative.dto.author.ContactInfo;
import fi.om.initiative.util.NotDeleted;
import fi.om.initiative.validation.group.Organizers;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import javax.validation.constraints.Pattern;

public class Invitation implements Deletable {

    public static final Predicate<Invitation> NOT_DELETED = NotDeleted.create();
    
    private Long id;

    private DateTime created;

    private DateTime sent;
    
    private String invitationCode;
    
    private AuthorRole role;

    @NotEmpty
    @Pattern(regexp = ContactInfo.EMAIL_PATTERN, groups=Organizers.class)
    private String email;
    
    
    public Invitation() {
        this(null);
    }
    
    public Invitation(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    
    public void assignId(Long id) {
        this.id = id;
    }

    public DateTime getCreated() {
        return created;
    }

    public void assignCreated(DateTime created) {
        this.created = created;
    }

    public DateTime getSent() {
        return sent;
    }

    public void assignSent(DateTime sent) {
        this.sent = sent;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void assignInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public AuthorRole getRole() {
        return role;
    }

    public void assignRole(AuthorRole role) {
        this.role = role;
    }

    public boolean isRole(AuthorRole role) {
        if ((role == AuthorRole.INITIATOR) && isInitiator()) {
            return true;
        } else  if ((role == AuthorRole.REPRESENTATIVE) && isRepresentative()) {
            return true;
        } else  if ((role == AuthorRole.RESERVE) && isReserve()) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean isInitiator() {
        return AuthorRole.INITIATOR.equals(role);
    }

    public boolean isRepresentative() {
        return AuthorRole.REPRESENTATIVE.equals(role);
    }

    public boolean isReserve() {
        return AuthorRole.RESERVE.equals(role);
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public boolean isDeleted() {
        return Strings.isNullOrEmpty(email);
    }
    
}
