package fi.om.initiative.service;

import com.google.common.collect.Lists;
import com.mysema.commons.lang.Assert;
import fi.om.initiative.conf.IntegrationTestConfiguration;
import fi.om.initiative.dao.InitiativeDao;
import fi.om.initiative.dao.InitiativeDaoTest;
import fi.om.initiative.dao.NotFoundException;
import fi.om.initiative.dao.ReviewHistoryDao;
import fi.om.initiative.dto.EditMode;
import fi.om.initiative.dto.Invitation;
import fi.om.initiative.dto.author.Author;
import fi.om.initiative.dto.initiative.InitiativeManagement;
import fi.om.initiative.dto.initiative.InitiativePublic;
import fi.om.initiative.dto.initiative.InitiativeState;
import fi.om.initiative.util.ReviewHistoryType;
import fi.om.initiative.util.SnapshotCreator;
import mockit.Expectations;
import mockit.Mocked;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;

import javax.annotation.Resource;
import java.util.List;

import static junit.framework.Assert.*;
import static junit.framework.Assert.assertSame;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={IntegrationTestConfiguration.class})
public class InitiativeServiceTest extends ServiceTestBase {
    
    @Mocked InitiativeDao initiativeDao;
    @Mocked ReviewHistoryDao reviewHistoryDao;
    @Mocked UserService userService; 
    @Mocked EmailService emailService; 
    @Mocked Errors errors;

    @Autowired EncryptionService encryptionService;
    @Autowired SmartValidator validator;

    private InitiativeServiceImpl initiativeService;


    private final Invitation INVITATION = new Invitation(123l); 
    
    private final String invitationCode = INITIATIVE_MANAGEMENT.getId() + "4567";
    
    private final Author AUTHOR = InitiativeDaoTest.createAuthor(REGISTERED_USER.getId());
    
    private final Author AUTHOR_RESERVE = InitiativeDaoTest.createAuthor(RESERVE_USER.getId(), false, false, true);

    private HashCreator hashCreator = new HashCreator("some hash");
    
    final List<String> AUTHOR_EMAILS = Lists.newArrayList(); 
    {
        AUTHOR_EMAILS.add(AUTHOR.getContactInfo().getEmail());
        AUTHOR_EMAILS.add(AUTHOR_RESERVE.getContactInfo().getEmail());
    }


    @Before
    public void init() {
        initiativeService = new InitiativeServiceImpl(initiativeDao, reviewHistoryDao, userService, emailService, encryptionService, validator, INITIATIVE_SETTINGS, hashCreator);
    }   
    
    @Test
    public void Create_OK() {        
        INITIATIVE_MANAGEMENT.setCurrentAuthor(AUTHOR);

        AUTHOR.assignUserId(REGISTERED_USER.getId());
        AUTHOR.assignFirstNames(REGISTERED_USER.getFirstNames());
        AUTHOR.assignLastName(REGISTERED_USER.getLastName());
        AUTHOR.assignHomeMunicipality(REGISTERED_USER.getHomeMunicipality());
        
        // Strict expectations
        new Expectations() {{
            userService.getUserInRole(Role.AUTHENTICATED); result = REGISTERED_USER;
            userService.currentAsRegisteredUser(); result = REGISTERED_USER;
            initiativeDao.create(INITIATIVE_MANAGEMENT, REGISTERED_USER.getId()); result = INITIATIVE_MANAGEMENT.getId();
            
            initiativeDao.updateLinks(INITIATIVE_MANAGEMENT.getId(), INITIATIVE_MANAGEMENT.getLinks());
            initiativeDao.updateInvitations(INITIATIVE_MANAGEMENT.getId(),  INITIATIVE_MANAGEMENT.getInitiatorInvitations(), 
                    INITIATIVE_MANAGEMENT.getRepresentativeInvitations(), 
                    INITIATIVE_MANAGEMENT.getReserveInvitations());
            
            initiativeDao.insertAuthor(INITIATIVE_MANAGEMENT.getId(), REGISTERED_USER.getId(), AUTHOR);
        }};
        
        Long result = initiativeService.create(INITIATIVE_MANAGEMENT, errors);
        assertSame(INITIATIVE_MANAGEMENT.getId(), result);
        
    }
    
