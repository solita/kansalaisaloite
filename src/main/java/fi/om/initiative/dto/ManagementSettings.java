package fi.om.initiative.dto;

import fi.om.initiative.dto.author.Author;
import fi.om.initiative.dto.initiative.InitiativeManagement;
import fi.om.initiative.dto.initiative.InitiativeState;
import org.joda.time.LocalDate;
import org.joda.time.ReadablePeriod;

import static fi.om.initiative.dto.EditMode.*;


public final class ManagementSettings {

    private final int requiredVoteCount;
    
    // Blocks: organizers, currentAuthor, extended, basic
    private final EditMode editMode;

    private final boolean allowEditBasic;

    private final boolean allowEditCurrentAuthor;

    private final boolean allowEditExtra;

    private final boolean allowEditOrganizers;
    
    private final boolean allowSendToOM;
    
    private final boolean allowRespondByOM;
    
    private final boolean allowSendToVRK;

    private final boolean allowRespondByVRK;
    
    private final boolean allowAcceptInvitation;
    
    private final boolean allowConfirmCurrentAuthor;
    
    private final boolean allowDeleteCurrentAuthor;
    
    private final boolean allowSendInvitations;

    private final boolean allowRemoveSupportVotes;
    
    private final boolean extraWarningforRemoveSupportVotes;

    private final boolean allowMarkAsSentToParliament;
    
    public ManagementSettings(InitiativeManagement initiative, EditMode editMode, User currentUser, int requiredVoteCount, ReadablePeriod sendToVrkDuration) {
        this.requiredVoteCount = requiredVoteCount;

        if (initiative.getId() == null) {
            // Create
            this.editMode = FULL;
            allowEditBasic = allowEditCurrentAuthor = allowEditExtra = allowEditOrganizers = true;
            allowSendInvitations = false;
            allowSendToOM = allowRespondByOM = false;
            allowSendToVRK = allowRespondByVRK = false;
            allowConfirmCurrentAuthor = allowDeleteCurrentAuthor = false;
            allowRemoveSupportVotes = false;
            extraWarningforRemoveSupportVotes = false;
            allowMarkAsSentToParliament = false;
        } else {
            // Update
            Author currentAuthor = initiative.getCurrentAuthor();
            
            allowRemoveSupportVotes = makeAllowRemoveSupportVotes(initiative, currentUser, currentAuthor, requiredVoteCount);
            extraWarningforRemoveSupportVotes = allowRemoveSupportVotes && isWaitingForVrkResponse(initiative);
            
            allowConfirmCurrentAuthor = 

                    isCurrentAuthor(currentAuthor)
                    && currentAuthor.isUnconfirmed()
                    && initiative.getState() == InitiativeState.PROPOSAL;

            allowDeleteCurrentAuthor = 
                    isCurrentAuthor(currentAuthor)
                    && initiative.getState() != InitiativeState.DONE
                    && initiative.getState() != InitiativeState.CANCELED;

            allowMarkAsSentToParliament = currentUser.isOm()
                    && initiative.getVerifiedSupportCount() >= requiredVoteCount
                    && initiative.getState() == InitiativeState.ACCEPTED;
            
            if (isCurrentAuthor(currentAuthor) && !currentAuthor.isUnconfirmed()) {
                this.editMode = editMode != null ? editMode : NONE;
                allowRespondByOM = false; // never allowed for authors
                allowRespondByVRK = false; // never allowed for authors
                
                // Authors rights
                switch (initiative.getState()) {
                case DRAFT:
                case PROPOSAL:
                    allowEditBasic = allowEditCurrentAuthor = allowEditExtra = allowEditOrganizers = true;
                    allowSendInvitations = initiative.getUnsentInvitations() > 0 || initiative.getPendingConfirmationReminders() > 0;
                    allowSendToOM = initiative.isEnoughConfirmedAuthors();
                    allowSendToVRK = false;
                    break;
                case REVIEW:
                    allowEditBasic = allowEditOrganizers = false;
                    allowEditCurrentAuthor = allowEditExtra = true;
                    allowSendInvitations = false;
                    allowSendToOM = false;
                    allowSendToVRK = false;
                    break;
                case ACCEPTED:
                    allowEditBasic = allowEditOrganizers = false;
                    allowEditCurrentAuthor = allowEditExtra = true;
                    allowSendInvitations = false;
                    allowSendToOM = false;
                    allowSendToVRK = 
                            isRepresentativeOrReserve(currentAuthor) // NOTE: #61: Initiator should not be able to send to VRK. 
                            && initiative.getTotalSupportCount() >= requiredVoteCount
                            && initiative.getSupportCount() > initiative.getSentSupportCount()
                            && initiative.getSupportStatementsRemoved() == null
                            && !initiative.isSendToVrkEnded(sendToVrkDuration, LocalDate.now());
                    break;
                case DONE:
                case CANCELED:
                default:
                    allowEditBasic = allowEditCurrentAuthor = allowEditExtra = allowEditOrganizers = false;
                    allowSendInvitations = false;
                    allowSendToOM = false;
                    allowSendToVRK = false;
                }
            }
            // Unconfirmed authors, OM and VRK 
            else {
                // OM or VRK (if OM/VRK is also author, then he/she is not allowed to be in OM/VRK role)
                this.editMode = NONE;
                allowEditBasic = allowEditCurrentAuthor = allowEditExtra = allowEditOrganizers = false;
                allowSendInvitations = false;
                allowSendToOM = false;
                allowSendToVRK = false;
                
                if (isNotCurrentAuthor(currentAuthor) && currentUser.isOm() && initiative.getState() == InitiativeState.REVIEW) {
                    allowRespondByOM = true;
                } else {
                    allowRespondByOM = false;
                }

                if (isNotCurrentAuthor(currentAuthor) && currentUser.isVrk()
                    && initiative.getState() == InitiativeState.ACCEPTED
                    && initiative.getSentSupportCount() > 0) {
                    
                    allowRespondByVRK = true;
                } else {
                    allowRespondByVRK = false;
                }
                
            }

        }

        allowAcceptInvitation = initiative.getState() == InitiativeState.PROPOSAL;

    }

