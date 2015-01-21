package fi.om.initiative.service;

import com.google.common.collect.Sets;
import com.mysema.commons.lang.Assert;
import fi.om.initiative.dao.InitiativeDao;
import fi.om.initiative.dao.NotFoundException;
import fi.om.initiative.dto.*;
import fi.om.initiative.dto.author.Author;
import fi.om.initiative.dto.initiative.InitiativeInfo;
import fi.om.initiative.dto.initiative.InitiativeManagement;
import fi.om.initiative.dto.initiative.InitiativePublic;
import fi.om.initiative.dto.initiative.InitiativeState;
import fi.om.initiative.dto.search.InitiativeSearch;
import fi.om.initiative.dto.search.InitiativeSublistWithTotalCount;
import fi.om.initiative.dto.search.SearchView;
import fi.om.initiative.dto.search.Show;
import fi.om.initiative.json.SupportCount;
import fi.om.initiative.util.OptionalHashMap;
import fi.om.initiative.validation.AuthorRoleValidator;
import fi.om.initiative.validation.LocalizationRequiredValidator;
import fi.om.initiative.validation.group.OM;
import fi.om.initiative.validation.group.VRK;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;

import javax.annotation.Nullable;
import javax.annotation.Resource;

import java.util.List;
import java.util.Set;

import static fi.om.initiative.dto.EditMode.CURRENT_AUTHOR;
import static fi.om.initiative.dto.EditMode.FULL;
import static fi.om.initiative.util.Locales.FI;
import static fi.om.initiative.util.Locales.SV;

public class InitiativeServiceImpl implements InitiativeService {

    private final Logger log = LoggerFactory.getLogger(InitiativeServiceImpl.class); 
    
    @Resource InitiativeDao initiativeDao;
    
    @Resource UserService userService;

    @Resource EmailService emailService;
    
    @Resource SmartValidator validator;
    
    @Resource EncryptionService encryptionService;

    @Resource InitiativeSettings initiativeSettings;

    @Resource HashCreator hashCreator;

    private final int invitationCodeLength = 12; // Multiples of 3 work best
    
    public InitiativeServiceImpl() {
    }
    
    public InitiativeServiceImpl(InitiativeDao initiativeDao, UserService userService, 
                                 EmailService emailService, EncryptionService encryptionService, 
                                 SmartValidator validator,
                                 InitiativeSettings initiativeSettings,
                                 HashCreator hashCreator) {
        this.initiativeDao = initiativeDao;
        this.userService = userService;
        this.emailService = emailService;
        this.encryptionService = encryptionService;
        this.validator = validator;
        this.initiativeSettings = initiativeSettings;
        this.hashCreator = hashCreator;
    }
    
    @Override
    @Transactional(readOnly=true)
    public InitiativePublic getInitiativeForPublic(Long initiativeId) {
        return getInitiativeForPublic(initiativeId, null);
    }

    @Override
    @Transactional(readOnly = true)
    public InitiativePublic getInitiativeForPublic(Long initiativeId, String hash) {
        InitiativePublic initiative = initiativeDao.getInitiativeForPublic(initiativeId);
        if (initiative == null) {
            throw new NotFoundException("initiative", initiativeId);
        } else {
            // If given hash is correct, always return initiative
            if (hashCreator.isHash(initiativeId, hash)) {
                return initiative;
            }

            if (initiative.getState().isNotPublicState()) {
                User user = userService.getUserInRole(Role.REGISTERED);
                // Allow preview for authors even if it's not implemented yet
                if (getAuthor(initiativeId, user.getId()) == null) {
                    throw new AccessDeniedException("No public view for a draft.");
                }
            }

            return initiative;
        }

    }

    @Override
    @Transactional(readOnly=true)
    public InitiativeManagement getInitiativeForManagement(Long initiativeId) {
        return getInitiativeForManagement(initiativeId, false);
    }