    @Test
    public void Initiative_for_Management_OK() {
        // Strict expectations
        new Expectations() {{
            userService.getUserInRole(Role.REGISTERED); result = REGISTERED_USER;
            initiativeDao.getInitiativeForManagement(INITIATIVE_MANAGEMENT.getId(), false); result = INITIATIVE_MANAGEMENT;
            initiativeDao.getAuthor(INITIATIVE_MANAGEMENT.getId(), REGISTERED_USER.getId()); result = AUTHOR;
        }};
        
        InitiativeManagement result = initiativeService.getInitiativeForManagement(INITIATIVE_MANAGEMENT.getId());
        assertSame(INITIATIVE_MANAGEMENT, result);
        assertNotNull(result.getCurrentAuthor());

        // No additional verifications
    }
    
    @Test(expected=NotFoundException.class)
    public void Initiative_for_Management_Initiative_Not_Found() {
        // Strict expectations
        new Expectations() {{
            userService.getUserInRole(Role.REGISTERED); result = REGISTERED_USER;
            initiativeDao.getInitiativeForManagement(INITIATIVE_MANAGEMENT.getId(), false); result = null;
        }};
        
        initiativeService.getInitiativeForManagement(INITIATIVE_MANAGEMENT.getId());
        // No additional verifications
    }
    
    @Test(expected=AccessDeniedException.class)
    public void Initiative_for_Management_Author_Not_Found() {
        // Strict expectations
        new Expectations() {{
            userService.getUserInRole(Role.REGISTERED); result = REGISTERED_USER;
            initiativeDao.getInitiativeForManagement(INITIATIVE_MANAGEMENT.getId(), false); result = INITIATIVE_MANAGEMENT;
            initiativeDao.getAuthor(INITIATIVE_MANAGEMENT.getId(), REGISTERED_USER.getId()); result = null;
        }};
        
        initiativeService.getInitiativeForManagement(INITIATIVE_MANAGEMENT.getId());
        // No additional verifications
    }

    @Test(expected = AccessDeniedException.class)
    public void Initiative_for_Public_throws_access_denied_if_initiative_in_proposal_state_and_logged_in_as_incorrect_author() {

        INITIATIVE_PUBLIC.assignState(InitiativeState.PROPOSAL);

        // Strict expectations
        new Expectations() {{
            initiativeDao.getInitiativeForPublic(INITIATIVE_PUBLIC.getId()); result = INITIATIVE_PUBLIC;
            userService.getUserInRole(Role.REGISTERED); result = REGISTERED_USER;
            initiativeDao.getAuthor(INITIATIVE_PUBLIC.getId(), REGISTERED_USER.getId()); result = null;

        }};

        InitiativePublic result = initiativeService.getInitiativeForPublic(INITIATIVE_PUBLIC.getId());
        assertSame(INITIATIVE_PUBLIC, result);

    }

    @Test
    public void Initiative_for_Public_OK_when_initiative_in_draft_state_and_idHash_is_given() {
        INITIATIVE_PUBLIC.assignState(InitiativeState.DRAFT);

        // Strict expectations
        new Expectations() {{
            initiativeDao.getInitiativeForPublic(INITIATIVE_PUBLIC.getId()); result = INITIATIVE_PUBLIC;
        }};

        InitiativePublic result = initiativeService.getInitiativeForPublic(INITIATIVE_PUBLIC.getId(), hashCreator.hash(INITIATIVE_PUBLIC.getId()));
        assertSame(INITIATIVE_PUBLIC, result);

    }

    @Test
    public void Initiative_for_Public_OK() {
        INITIATIVE_PUBLIC.assignState(InitiativeState.ACCEPTED);
        
        // Strict expectations
        new Expectations() {{
            initiativeDao.getInitiativeForPublic(INITIATIVE_PUBLIC.getId()); result = INITIATIVE_PUBLIC;
        }};
                
        InitiativePublic result = initiativeService.getInitiativeForPublic(INITIATIVE_PUBLIC.getId());
        assertSame(INITIATIVE_PUBLIC, result);
    }
    
