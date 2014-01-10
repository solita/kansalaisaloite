package fi.om.initiative.dto.initiative;

import com.google.common.collect.Lists;
import fi.om.initiative.dto.EditMode;
import fi.om.initiative.dto.Invitation;
import fi.om.initiative.dto.ManagementSettings;
import fi.om.initiative.dto.User;
import fi.om.initiative.dto.author.Author;
import org.joda.time.DateTime;
import org.joda.time.ReadablePeriod;

import java.util.List;


public class ManagementSettingsBuilder {

    private final InitiativeManagement initiative;
    private EditMode editMode;
    private User currentUser;
    private int requiredVoteCount;
    private ReadablePeriod sendToVrkDuration;

    public enum ConfirmStatus { CONFIRMED, UNCONFIRMED }

    public static ManagementSettingsBuilder withInitiativeId(Long id) {return new ManagementSettingsBuilder(id); }

    public ManagementSettingsBuilder withIsAuthor(ConfirmStatus confirmStatus) {
        Author author = new Author();
        initiative.setCurrentAuthor(author);
        if (confirmStatus == ConfirmStatus.CONFIRMED) {
            author.assignConfirmed(anyDate());
        }
        return this;
    }

    private DateTime anyDate() {
        return new DateTime();
    }

    public ManagementSettingsBuilder withInitiativeState(InitiativeState initiativeState) {
        initiative.assignState(initiativeState);
        return this;
    }

    public ManagementSettingsBuilder withEditMode(EditMode editMode) {
        this.editMode = editMode;
        return this;
    }

    public ManagementSettingsBuilder withIsOmUser() {
        currentUser = new User(null, null, null, null, null, false, true);
        assert(currentUser.isOm());
        return this;
    }

    public ManagementSettingsBuilder withIsVRKUser() {
        currentUser = new User(null, null, null, null, null, true, false);
        assert(currentUser.isVrk());
        return this;
    }

    public ManagementSettingsBuilder withIsOmVRKUser() {
        currentUser = new User(null, null, null, null, null, true, true);
        assert(currentUser.isVrk());
        assert currentUser.isOm();
        return this;
    }

    public ManagementSettingsBuilder withIsDefaultUser() {
        currentUser = new User();
        return this;
    }


    public ManagementSettingsBuilder withUnsetInvitations(int unsetInvitations) {
        List<Invitation> invitations = Lists.newArrayList();
        for (int i = 0; i < unsetInvitations; ++i) {
            invitations.add(null);
        }
        initiative.setInitiatorInvitations(invitations);
        assert(initiative.getUnsentInvitations() == unsetInvitations);
        return this;
    }

    public ManagementSettingsBuilder withPendingConfirmationReminders(int reminders) {
        initiative.pendingConfirmationReminders = reminders;
        return this;
    }

    public ManagementSettingsBuilder withIsEnoughConfirmedAuthors(boolean isEnough) {
        initiative.enoughConfirmedAuthors = isEnough;
        return this;
    }

    public ManagementSettingsBuilder withSupportCount(int supportCount) {
        initiative.assignSupportCount(supportCount);
        return this;
    }

    public ManagementSettingsBuilder withSupportStatementsRemoved(DateTime removeTime) {
        initiative.assignSupportStatementsRemoved(removeTime);
        return this;
    }

    public ManagementSettingsBuilder withSentSupportCount(int sentSupportCount) {
        initiative.assignSentSupportCount(sentSupportCount);
        return this;
    }

    public ManagementSettingsBuilder withIsNotAuthor() {
        initiative.setCurrentAuthor(null);
        return this;
    }

    public ManagementSettingsBuilder withIsNotVrkUser() {
        currentUser = new User();
        return this;
    }

    public ManagementSettingsBuilder(Long initiativeId) {
        this.initiative = new InitiativeManagement(initiativeId);
    }

    public ManagementSettings toSettings() {
        return new ManagementSettings(initiative, editMode, currentUser, requiredVoteCount, sendToVrkDuration);
    }
}