    private InitiativeManagement getInitiativeForManagement(Long initiativeId, boolean forUpdate) {
        // NOTE: User must be REGISTERED before allowed to manage existing initiative
        User user = userService.getUserInRole(Role.REGISTERED);
        
        InitiativeManagement initiative = initiativeDao.getInitiativeForManagement(initiativeId, forUpdate);
        if (initiative == null) {
            throw new NotFoundException("initiative", initiativeId);
        } else {
            Author author = requireAuthorOrOfficial(initiativeId, user);
            initiative.setCurrentAuthor(author);
            return initiative;
        }
    }
    
    private Author requireAuthorOrOfficial(Long initiativeId, User user) {
        Author author = getAuthor(initiativeId, user.getId());

        if (author != null) {
            return author;
        } else if (user.isOm() || user.isVrk()) {
            return null;
        } else {
            throw new AccessDeniedException("Not an author or om or vrk official.");
        }
    }

    @Override
    @Transactional(readOnly=false)
    public InitiativeManagement update(InitiativeManagement initiative, EditMode editMode, Errors errors) {
        final String METHOD_NAME = "update";
        // NOTE: User MUST be registered before allowed to update initiative
        User user = userService.getUserInRole(Role.REGISTERED);

        Assert.notNull(initiative, "initiative");
        Assert.notNull(initiative.getId(), "initiative.id");
        Assert.notNull(editMode, "editMode");
        Assert.notNull(errors, "errors");
        
        if (editMode == EditMode.NONE) {
            return initiative;
        }

        InitiativeManagement persisted = getInitiativeForManagement(initiative.getId(), true);
        ManagementSettings managementSettings = initiativeSettings.getManagementSettings(persisted, editMode, user);
        
        initiative = mergeEdits(initiative, persisted, editMode, managementSettings);
        
        Author editAuthor = initiative.getCurrentAuthor();
        if (editAuthor != null) {
            assignAuthorAutoFields(editAuthor, user);
        }
        
        if (validate(initiative, user, errors, editMode.getValidationGroups())) {
            boolean updateBasic = false;
            boolean updateExtra = false;
            
            switch (editMode) {
            case FULL:
            case BASIC:
                require(managementSettings.isAllowEditBasic());
                updateBasic = true;
                initiativeDao.clearConfirmations(initiative.getId(), user.getId());
                if (editMode != FULL) break;
            case EXTRA:
                require(managementSettings.isAllowEditExtra());
                updateExtra = true;
                initiativeDao.updateLinks(initiative.getId(), initiative.getLinks());
                if (editMode != FULL) break;
            case ORGANIZERS:
                require(managementSettings.isAllowEditOrganizers());
                initiativeDao.updateInvitations(initiative.getId(), 
                        initiative.getInitiatorInvitations(), 
                        initiative.getRepresentativeInvitations(), 
                        initiative.getReserveInvitations());
                if (editMode != FULL) break;
            case CURRENT_AUTHOR:
                require(managementSettings.isAllowEditCurrentAuthor());
                initiativeDao.updateAuthor(initiative.getId(), user.getId(), editAuthor);
                break;
            default: 
                throw new IllegalArgumentException("Unsupported EditMode: " + editMode);
            }
            // At the minimum this sets the modifierId and modified timestamp (by trigger)
            initiativeDao.updateInitiative(initiative, user.getId(), updateBasic, updateExtra);
            
            log(METHOD_NAME, initiative.getId(), user, true);
            return initiative;
        } else {
            log(METHOD_NAME, initiative.getId(), user, false);
            return initiative;
        }
    }
    
    private void require(boolean right) {
        if (!right) {
            throw new AccessDeniedException();
        }
    }
    
    private boolean validate(Author author, User currentUser, Errors errors) {
        AuthorRoleValidator.setCurrentUser(currentUser);
        try {
            validator.validate(author, errors, (Object[]) CURRENT_AUTHOR.getValidationGroups());
        } finally {
            AuthorRoleValidator.clearCurrentUser(); // Defensive
        }
        return !errors.hasErrors();
    }
    