    @Test(expected=NotFoundException.class)
    public void Initiative_for_Public_Not_Found() {
        
        // Strict expectations
        new Expectations() {{
            initiativeDao.getInitiativeForPublic(INITIATIVE_PUBLIC.getId()); result = null;
        }};
                
        initiativeService.getInitiativeForPublic(INITIATIVE_PUBLIC.getId());
    }
    
    @Test
    public void Send_Invitations_OK(){
        INITIATIVE_MANAGEMENT.assignState(InitiativeState.PROPOSAL);
        addAuthorsToInitiative();
        addInvitationsToInitiative();

        // Strict expectations
        new Expectations(encryptionService) {{
            userService.getUserInRole(Role.REGISTERED); result = REGISTERED_USER;
            initiativeDao.getAuthor(INITIATIVE_MANAGEMENT.getId(), REGISTERED_USER.getId()); result = AUTHOR;

            userService.getUserInRole(Role.REGISTERED); result = REGISTERED_USER;
            initiativeDao.getInitiativeForManagement(INITIATIVE_MANAGEMENT.getId(), true); result = INITIATIVE_MANAGEMENT;
            initiativeDao.getAuthor(INITIATIVE_MANAGEMENT.getId(), REGISTERED_USER.getId()); result = AUTHOR;

            initiativeDao.updateInitiativeState(INITIATIVE_MANAGEMENT.getId(), REGISTERED_USER.getId(), InitiativeState.PROPOSAL, null);
            
            encryptionService.randomToken(12); result = "4567";
            
            emailService.sendInvitation(INITIATIVE_MANAGEMENT, INVITATION);
            initiativeDao.updateInvitationSent(INITIATIVE_MANAGEMENT.getId(), INVITATION.getId(), invitationCode);
            emailService.sendInvitationSummary(INITIATIVE_MANAGEMENT);

        }};
        
        assertTrue(initiativeService.sendInvitations(INITIATIVE_MANAGEMENT.getId()));
    }
    
    @Test
    public void Accept_invitation_OK(){
        INITIATIVE_PUBLIC.assignState(InitiativeState.PROPOSAL);
        INITIATIVE_MANAGEMENT.assignState(InitiativeState.PROPOSAL);
        addInvitationsToInitiative();
        
        // Strict expectations
        new Expectations() {{
            userService.getUserInRole(Role.AUTHENTICATED); result = RESERVE_USER; 
            
            userService.currentAsRegisteredUser(); result = RESERVE_USER;
            
            initiativeDao.getInitiativeForManagement(INITIATIVE_PUBLIC.getId(), true); result = INITIATIVE_MANAGEMENT;

            errors.hasErrors(); result = false;
            
            initiativeDao.getOpenInvitation(INITIATIVE_PUBLIC.getId(), invitationCode, anyInt); result = INVITATION;
            initiativeDao.getAuthor(INITIATIVE_MANAGEMENT.getId(), RESERVE_USER.getId()); result = AUTHOR;
            initiativeDao.updateAuthor(INITIATIVE_PUBLIC.getId(), RESERVE_USER.getId(), AUTHOR);
            initiativeDao.confirmAuthor(INITIATIVE_PUBLIC.getId(), RESERVE_USER.getId());

            initiativeDao.removeInvitation(INITIATIVE_PUBLIC.getId(), invitationCode);            

            userService.getUserInRole(Role.REGISTERED); result = REGISTERED_USER; 
            initiativeDao.getInitiativeForManagement(INITIATIVE_MANAGEMENT.getId(), false); result = INITIATIVE_MANAGEMENT;
            initiativeDao.getAuthor(INITIATIVE_MANAGEMENT.getId(), REGISTERED_USER.getId()); result = AUTHOR;

            initiativeDao.getAuthorEmailsWhichAreNotNull(INITIATIVE_MANAGEMENT.getId()); result = AUTHOR_EMAILS;
            emailService.sendInvitationAcceptedInfoToVEVs(INITIATIVE_MANAGEMENT, AUTHOR_EMAILS);
        }};
        
        assertTrue( initiativeService.acceptInvitation(INITIATIVE_PUBLIC.getId(), invitationCode, AUTHOR, errors) );
    }
    
