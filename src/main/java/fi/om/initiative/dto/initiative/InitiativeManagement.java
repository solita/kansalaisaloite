package fi.om.initiative.dto.initiative;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import fi.om.initiative.dto.Invitation;
import fi.om.initiative.dto.LanguageCode;
import fi.om.initiative.dto.author.Author;
import fi.om.initiative.dto.author.AuthorRole;
import fi.om.initiative.validation.group.CurrentAuthor;
import fi.om.initiative.validation.group.VRK;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.util.List;

public class InitiativeManagement extends InitiativeBase {

    private Long modifierId;
    
    @Valid
    @NotNull(groups=CurrentAuthor.class)
    private Author currentAuthor;

    private List<Author> authors = Lists.newArrayList();
    
    private List<Author> initiators = Lists.newArrayList();
    
    private List<Author> representatives = Lists.newArrayList();
    
    private List<Author> reserves = Lists.newArrayList();

    @Valid
    private List<Invitation> initiatorInvitations = Lists.newArrayList();

    @Valid
    private List<Invitation> representativeInvitations = Lists.newArrayList();

    @Valid
    private List<Invitation> reserveInvitations = Lists.newArrayList();

    private List<Invitation> initiatorSentInvitations = Lists.newArrayList();

    private List<Invitation> representativeSentInvitations = Lists.newArrayList();

    private List<Invitation> reserveSentInvitations = Lists.newArrayList();
    
    boolean enoughConfirmedAuthors; // Package private for tests

    private int unconfirmedAuthors;
    
    /**
     * Number of unsent (re)confirmation requests for authors with email address. 
     */
    int pendingConfirmationReminders; // Package-private for tests

    // When we know OM group: @Size(max=InitiativeConstants.STATE_COMMENT_MAX)
    private String stateComment;

    @NotEmpty(groups=VRK.class)
    private String verificationIdentifier;

    public InitiativeManagement() {
        // For Spring binding
    }
    
    public InitiativeManagement(Author currentAuthor, LanguageCode primaryLanguage) {
        this.currentAuthor = currentAuthor;
        setPrimaryLanguage(primaryLanguage);
    }
    
    public InitiativeManagement(Long id) {
        super(id);
    }

    public Long getModifierId() {
        return modifierId;
    }

    public void assignModifierId(Long modifierId) {
        this.modifierId = modifierId;
    }

    public List<Author> getInitiators() {
        return initiators;
    }
    
    @Override
    public void assignAuthors(List<Author> authors) {
        this.authors = authors;
        boolean hasInitiator = false;
        boolean hasRepresentative = false;
        boolean hasReserve = false;
        for (Author author : authors) {
            if (author.isUnconfirmed()) {
                unconfirmedAuthors++;
                if (author.isRequiresConfirmationReminder()) {
                    pendingConfirmationReminders++;
                }
            }
            
            if (author.isInitiator()) {
                this.initiators.add(author);
                if (!author.isUnconfirmed()) {
                    hasInitiator = true;
                }
            }
            if (author.isRepresentative()) {
                this.representatives.add(author);
                if (!author.isUnconfirmed()) {
                    hasRepresentative = true;
                }
            } else if (author.isReserve()) {
                this.reserves.add(author);
                if (!author.isUnconfirmed()) {
                    hasReserve = true;
                }
            }
        }
        this.enoughConfirmedAuthors = hasInitiator && hasRepresentative && hasReserve;
    }

    public List<Author> getRepresentatives() {
        return representatives;
    }

    public List<Author> getReserves() {
        return reserves;
    }
 
    public Author getCurrentAuthor() {
        return currentAuthor;
    }

    public void setCurrentAuthor(Author currentAuthor) {
        this.currentAuthor = currentAuthor;
    }

    public List<Invitation> getInitiatorInvitations() {
        return initiatorInvitations;
    }

    public void setInitiatorInvitations(List<Invitation> initiatorInvitations) {
        this.initiatorInvitations = initiatorInvitations;
    }

    public List<Invitation> getRepresentativeInvitations() {
        return representativeInvitations;
    }

    public void setRepresentativeInvitations(
            List<Invitation> representativeInvitations) {
        this.representativeInvitations = representativeInvitations;
    }

    public List<Invitation> getReserveInvitations() {
        return reserveInvitations;
    }

    public void setReserveInvitations(List<Invitation> reserveInvitations) {
        this.reserveInvitations = reserveInvitations;
    }

    public List<Invitation> getInitiatorSentInvitations() {
        return initiatorSentInvitations;
    }

    public List<Invitation> getRepresentativeSentInvitations() {
        return representativeSentInvitations;
    }

    public List<Invitation> getReserveSentInvitations() {
        return reserveSentInvitations;
    }

    public void assignInvitations(List<Invitation> invitations) {
        for (Invitation invitation : invitations) {
            if (invitation.getSent() == null) {
                if (AuthorRole.INITIATOR.equals(invitation.getRole())) {
                    initiatorInvitations.add(invitation);
                } else if (AuthorRole.REPRESENTATIVE.equals(invitation.getRole())) {
                    representativeInvitations.add(invitation);
                } else if (AuthorRole.RESERVE.equals(invitation.getRole())) {
                    reserveInvitations.add(invitation);
                }
            } else {
                if (AuthorRole.INITIATOR.equals(invitation.getRole())) {
                    initiatorSentInvitations.add(invitation);
                } else if (AuthorRole.REPRESENTATIVE.equals(invitation.getRole())) {
                    representativeSentInvitations.add(invitation);
                } else if (AuthorRole.RESERVE.equals(invitation.getRole())) {
                    reserveSentInvitations.add(invitation);
                }
            }
        }
    }

    public List<Invitation> getInvitations() {
        return Lists.newArrayList(Iterables.concat(initiatorInvitations, representativeInvitations, reserveInvitations));
    }

    public List<Invitation> getSentInvitations() {
        return Lists.newArrayList(Iterables.concat(initiatorSentInvitations, representativeSentInvitations, reserveSentInvitations));
    }
    
    public List<Author> getAuthors() {
        return authors;
    }
    
    public void cleanupAfterBinding() {
        super.cleanupAfterBinding();
        
        initiatorInvitations = Lists.newArrayList(Iterables.filter(initiatorInvitations, Invitation.NOT_DELETED));
        representativeInvitations = Lists.newArrayList(Iterables.filter(representativeInvitations, Invitation.NOT_DELETED));
        reserveInvitations = Lists.newArrayList(Iterables.filter(reserveInvitations, Invitation.NOT_DELETED));
    }

    public boolean isEnoughConfirmedAuthors() {
        return enoughConfirmedAuthors;
    }

    public int getUnconfirmedAuthors() {
        return unconfirmedAuthors;
    }
    
    public int getUnsentInvitations() {
        return initiatorInvitations.size() + representativeInvitations.size() + reserveInvitations.size();
    }
    
    public int getTotalUnconfirmedCount() {
        return getUnsentInvitations() + getSentInvitations().size() + getUnconfirmedAuthors();
    }

    public int getPendingConfirmationReminders() {
        return pendingConfirmationReminders;
    }

    public String getStateComment() {
        return stateComment;
    }

    public void assignStateComment(String stateComment) {
        this.stateComment = stateComment;
    }

    public String getVerificationIdentifier() {
        return verificationIdentifier;
    }

    public void setVerificationIdentifier(String verificationIdentifier) {
        this.verificationIdentifier = verificationIdentifier;
    }
}