    private boolean isNotCurrentAuthor(Author currentAuthor) {
        return currentAuthor == null;
    }

    private boolean isCurrentAuthor(Author currentAuthor) {
        return currentAuthor != null;
    }

    private boolean makeAllowRemoveSupportVotes(InitiativeManagement initiative, User currentUser, Author currentAuthor, int requiredVoteCount) {
        //1. check user rights for this initiative
        if (!currentUser.isOm() && !isConfirmedAuthor(currentAuthor)) {
            return false;
        }

        // Allow removal only if initiative has not enough supports or vrk has verified the supports.
        // -> Disallow removal if we've reached 50000 until vrk has verified them.
        if (initiative.isVotingEnded(LocalDate.now())
                && initiative.getSupportStatementsRemoved() == null
                && (initiative.getVerifiedSupportCount() > 0 || initiative.getTotalSupportCount() < requiredVoteCount)) {
            return true;
        }

        return false;
    }
    
    private boolean isWaitingForVrkResponse(InitiativeManagement initiative) {
        return (initiative.getSentSupportCount() > 0 && initiative.getVerified() == null);
    }
    
    private boolean isRepresentativeOrReserve(Author author) {
        return isConfirmedAuthor(author) && (author.isRepresentative() || author.isReserve());
    }
    
    private boolean isConfirmedAuthor(Author author) {
        return isCurrentAuthor(author) && !author.isUnconfirmed();
    }
    
    public boolean isEditBasic() {
        return editMode == FULL || editMode == BASIC;
    }

    public boolean isEditCurrentAuthor() {
        return editMode == FULL || editMode == CURRENT_AUTHOR;
    }

    public boolean isEditExtra() {
        return editMode == FULL || editMode == EXTRA;
    }

    public boolean isEditOrganizers() {
        return editMode == FULL || editMode == ORGANIZERS;
    }
    
    public boolean isEditFull() {
        return editMode == FULL;
    }

    public boolean isAllowEditBasic() {
        return allowEditBasic;
    }

    public boolean isAllowEditCurrentAuthor() {
        return allowEditCurrentAuthor;
    }

    public boolean isAllowEditExtra() {
        return allowEditExtra;
    }

    public boolean isAllowEditOrganizers() {
        return allowEditOrganizers;
    }

    public EditMode getEditMode() {
        return editMode;
    }

    public boolean isAllowSendToOM() {
        return allowSendToOM;
    }

    public boolean isAllowRespondByOM() {
        return allowRespondByOM;
    }
    
    public boolean isAllowSendToVRK() {
        return allowSendToVRK;
    }

    public boolean isAllowRespondByVRK() {
        return allowRespondByVRK;
    }
    
    public boolean isAllowConfirmCurrentAuthor() {
        return allowConfirmCurrentAuthor;
    }

    public boolean isAllowDeleteCurrentAuthor() {
        return allowDeleteCurrentAuthor;
    }

    public boolean isAllowSendInvitations() {
        return allowSendInvitations;
    }
    
    public boolean isAllowRemoveSupportVotes() {
        return allowRemoveSupportVotes;
    }
    
    public boolean isExtraWarningforRemoveSupportVotes() {
        return extraWarningforRemoveSupportVotes;
    }

    public int getRequiredVoteCount() {
        return requiredVoteCount;
    }

    public boolean isAllowAcceptInvitation() {
        return allowAcceptInvitation;
    }

    public boolean isAllowMarkAsSentToParliament() {
        return allowMarkAsSentToParliament;
    }
}