    private boolean validate(InitiativeManagement initiative, User currentUser, Errors errors, Class<?>... groupClasses) {
        initiative.cleanupAfterBinding(); // if validation fails, empty links will mess the form

        LocalizationRequiredValidator.setRequiredLocales(getRequiredLocales(initiative));
        AuthorRoleValidator.setCurrentUser(currentUser);
        try {
            validator.validate(initiative, errors, (Object[]) groupClasses);
        } finally {
            LocalizationRequiredValidator.clearRequiredLocales(); // Defensive
            AuthorRoleValidator.clearCurrentUser(); // Defensive
        }
        return !errors.hasErrors();
    }
    
    private Set<String> getRequiredLocales(InitiativeManagement initiative) {
        Set<String> requiredLocales = Sets.newHashSet();
        
        if (initiative.hasTranslation(FI)) {
            requiredLocales.add(FI);
        }
        
        if (initiative.hasTranslation(SV)) {
            requiredLocales.add(SV);
        }
        
        if (requiredLocales.isEmpty()) {
            requiredLocales.add(LocaleContextHolder.getLocale().getLanguage());
        }

        return requiredLocales;
    }

    @Override
    @Transactional(readOnly=false)
    public Long create(InitiativeManagement initiative, Errors errors) {
        final String METHOD_NAME = "create";
        // Requires authenticated user and registers user if he/she is not yet registered
        User user = userService.getUserInRole(Role.AUTHENTICATED);

        Assert.notNull(initiative, "initiative");
        Assert.notNull(errors, "errors");
        Author editAuthor = initiative.getCurrentAuthor();
        Assert.notNull(editAuthor, "author");

        assignAuthorAutoFields(editAuthor, user);
        
        if (validate(initiative, user, errors, FULL.getValidationGroups())) {
            user = userService.currentAsRegisteredUser(); 
            
            initiative.assignEndDate(initiative.getStartDate(), initiativeSettings.getVotingDuration());
            Long id = initiativeDao.create(initiative, user.getId());

            initiativeDao.updateLinks(id, initiative.getLinks());
            
            initiativeDao.updateInvitations(id, initiative.getInitiatorInvitations(), 
                    initiative.getRepresentativeInvitations(), 
                    initiative.getReserveInvitations());
            
            initiativeDao.insertAuthor(id, user.getId(), editAuthor);
            
            log(METHOD_NAME, id, user, true);
            return id;
        } else {
            log(METHOD_NAME, null, user, false);
            return null;
        }
    }
    
    private void assignAuthorAutoFields(Author editAuthor, User user) {
        editAuthor.assignFirstNames(user.getFirstNames());
        editAuthor.assignLastName(user.getLastName());
        editAuthor.assignHomeMunicipality(user.getHomeMunicipality());
    }
    
    private InitiativeManagement mergeEdits(InitiativeManagement edited, InitiativeManagement persisted, EditMode editMode, ManagementSettings managementSettings) {
        switch (editMode) {
        case FULL:
        case BASIC:
            persisted.setName(edited.getName());
            persisted.setStartDate(edited.getStartDate());
            persisted.assignEndDate(edited.getStartDate(), initiativeSettings.getVotingDuration());
            persisted.setProposalType(edited.getProposalType());
            persisted.setProposal(edited.getProposal());
            persisted.setRationale(edited.getRationale());
            persisted.setPrimaryLanguage(edited.getPrimaryLanguage());
            if (editMode != FULL) break;
        case EXTRA:
            // Keywords
            persisted.setFinancialSupport(edited.isFinancialSupport());
            persisted.setFinancialSupportURL(edited.getFinancialSupportURL());
            persisted.setSupportStatementsInWeb(edited.isSupportStatementsInWeb());
            persisted.setSupportStatementsOnPaper(edited.isSupportStatementsOnPaper());
            persisted.setExternalSupportCount(edited.getExternalSupportCount());
            persisted.setLinks(edited.getLinks());
            persisted.setSupportStatementPdf(edited.isSupportStatementPdf());
            persisted.setSupportStatementAddress(edited.getSupportStatementAddress());
            if (editMode != FULL) break;
        case ORGANIZERS:
            persisted.setInitiatorInvitations(edited.getInitiatorInvitations());
            persisted.setRepresentativeInvitations(edited.getRepresentativeInvitations());
            persisted.setReserveInvitations(edited.getReserveInvitations());
            if (editMode != FULL) break;
        case CURRENT_AUTHOR:
            Author editedAuthor = edited.getCurrentAuthor();
            Author persistedAuthor = persisted.getCurrentAuthor();
            if (editedAuthor != null && persistedAuthor != null) {
                persistedAuthor.setContactInfo(editedAuthor.getContactInfo());
                if (managementSettings.isAllowEditOrganizers()) {
                    persistedAuthor.setInitiator(editedAuthor.isInitiator());
                    persistedAuthor.setRepresentative(editedAuthor.isRepresentative());
                    persistedAuthor.setReserve(editedAuthor.isReserve());
                }
            }
            break;
        }
        
        return persisted;
    }