    @Test
    public void Decline_invitation_OK(){
        INITIATIVE_PUBLIC.assignState(InitiativeState.PROPOSAL);
        addInvitationsToInitiative();

        // Strict expectations
        new Expectations() {{
            initiativeDao.getOpenInvitation(INITIATIVE_PUBLIC.getId(), invitationCode, anyInt); result = INVITATION;
            
            initiativeDao.removeInvitation(INITIATIVE_PUBLIC.getId(), invitationCode);
            
            initiativeDao.getInitiativeForManagement(INITIATIVE_PUBLIC.getId(), false); result = INITIATIVE_MANAGEMENT;
            initiativeDao.getAuthorEmailsWhichAreNotNull(INITIATIVE_MANAGEMENT.getId()); result = AUTHOR_EMAILS;
            emailService.sendInvitationRejectedInfoToVEVs(INITIATIVE_MANAGEMENT, INVITATION.getEmail(), AUTHOR_EMAILS);
            
        }};
        
        initiativeService.declineInvitation(INITIATIVE_PUBLIC.getId(), invitationCode);
        
    }
    
    @Test
    public void Confirm_Current_Author_OK(){
        INITIATIVE_MANAGEMENT.assignState(InitiativeState.PROPOSAL);

        // Strict expectations
        new Expectations() {{
            userService.getUserInRole(Role.REGISTERED); result = REGISTERED_USER; minTimes = 1; maxTimes = 2;
            initiativeDao.getInitiativeForManagement(INITIATIVE_MANAGEMENT.getId(), true); result = INITIATIVE_MANAGEMENT;
            initiativeDao.getAuthor(INITIATIVE_MANAGEMENT.getId(), REGISTERED_USER.getId()); result = AUTHOR;

            initiativeDao.confirmAuthor(INITIATIVE_MANAGEMENT.getId(), REGISTERED_USER.getId());

            userService.getUserInRole(Role.REGISTERED); result = REGISTERED_USER; 
            initiativeDao.getInitiativeForManagement(INITIATIVE_MANAGEMENT.getId(), false); result = INITIATIVE_MANAGEMENT;
            initiativeDao.getAuthor(INITIATIVE_MANAGEMENT.getId(), REGISTERED_USER.getId()); result = AUTHOR;

            initiativeDao.getAuthorEmailsWhichAreNotNull(INITIATIVE_MANAGEMENT.getId()); result = AUTHOR_EMAILS;
            emailService.sendAuthorConfirmedInfoToVEVs(INITIATIVE_MANAGEMENT, AUTHOR_EMAILS);
        }};
     
        initiativeService.confirmCurrentAuthor(INITIATIVE_MANAGEMENT.getId());
    }
    
    @Test
    public void Delete_Current_Author_OK(){
        // Delete author is allowed in initiative states: DRAFT, PROPOSAL, REVIEW
        INITIATIVE_MANAGEMENT.assignState(InitiativeState.PROPOSAL);
        addAuthorsToInitiative();
        
        // Strict expectations
        new Expectations() {{
            userService.getUserInRole(Role.REGISTERED); result = REGISTERED_USER; minTimes = 1; maxTimes = 2;
            initiativeDao.getInitiativeForManagement(INITIATIVE_MANAGEMENT.getId(), true); result = INITIATIVE_MANAGEMENT;
            
            initiativeDao.getAuthor(INITIATIVE_MANAGEMENT.getId(), REGISTERED_USER.getId()); result = AUTHOR;
            initiativeDao.deleteAuthor(INITIATIVE_MANAGEMENT.getId(), REGISTERED_USER.getId());            

            initiativeDao.getInitiativeForManagement(INITIATIVE_MANAGEMENT.getId(), false); result = INITIATIVE_MANAGEMENT;
            initiativeDao.getAuthorEmailsWhichAreNotNull(INITIATIVE_MANAGEMENT.getId()); result = AUTHOR_EMAILS;
            emailService.sendAuthorRemovedInfoToVEVs(INITIATIVE_MANAGEMENT, AUTHOR, AUTHOR_EMAILS);
        }};
        
        initiativeService.deleteCurrentAuthor(INITIATIVE_MANAGEMENT.getId());
    }
    
