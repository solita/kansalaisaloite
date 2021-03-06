package fi.om.initiative.service;

import fi.om.initiative.dto.Follower;
import fi.om.initiative.dto.Invitation;
import fi.om.initiative.dto.author.Author;
import fi.om.initiative.dto.initiative.InitiativeManagement;

import java.util.List;

public interface EmailService {

    void sendInvitation(InitiativeManagement initiative, Invitation invitation);

    void sendInvitationSummary(InitiativeManagement initiative);

    void sendNotificationToOM(InitiativeManagement initiative);

    void sendNotificationToVRK(InitiativeManagement initiative, int batchSize);

    void sendInvitationRejectedInfoToVEVs(InitiativeManagement initiative, String rejectedEmail, List<String> authorEmails);

    void sendConfirmationRequest(InitiativeManagement initiative, Author author);

    void sendStatusInfoToVEVs(InitiativeManagement initiative, EmailMessageType emailMessageType);

    void sendStatusInfoToFollowers(InitiativeManagement initiative, EmailMessageType emailMessageType, List<Follower> followers);

    void sendInvitationAcceptedInfoToVEVs(InitiativeManagement initiative, List<String> authorEmails);

    void sendAuthorConfirmedInfoToVEVs(InitiativeManagement initiative, List<String> authorEmails);

    void sendAuthorRemovedInfoToVEVs(InitiativeManagement initiative, Author removedAuthor, List<String> authorEmails);

    void sendFollowConfirmationEmail(InitiativeManagement initiative, String email, String unsubscribeHash);
}