    @Override
    @Transactional(readOnly=true)
    public InitiativeSublistWithTotalCount findInitiatives(InitiativeSearch search) {
        final Long userId;
        if (search.getSearchView() == SearchView.own) {
            userId = userService.getUserInRole(Role.REGISTERED).getId();
            search.setMinSupportCount(0);
        }
        else if (search.getSearchView() == SearchView.om
                || search.getShow().isOmOnly()) {
            userId = userService.getUserInRole(Role.OM).getId();
        }
        else {
            userId = null;
        }
        if (search.getShow() == Show.closeToTermination) {
            List<InitiativeInfo> initiativesWithUnremovedVotes = findInitiativesWithUnremovedVotes(initiativeSettings.getOmSearchBeforeVotesRemovalDuration().toPeriod());
            return new InitiativeSublistWithTotalCount(initiativesWithUnremovedVotes, initiativesWithUnremovedVotes.size());
        }
        return initiativeDao.findInitiatives(search, userId, initiativeSettings.getMinSupportCountSettings());
    }

    @Override
    @Transactional(readOnly=true)
    public List<InitiativeInfo> findInitiativesWithUnremovedVotes(Period beforeDeadLine) {
        userService.getUserInRole(Role.OM);
        List<InitiativeInfo> initiatives = initiativeDao.findInitiativesWithUnremovedVotes();
        for (int i = initiatives.size() - 1; i >= 0; i--) {
            InitiativeInfo initiative = initiatives.get(i);
            if (!initiative.isVotesRemovalEndDateNear(LocalDate.now(), initiativeSettings.getVotesRemovalDuration(), beforeDeadLine)) {
                initiatives.remove(i);
            }
        }
        return initiatives;
    }

    @Cacheable("frontPageInitiatives")
    @Transactional(readOnly=true)
    public List<InitiativeInfo> getFrontPageInitiatives() {
        InitiativeSublistWithTotalCount initiativeSublistWithTotalCount = initiativeDao.findInitiatives(new InitiativeSearch().setLimit(2), null, initiativeSettings.getMinSupportCountSettings());
        return initiativeSublistWithTotalCount.list;
    }

    @Override
    @Cacheable("publicInitiativeCount")
    public InitiativeCountByState getPublicInitiativeCountByState() {
        InitiativeCountByState initiativeCountByState = new InitiativeCountByState();
        setPublicInitiativeCountsByStateTo(initiativeCountByState, initiativeDao.getPublicCounts(initiativeSettings.getMinSupportCountSettings()));
        return initiativeCountByState;
    }

    private static void setPublicInitiativeCountsByStateTo(InitiativeCountByState initiativeCountByState, OptionalHashMap<String, Long> publicCounts) {
        initiativeCountByState.setCanceled(publicCounts.get(Show.canceled.name()).or(0L));
        initiativeCountByState.setSentToParliament(publicCounts.get(Show.sentToParliament.name()).or(0L));
        initiativeCountByState.setEnded(publicCounts.get(Show.ended.name()).or(0L));
        initiativeCountByState.setRunning(publicCounts.get(Show.running.name()).or(0L));
        initiativeCountByState.setWaiting(publicCounts.get(Show.waiting.name()).or(0L));
    }