    @Test
    public void Send_to_OM_OK(){
        INITIATIVE_MANAGEMENT.assignState(InitiativeState.PROPOSAL);
        addAuthorsToInitiative();
        
        // Strict expectations
        new Expectations() {{
            userService.getUserInRole(Role.REGISTERED); result = REGISTERED_USER; minTimes = 1; maxTimes = 2;
            initiativeDao.getInitiativeForManagement(INITIATIVE_MANAGEMENT.getId(), true); result = INITIATIVE_MANAGEMENT;
            initiativeDao.getAuthor(INITIATIVE_MANAGEMENT.getId(), REGISTERED_USER.getId()); result = AUTHOR;

            initiativeDao.updateInitiativeState(INITIATIVE_MANAGEMENT.getId(), REGISTERED_USER.getId(), InitiativeState.REVIEW, null);
            initiativeDao.removeInvitations(INITIATIVE_MANAGEMENT.getId());
            initiativeDao.removeUnconfirmedAuthors(INITIATIVE_MANAGEMENT.getId());
            
            emailService.sendNotificationToOM(INITIATIVE_MANAGEMENT);
            emailService.sendStatusInfoToVEVs(INITIATIVE_MANAGEMENT, EmailMessageType.SENT_TO_OM);
        }};
    
        initiativeService.sendToOM(INITIATIVE_MANAGEMENT.getId());
    }
    
    @Test
    public void Respond_by_OM_Accept_OK(){
        addAuthorsToInitiative();
        INITIATIVE_MANAGEMENT.assignState(InitiativeState.REVIEW);
        final String comment = "OM comment";
        final String acceptanceIdentifier = "123/23/59";

        // Strict expectations
        new Expectations() {{
            userService.getUserInRole(Role.OM); result = OM_USER;

            userService.getUserInRole(Role.REGISTERED); result = OM_USER;
            initiativeDao.getInitiativeForManagement(INITIATIVE_MANAGEMENT.getId(), true); result = INITIATIVE_MANAGEMENT;
            initiativeDao.getAuthor(INITIATIVE_MANAGEMENT.getId(), OM_USER.getId()); result = null;
            
            initiativeDao.updateInitiativeStateAndAcceptanceIdentifier(INITIATIVE_MANAGEMENT.getId(), OM_USER.getId(), InitiativeState.ACCEPTED, comment, acceptanceIdentifier);
            emailService.sendStatusInfoToVEVs(INITIATIVE_MANAGEMENT, EmailMessageType.ACCEPTED_BY_OM);

        }};
        
        initiativeService.respondByOm(INITIATIVE_MANAGEMENT.getId(), true, comment, acceptanceIdentifier);
    }


    @Test
    public void Respond_by_OM_Reject_OK(){
        addAuthorsToInitiative();
        INITIATIVE_MANAGEMENT.assignState(InitiativeState.REVIEW);
        final String comment = "OM comment";

        // Strict expectations
        new Expectations() {{
            userService.getUserInRole(Role.OM); result = OM_USER;

            userService.getUserInRole(Role.REGISTERED); result = OM_USER;
            initiativeDao.getInitiativeForManagement(INITIATIVE_MANAGEMENT.getId(), true); result = INITIATIVE_MANAGEMENT;
            initiativeDao.getAuthor(INITIATIVE_MANAGEMENT.getId(), OM_USER.getId()); result = null;
            
            initiativeDao.updateInitiativeState(INITIATIVE_MANAGEMENT.getId(), OM_USER.getId(), InitiativeState.PROPOSAL, comment);
            emailService.sendStatusInfoToVEVs(INITIATIVE_MANAGEMENT, EmailMessageType.REJECTED_BY_OM);
        }};
        
        initiativeService.respondByOm(INITIATIVE_MANAGEMENT.getId(), false, comment, null);
    }
    
