package fi.om.initiative.dao;

import fi.om.initiative.dto.InitiativeSettings;
import fi.om.initiative.dto.Invitation;
import fi.om.initiative.dto.SchemaVersion;
import fi.om.initiative.dto.SendToParliamentData;
import fi.om.initiative.dto.author.Author;
import fi.om.initiative.dto.initiative.*;
import fi.om.initiative.dto.search.InitiativeSearch;
import fi.om.initiative.dto.search.InitiativeSublistWithTotalCount;
import fi.om.initiative.json.SupportCount;
import fi.om.initiative.util.OptionalHashMap;
import org.joda.time.LocalDate;

import java.util.List;

public interface InitiativeDao {

    InitiativePublic getInitiativeForPublic(Long id);

    InitiativePublicApi getInitiativeForPublicApi(Long id);

    InitiativeManagement getInitiativeForManagement(Long id, boolean forUpdate);

    Long create(InitiativeManagement initiative, Long userId);

    void updateInitiative(InitiativeManagement initiative, Long userId, boolean basic, boolean extra);

    void updateInitiativeState(Long initiativeId, Long userId, InitiativeState state, String comment);

    void updateInitiativeStateAndAcceptanceIdentifier(Long initiativeId, Long userId, InitiativeState state, String comment, String acceptanceIdentifier);

    InitiativeSublistWithTotalCount findInitiatives(InitiativeSearch search, Long userId, InitiativeSettings.MinSupportCountSettings minSupportCountSettings);

    void insertAuthor(Long initiativeId, Long userId, Author author);

    Author getAuthor(Long initiativeId, Long userId);

    void updateAuthor(Long initiativeId, Long userId, Author author);

    void updateInvitationSent(Long initiativeId, Long invitationId, String invitationCode);

    Invitation getOpenInvitation(Long initiativeId, String invitationCode, Integer invitationExpirationDays);

    void removeInvitation(Long initiativeId, String invitationCode);

    void updateLinks(Long initiativeId, List<Link> links);

    void updateInvitations(Long initiativeId, List<Invitation> initiatorInvitations, List<Invitation> representativeInvitations, List<Invitation> reserveInvitations);

    void removeInvitations(Long initiativeId);

    void clearConfirmations(Long initiativeId, Long userId);

    void confirmAuthor(Long initiativeId, Long userId);

    void deleteAuthor(Long initiativeId, Long userId);

    void updateConfirmationRequestSent(Long initiativeId, Long userId);

    List<String> getAuthorEmailsWhichAreNotNull(Long initiativeId);

    void removeUnconfirmedAuthors(Long initiativeId);

    void updateVRKResolution(Long initiativeId, int verifiedSupportCount, LocalDate verified, String verificationIdentifier, Long userId);

    List<InitiativeInfo> findInitiativesWithUnremovedVotes();

    long getInitiativeCount();

    List<SchemaVersion> getSchemaVersions();

    OptionalHashMap<InitiativeState, Long> getInitiativeCountByState();

    InitiativeManagement get(Long id);

    OptionalHashMap<String, Long> getOmCounts(InitiativeSettings.MinSupportCountSettings minSupportCountSettings);

    OptionalHashMap<String, Long> getPublicCounts(InitiativeSettings.MinSupportCountSettings minSupportCountSettings);

    SupportCount getSupportCount(Long id);

    void updateSendToParliament(Long initiativeId, SendToParliamentData data);

    void endInitiative(Long initiativeId, LocalDate endDate);

    List<InitiativeInfo> listAllInitiatives();


}