    @Override
    public InitiativeCountByStateOm getOmInitiativeCountByState() {
        userService.getUserInRole(Role.OM);
        InitiativeCountByStateOm initiativeCountByStateOm = new InitiativeCountByStateOm();
        OptionalHashMap<String, Long> initiativeCountsMap = initiativeDao.getOmCounts(initiativeSettings.getMinSupportCountSettings());

        setPublicInitiativeCountsByStateTo(initiativeCountByStateOm, initiativeCountsMap);
        setOmInitiativeCountsByStateTo(initiativeCountByStateOm, initiativeCountsMap);

        return initiativeCountByStateOm;
    }

    private void setOmInitiativeCountsByStateTo(InitiativeCountByStateOm initiativeCountByStateOm, OptionalHashMap<String, Long> initiativeCountsMap) {
        initiativeCountByStateOm.setPreparation(initiativeCountsMap.get(Show.preparation.name()).or(0L));
        initiativeCountByStateOm.setReview(initiativeCountsMap.get(Show.review.name()).or(0L));

        initiativeCountByStateOm.setCloseToTermination(findInitiativesWithUnremovedVotes(initiativeSettings.getOmSearchBeforeVotesRemovalDuration().toPeriod()).size());
    }

    private static void setPublicInitiativeCountsByStateTo(InitiativeCountByStateOm initiativeCountByState, OptionalHashMap<String, Long> publicCounts) {
        initiativeCountByState.setOmCanceled(publicCounts.get(Show.canceled.name()).or(0L));
        initiativeCountByState.setSentToParliament(publicCounts.get(Show.sentToParliament.name()).or(0L));
        initiativeCountByState.setEnded(publicCounts.get(Show.ended.name()).or(0L));
        initiativeCountByState.setRunning(publicCounts.get(Show.running.name()).or(0L));
        initiativeCountByState.setWaiting(publicCounts.get(Show.waiting.name()).or(0L));
    }

    private static long nullToZero(Long amount) {
        if (amount == null)
            return 0;
        return amount;
    }

