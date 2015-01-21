package fi.om.initiative.service;

import fi.om.initiative.dto.EditMode;
import fi.om.initiative.dto.InitiativeCountByState;
import fi.om.initiative.dto.InitiativeCountByStateOm;
import fi.om.initiative.dto.Invitation;
import fi.om.initiative.dto.author.Author;
import fi.om.initiative.dto.initiative.InitiativeInfo;
import fi.om.initiative.dto.initiative.InitiativeManagement;
import fi.om.initiative.dto.initiative.InitiativePublic;
import fi.om.initiative.dto.search.InitiativeSearch;
import fi.om.initiative.dto.search.InitiativeSublistWithTotalCount;
import fi.om.initiative.json.SupportCount;
import org.joda.time.Period;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import java.util.List;

public interface InitiativeService {

    InitiativePublic getInitiativeForPublic(Long id);

    InitiativePublic getInitiativeForPublic(Long initiativeId, String hash);

    InitiativeManagement getInitiativeForManagement(Long id);

    Long create(InitiativeManagement initiative, Errors errors);

    InitiativeSublistWithTotalCount findInitiatives(InitiativeSearch search);

    List<InitiativeInfo> findInitiativesByAmount(InitiativeSearch search, Integer limit);

    InitiativeManagement update(InitiativeManagement initiative, EditMode editMode, Errors errors);

    Author getAuthor(Long initiativeId, Long userId);

    boolean sendInvitations(Long initiativeId);

    Invitation getInvitation(Long initiativeId, String invitationCode);

    boolean declineInvitation(Long initiativeId, String invitationCode);

    boolean acceptInvitation(Long initiativeId, String invitationCode, Author author, Errors errors);

    void sendToOM(Long initiativeId);

    void respondByOm(Long initiativeId, boolean accept, String comment, String acceptanceIdentifier);

    void confirmCurrentAuthor(Long initiativeId);

    void deleteCurrentAuthor(Long initiativeId);

    boolean updateVRKResolution(InitiativeManagement initiative, Errors bindingResult);

    List<InitiativeInfo> findInitiativesWithUnremovedVotes(Period afterEndDate);

    InitiativeCountByState getPublicInitiativeCountByState();

    InitiativeCountByStateOm getOmInitiativeCountByState();

    SupportCount getSupportCount(Long id);

    void updateSendToParliament(InitiativeManagement initiativeManagement, BindingResult errors);

    void endInitiative(Long initiativeId);
}