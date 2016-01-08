package fi.om.initiative.service;


import fi.om.initiative.dao.TestHelper;
import fi.om.initiative.dto.User;
import fi.om.initiative.dto.author.AuthorRole;
import fi.om.initiative.dto.initiative.InitiativeManagement;
import fi.om.initiative.dto.initiative.InitiativeState;
import fi.om.initiative.web.HttpUserServiceImpl;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class EmailsTest extends EmailSpyConfiguration {

    public static final String FOLLOWER_EMAIL = "follower@example.com";

    public static final String AUTHOR_EMAIL = "author@example.com";
    @Resource
    private TestHelper testHelper;

    private Long userId;

    @Mocked
    HttpUserServiceImpl userService;

    @Resource
    private SupportVoteService supportVoteService;

    @Resource
    private InitiativeService initiativeService;

    @Resource
    private FollowService followService;

    @Before
    public void init() {
        testHelper.dbCleanup();
        userId = testHelper.createTestUser();

        new Expectations() {{
            userService.getUserInRole((Role[]) withNotNull());
            result = new Delegate() {
                private AtomicInteger count = new AtomicInteger(0);
                @SuppressWarnings("unused")
                public User getUserInRole(Role... roles) {
                    return new User(userId, DateTime.now(), "Kaikki", "Oikeudet", new LocalDate(1990, 1, 1), true, true);
                }
            };
            times = -1;
        }};

    }

    @Test
    public void emails_after_publication_are_sent() {

        final Long initiative = testHelper.create(
                new TestHelper.InitiativeDraft(userId, AUTHOR_EMAIL)
                        .withState(InitiativeState.ACCEPTED)
                        .withSupportCount(50001)

        );

        testHelper.createSupport(initiative, LocalDate.now());
        testHelper.addFollower(initiative, FOLLOWER_EMAIL);

        // When sent to VRK: Assert that emails to VRK and follower are sent

        supportVoteService.sendToVRK(initiative);

        assertSentEmailCount(3);
        assertSentEmail("kansalaisaloite.tarkastus@vrk.fi", "Kannatusilmoitusten määrän vahvistuspyyntö / Ansökan om bekräftelse av antalet stödförklaringar");
        assertSentEmail(FOLLOWER_EMAIL, "Kannatusilmoitusten tarkastuspyyntö on lähetetty Väestörekisterikeskukseen / Ansökan om att granska stödförklaringarna har skickats till Befolkningsregistercentralen");
        assertSentEmail(AUTHOR_EMAIL, "Kannatusilmoitusten tarkastuspyyntö on lähetetty Väestörekisterikeskukseen / Ansökan om att granska stödförklaringarna har skickats till Befolkningsregistercentralen");
        clearAllSentEmails();

        // When accepted by VRK: Assert that emails for follower are sent

        InitiativeManagement vrkData = new InitiativeManagement(initiative);

        vrkData.setVerificationIdentifier("lol");
        vrkData.setVerified(LocalDate.now());
        vrkData.setVerifiedSupportCount(51000);

        assertTrue(initiativeService.updateVRKResolution(vrkData, mock(Errors.class)));

        assertSentEmailCount(2);
        assertSentEmail(FOLLOWER_EMAIL, "Väestörekisterikeskus on vahvistanut kannatusilmoitusten määrän / Befolkningsregistercentralen har bekräftat antalet stödförklaringar");
        assertSentEmail(AUTHOR_EMAIL, "Väestörekisterikeskus on vahvistanut kannatusilmoitusten määrän / Befolkningsregistercentralen har bekräftat antalet stödförklaringar");

        clearAllSentEmails();

        // When marking as sent to parliament, assert that emails are sent.

        InitiativeManagement omToParliament = new InitiativeManagement(initiative);
        omToParliament.setParliamentURL("url");
        omToParliament.setParliamentIdentifier("identifier");
        omToParliament.setParliamentSentTime(LocalDate.now());

        initiativeService.updateSendToParliament(omToParliament, mock(BindingResult.class));

        assertSentEmailCount(2);
        assertSentEmail(FOLLOWER_EMAIL, "Aloite on lähetetty eduskuntaan / Initiativet har skickats till riksdagen");
        assertSentEmail(AUTHOR_EMAIL, "Aloite on lähetetty eduskuntaan / Initiativet har skickats till riksdagen");

    }

    @Test
    public void sending_initiative_to_review_sends_email_to_om() {

        final Long initiative = testHelper.create(
                new TestHelper.InitiativeDraft(userId)
                        .withName("Testialoite")
                        .withState(InitiativeState.PROPOSAL)
        );

        testHelper.makeAuthorFor(initiative, "initiator@example.com", AuthorRole.INITIATOR, testHelper.createTestUser());
        testHelper.makeAuthorFor(initiative, "reserve@example.com", AuthorRole.RESERVE, testHelper.createTestUser());

        initiativeService.sendToOM(initiative);

        assertSentEmailCount(3);
        assertSentEmail("kansalaisaloite.tarkastus@om.fi", "Kansalaisaloite tarkastettavaksi: Testialoite / Ett medborgarinitiativ för granskning: Testialoite");
        assertSentEmail("initiator@example.com", "Aloite on lähetetty tarkastettavaksi oikeusministeriön / Medborgarinitiativet har skickats till justitieministeriet för granskning");
        assertSentEmail("reserve@example.com", "Aloite on lähetetty tarkastettavaksi oikeusministeriön / Medborgarinitiativet har skickats till justitieministeriet för granskning");

    }


    @Test
    public void send_emails_for_vevs_and_followers_when_initiative_has_ended_normally() {


        LocalDate initiativeEndDate = new LocalDate(2000, 1, 1);
        LocalDate initiativeStartDate = initiativeEndDate.minusMonths(6);

        final Long initiative = testHelper.create(
                new TestHelper.InitiativeDraft(userId, AUTHOR_EMAIL)
                        .withName("Testialoite")
                        .withState(InitiativeState.PROPOSAL)
                        .isRunning(initiativeStartDate, initiativeEndDate)
                        .withSupportCount(51000)
        );

        testHelper.addFollower(initiative, FOLLOWER_EMAIL);

        followService.sendEmailsForEndedInitiatives(initiativeStartDate);
        assertSentEmailCount(0);

        followService.sendEmailsForEndedInitiatives(initiativeEndDate);
        assertSentEmailCount(0);

        followService.sendEmailsForEndedInitiatives(initiativeEndDate.plusDays(1));
        assertSentEmailCount(2);
        assertSentEmail(FOLLOWER_EMAIL, "Kannatusilmoitusten keruuaika on päättynyt / Insamlingen av stödförklaringar har avslutats");
        assertSentEmail(AUTHOR_EMAIL, "Kannatusilmoitusten keruuaika on päättynyt / Insamlingen av stödförklaringar har avslutats");

        clearAllSentEmails();
        followService.sendEmailsForEndedInitiatives(initiativeEndDate.plusDays(2));
        assertSentEmailCount(0);

    }



    // default.properties has:
    // initiative.minSupportCountForSearch = 50
    // initiative.requiredMinSupportCountDuration = P1M


}