    @Override
    @Transactional(readOnly=true)
    public Author getAuthor(Long initiativeId, Long userId) {
        return initiativeDao.getAuthor(initiativeId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public SupportCount getSupportCount(Long id) {
        return initiativeDao.getSupportCount(id);
    }

    @Override
    @Transactional(readOnly = false)
    public void updateSendToParliament(InitiativeManagement initiative, BindingResult errors) {
        userService.getUserInRole(Role.OM);
        ManagementSettings managementSettings = initiativeSettings.getManagementSettings(initiativeDao.get(initiative.getId()), userService.getCurrentUser());

        if (!managementSettings.isAllowMarkAsSentToParliament()) {
            throw new IllegalStateException("Not allowed to send to parliament");
        }

        if (validate(initiative, userService.getCurrentUser(), errors, OM.class)) {
            SendToParliamentData data = new SendToParliamentData();
            data.setParliamentURL(initiative.getParliamentURL());
            data.setParliamentIdentifier(initiative.getParliamentIdentifier());
            data.setParliamentSentTime(initiative.getParliamentSentTime());
            initiativeDao.updateSendToParliament(initiative.getId(), data);
            emailService.sendStatusInfoToVEVs(initiativeDao.getInitiativeForManagement(initiative.getId(), false), EmailMessageType.SENT_TO_PARLIAMENT);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void endInitiative(Long initiativeId) {
        LocalDate originalEndDate = initiativeDao.get(initiativeId).getEndDate();
        LocalDate today = LocalDate.now();

        if (originalEndDate.isAfter(today)) {
            initiativeDao.endInitiative(initiativeId, today.minusDays(1));
        }
    }

    @Override
    @Transactional(readOnly=false)
    public boolean sendInvitations(Long initiativeId) {
        final String METHOD_NAME = "sendInvitations";
        User user = userService.getUserInRole(Role.REGISTERED);
        
        Author currentAuthor = getAuthor(initiativeId, user.getId());

        if (currentAuthor == null) {
            throw new AccessDeniedException("user is not an author");
        }
        
        InitiativeManagement initiative = getInitiativeForManagement(initiativeId, true);
        ManagementSettings managementSettings = initiativeSettings.getManagementSettings(initiative, user);
        
        if (managementSettings.isAllowSendInvitations()) {
            initiativeDao.updateInitiativeState(initiative.getId(), user.getId(), InitiativeState.PROPOSAL, null);
    
            for (Invitation invitation : initiative.getInvitations()) {
                if (invitation.getSent() == null) { // send only unsent invitations
                    // Use invitation.id to ensure uniqueness! 
                    String invitationCode = invitation.getId() + encryptionService.randomToken(invitationCodeLength);
                    invitation.assignInvitationCode(invitationCode);
    
                    emailService.sendInvitation(initiative, invitation);                    
                    initiativeDao.updateInvitationSent(initiative.getId(), invitation.getId(), invitationCode);
                }
            }
            
            // Confirmation requests
            for (Author author : initiative.getAuthors()) {
                if (author.isRequiresConfirmationReminder()) {
                    emailService.sendConfirmationRequest(initiative, author);
                    initiativeDao.updateConfirmationRequestSent(initiativeId, author.getUserId());
                }
            }
            
            if (initiative.getInvitations().size() > 0) {
                emailService.sendInvitationSummary(initiative);
            }
            
            log(METHOD_NAME, initiativeId, user, true);
            return true;
        } else {
            log(METHOD_NAME, initiativeId, user, false);
            return false;
        }
    }
    
    @Override
    @Transactional(readOnly=true)
    public @Nullable Invitation getInvitation(Long initiativeId, String invitationCode) {
        Assert.notNull(initiativeId, "initiativeId");
        Assert.hasText(invitationCode, "invitationCode");
        
        return initiativeDao.getOpenInvitation(initiativeId, invitationCode, initiativeSettings.getInvitationExpirationDays());
    }

    @Override
    @Transactional(readOnly=false)
    public boolean declineInvitation(Long initiativeId, String invitationCode) {
        final String METHOD_NAME = "declineInvitation";
        
        Invitation invitation = initiativeDao.getOpenInvitation(initiativeId, invitationCode, initiativeSettings.getInvitationExpirationDays());
        if (invitation == null) {
            log(METHOD_NAME, initiativeId, userService.getCurrentUser(), false);
            return false;
        }
        else {
            // NOTE: There's no need to lock initiative
            initiativeDao.removeInvitation(initiativeId, invitationCode);

            InitiativeManagement initiative = initiativeDao.getInitiativeForManagement(initiativeId, false);
            List<String> authorEmails = initiativeDao.getAuthorEmailsWhichAreNotNull(initiativeId);
            emailService.sendInvitationRejectedInfoToVEVs(initiative, invitation.getEmail(), authorEmails);

            log(METHOD_NAME, initiativeId, userService.getCurrentUser(), true);
            return true;
        }
    }
    
    @Override
    @Transactional(readOnly=false)
    public void confirmCurrentAuthor(Long initiativeId) {
        final String METHOD_NAME = "confirmCurrentAuthor";
        User user = userService.getUserInRole(Role.REGISTERED);
        
        InitiativeManagement initiative = getInitiativeForManagement(initiativeId, true);        
        ManagementSettings managementSettings = initiativeSettings.getManagementSettings(initiative, user);
        
        if (managementSettings.isAllowConfirmCurrentAuthor()) {
            initiativeDao.confirmAuthor(initiativeId, user.getId());

            initiative = getInitiativeForManagement(initiativeId, false); // refresh values
            List<String> authorEmails = initiativeDao.getAuthorEmailsWhichAreNotNull(initiativeId);
            emailService.sendAuthorConfirmedInfoToVEVs(initiative, authorEmails);

            log(METHOD_NAME, initiativeId, user, true);
        } else {
            throw new IllegalStateException("Cannot confirm current author");
        }
    }
    
    @Override
    @Transactional(readOnly=false)
    public void deleteCurrentAuthor(Long initiativeId) {
        final String METHOD_NAME = "deleteCurrentAuthor";
        User user = userService.getUserInRole(Role.REGISTERED);
        
        InitiativeManagement initiative = getInitiativeForManagement(initiativeId, true);
        ManagementSettings managementSettings = initiativeSettings.getManagementSettings(initiative, user);
        
        if (managementSettings.isAllowDeleteCurrentAuthor()) {
            initiativeDao.deleteAuthor(initiativeId, user.getId());

            Author removedAuthor = initiative.getCurrentAuthor();
            initiative = initiativeDao.getInitiativeForManagement(initiativeId, false); // refresh values
            List<String> authorEmails = initiativeDao.getAuthorEmailsWhichAreNotNull(initiativeId);
            emailService.sendAuthorRemovedInfoToVEVs(initiative, removedAuthor, authorEmails);

            log(METHOD_NAME, initiativeId, user, true);
        } else {
            throw new IllegalStateException("Cannot delete current author");
        }
    }
    
    @Override
    @Transactional(readOnly=false)
    public boolean acceptInvitation(Long initiativeId, String invitationCode, Author author, Errors errors) {
        final String METHOD_NAME = "acceptInvitation";
        userService.getUserInRole(Role.AUTHENTICATED);
        //NOTE: user must be authenticated _before_ invitation verification, but persisting user happens _after_

        Assert.notNull(initiativeId, "initiativeId");
        Assert.notNull(invitationCode, "invitationCode");
        Assert.notNull(author, "author");

        User user = userService.currentAsRegisteredUser(); // Requires authenticated user and optionally registers

        InitiativeManagement initiative = initiativeDao.getInitiativeForManagement(initiativeId, true);
        Assert.notNull(initiative, "initiative");
        
        ManagementSettings managementSettings = initiativeSettings.getManagementSettings(initiative, user);
        
        if (managementSettings.isAllowAcceptInvitation()) {
            assignAuthorAutoFields(author, user);
            
            if (validate(author, user, errors)) {
                Invitation invitation = getInvitation(initiativeId, invitationCode);
                Assert.notNull(invitation, "invitation");
                
                if (getAuthor(initiativeId, user.getId()) != null) {
                    initiativeDao.updateAuthor(initiativeId, user.getId(), author);
                    if (author.isUnconfirmed()) {   // does automatic confirmation, if needed
                        initiativeDao.confirmAuthor(initiativeId, user.getId());
                    }
                } else {
                    initiativeDao.insertAuthor(initiativeId, user.getId(), author);
                }
                
                initiativeDao.removeInvitation(initiativeId, invitationCode);
                
                initiative = getInitiativeForManagement(initiativeId, false); // refresh values
                List<String> authorEmails = initiativeDao.getAuthorEmailsWhichAreNotNull(initiativeId);
                emailService.sendInvitationAcceptedInfoToVEVs(initiative, authorEmails);
                
                log(METHOD_NAME, initiativeId, user, true);
                return true;
            } else {
                log(METHOD_NAME, initiativeId, user, false);
                return false;
            }
        } else {
            throw new IllegalStateException("Accept invitation is not allowed at this point");
        }
        
    }

    @Override
    @Transactional(readOnly=false)
    // Even if it's not possible to rollback emails, 
    // it is important to have updatestate and removeinvitations in the same transaction
    public void sendToOM(Long initiativeId) {
        final String METHOD_NAME = "sendToOM";
        User user = userService.getUserInRole(Role.REGISTERED);
        
        InitiativeManagement initiative = getInitiativeForManagement(initiativeId, true);
        ManagementSettings managementSettings = initiativeSettings.getManagementSettings(initiative, user);
        
        // Allowed only for authors 
        if (!managementSettings.isAllowSendToOM()) {
            throw new IllegalStateException("Not allowed for current user or current state");
        }
        
        initiativeDao.updateInitiativeState(initiative.getId(), user.getId(), InitiativeState.REVIEW, null);
        initiativeDao.removeInvitations(initiative.getId());
        initiativeDao.removeUnconfirmedAuthors(initiative.getId());
        emailService.sendNotificationToOM(initiative);
        emailService.sendStatusInfoToVEVs(initiative, EmailMessageType.SENT_TO_OM);

        log(METHOD_NAME, initiativeId, user, true);
    }

    @Override
    @Transactional(readOnly=false)
    public void respondByOm(Long initiativeId, boolean accept, String comment, String acceptanceIdentifier) {
        final String METHOD_NAME = "respondByOm";
        User user = userService.getUserInRole(Role.OM);
        
        InitiativeManagement initiative = getInitiativeForManagement(initiativeId, true);
        ManagementSettings managementSettings = initiativeSettings.getManagementSettings(initiative, user);
        
        // Allowed only for non-author OM officials
        if (!managementSettings.isAllowRespondByOM()) {
            throw new IllegalStateException("Not allowed for current user or current state");
        }
        InitiativeState state = accept ? InitiativeState.ACCEPTED : InitiativeState.PROPOSAL;
        DateTime now = new DateTime();

        if (accept) {
            initiativeDao.updateInitiativeStateAndAcceptanceIdentifier(initiative.getId(), user.getId(), state , comment, acceptanceIdentifier);
        } else {
            initiativeDao.updateInitiativeState(initiative.getId(), user.getId(), state , comment);
        }
        // Update initiative dto
        initiative.assignModifierId(user.getId());
        initiative.assignModified(now);
        initiative.assignState(state);
        initiative.assignStateComment(comment);
        initiative.assignStateDate(now);
        if (accept) {
            initiative.setAcceptanceIdentifier(acceptanceIdentifier);
        }

        emailService.sendStatusInfoToVEVs(initiative, accept ? EmailMessageType.ACCEPTED_BY_OM : EmailMessageType.REJECTED_BY_OM);

        log(METHOD_NAME, initiativeId, user, true);
    }

    @Override
    @Transactional(readOnly=false)
    public boolean updateVRKResolution(InitiativeManagement initiative, Errors bindingResult) {
        final String METHOD_NAME = "updateVRKResolution";
        User user = userService.getUserInRole(Role.VRK);

        InitiativeManagement persistedInitiative = initiativeDao.getInitiativeForManagement(initiative.getId(), true);
        ManagementSettings managementSettings = initiativeSettings.getManagementSettings(persistedInitiative, user);
        
        if (managementSettings.isAllowRespondByVRK() && validate(initiative, user, bindingResult, VRK.class)) {
            initiativeDao.updateVRKResolution(
                    initiative.getId(), 
                    initiative.getVerifiedSupportCount(),
                    initiative.getVerified(),
                    initiative.getVerificationIdentifier(), user.getId());
            emailService.sendVRKResolutionToVEVs(initiativeDao.getInitiativeForManagement(initiative.getId(), false));

            log(METHOD_NAME, initiative.getId(), user, true);
            return true;
        } else {
            log(METHOD_NAME, initiative.getId(), user, false);
            return false;
        }
    }

    private void log(final String method, final Long initiativeId, final User user, final boolean ok) {
        log(method, initiativeId, user, ok, log);
    }
        
    static void log(final String method, final Long id, final User user, final boolean ok, Logger log) {
        if (log.isInfoEnabled()) {
            final String status = ok ? "OK" : "FAIL";
            final String idArg = id != null ? id.toString() : "_"; 
            if (user == null || user.isAnonymous()) {
                log.info("{}({}) {} by ANONYMOUS", new Object[] { method, idArg, status});
            } else {
                if (user.getId() != null) {
                    log.info("{}({}) {} by {} {} ({})", new Object[] { method, idArg, status, user.getFirstNames(), user.getLastName(), user.getId()});
                } else {
                    log.info("{}({}) {} by {} {}", new Object[] { method, idArg, status, user.getFirstNames(), user.getLastName()});
                }
            }
        }
    }

}