    @Test
    public void Update_OK(){
        addAuthorsToInitiative();
        INITIATIVE_MANAGEMENT.assignState(InitiativeState.PROPOSAL);

        // Strict expectations
        new Expectations() {{
            userService.getUserInRole(Role.REGISTERED); result = REGISTERED_USER; minTimes = 1; maxTimes = 2;
            initiativeDao.getInitiativeForManagement(INITIATIVE_MANAGEMENT.getId(), true); result = INITIATIVE_MANAGEMENT;
            initiativeDao.getAuthor(INITIATIVE_MANAGEMENT.getId(), REGISTERED_USER.getId()); result = AUTHOR;
            
            initiativeDao.updateAuthor(INITIATIVE_MANAGEMENT.getId(), REGISTERED_USER.getId(), AUTHOR);
            initiativeDao.updateInitiative(INITIATIVE_MANAGEMENT, REGISTERED_USER.getId(), false, false);
        }};
        
        initiativeService.update(INITIATIVE_MANAGEMENT, EditMode.CURRENT_AUTHOR, errors);
    }

    @Test
    public void test_snapshot_creator() {

        String realSnapshot = SnapshotCreator.create(INITIATIVE_MANAGEMENT_CONSTANT);

        String snapshot = "Nimi" +  "\n\n" +
                "Ehdotus" +  "\n\n" +
                "Perustelut" +  "\n\n" +
                "Nimi-sv" + "\n\n" +
                "Ehdotus-sv" + "\n\n" +
                "Perustelut-sv" + "\n\n" +
                "http://www.solita.fi" + "\n\n" +
                "Solita" + ": " + "http://www.solita.fi" + "\n" +
                "Solita" + ": " + "http://www.solita.fi" + "\n";

        assertEquals(snapshot, realSnapshot);

    }

    @Test
    public void test_snapshot_creator_with_empty_initiative() {

        String realSnapshot = SnapshotCreator.create(new InitiativeManagement());

        String snapshot = "" +  "\n\n" +
                "" +  "\n\n" +
                "" +  "\n\n" +
                "" + "\n\n" +
                "" + "\n\n" +
                "" + "\n\n" +
                "" + "\n\n";

        assertEquals(snapshot, realSnapshot);

    }

    @Test
    public void check_initiative_for_links(){
        InitiativeManagement initiativeManagement = InitiativeDaoTest.createNotEndedInitiativeWithGivenContent("https://hs.fi lol lol", "ja jotain muuta linkkiä    http://huvudstagbladet.fi", "tässäpä nettisivu www.solita.fi", "www.solita.se och https://www.lol.se");
        List<String> links = initiativeService.checkProposalAndRationalForLinks(initiativeManagement);
        assertEquals(links.get(0), "https://hs.fi");
        assertEquals(links.get(1), "http://huvudstagbladet.fi");
        assertEquals(links.get(2), "www.solita.fi");
        assertEquals(links.get(3), "www.solita.se");
        assertEquals(links.get(4), "https://www.lol.se");
    }


    
    private void addAuthorsToInitiative(){
        AUTHOR.assignConfirmed(new DateTime());
        AUTHOR_RESERVE.assignConfirmed(new DateTime());
        
        List<Author> authors = Lists.newArrayList();
        authors.add(AUTHOR);
        authors.add(AUTHOR_RESERVE);
        INITIATIVE_MANAGEMENT.assignAuthors(authors);
    }

    private void addInvitationsToInitiative(){
        List<Invitation> invitations = Lists.newArrayList();
        INVITATION.assignInvitationCode(invitationCode);
        invitations.add(INVITATION);
        
        INITIATIVE_MANAGEMENT.setReserveInvitations(invitations);
    }

    
}

