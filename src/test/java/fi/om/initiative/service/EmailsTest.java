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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
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
    public void send_emails_for_vevs_and_followers_when_initiative_has_ended_normally_and_has_enough_support_votes() {


        LocalDate initiativeEndDate = LocalDate.now().minusDays(1);
        LocalDate initiativeStartDate = initiativeEndDate.minusMonths(6);

        final Long initiative = testHelper.create(
                new TestHelper.InitiativeDraft(userId, AUTHOR_EMAIL)
                        .withName("Testialoite")
                        .withState(InitiativeState.PROPOSAL)
                        .isRunning(initiativeStartDate, initiativeEndDate)
                        .withSupportCount(51000)
        );

        testHelper.addFollower(initiative, FOLLOWER_EMAIL);

        followService.sendEmailsForEndedInitiatives(initiativeEndDate.plusDays(1));
        assertSentEmailCount(2);
        assertSentEmail(FOLLOWER_EMAIL, "Kannatusilmoitusten keruuaika on päättynyt / Insamlingen av stödförklaringar har avslutats");

        assertSentEmail(AUTHOR_EMAIL, "Kannatusilmoitusten keruuaika on päättynyt / Insamlingen av stödförklaringar har avslutats",
                "Kannatusilmoitusten kerääminen on päättynyt",
                "Aloite keräsi 51,000 kannatusilmoitusta, joista 51,000 palvelussa kansalaisaloite.fi ja muissa palveluissa 0 kpl",
                "Vastuuhenkilöt ovat nyt velvollisia lähettämään kannatusilmoitukset väestörekisterikeskuksen tarkastettavaksi");

        assertMailsSentOnlyForDate(
                initiativeEndDate.plusDays(1),
                2,
                initiativeStartDate.minusDays(10),
                initiativeEndDate.plusDays(10)
        );

    }

    // default.properties has:
    // initiative.minSupportCountForSearch = 50
    // initiative.requiredMinSupportCountDuration = P1M

    @Test
    public void send_emails_for_vevs_and_followers_when_initiative_has_ended_normally_and_has_not_enough_support_votes() {


        LocalDate initiativeEndDate = LocalDate.now().minusDays(1);
        LocalDate initiativeStartDate = initiativeEndDate.minusMonths(6);

        final Long initiative = testHelper.create(
                new TestHelper.InitiativeDraft(userId, AUTHOR_EMAIL)
                        .withName("Testialoite")
                        .withState(InitiativeState.PROPOSAL)
                        .isRunning(initiativeStartDate, initiativeEndDate)
                        .withExternalSupportCount(10000)
                        .withSupportCount(39000)
        );

        testHelper.addFollower(initiative, FOLLOWER_EMAIL);

        followService.sendEmailsForEndedInitiatives(initiativeEndDate.plusDays(1));
        assertSentEmailCount(2);
        assertSentEmail(FOLLOWER_EMAIL, "Kannatusilmoitusten keruuaika on päättynyt / Insamlingen av stödförklaringar har avslutats");

        assertSentEmail(AUTHOR_EMAIL, "Kannatusilmoitusten keruuaika on päättynyt / Insamlingen av stödförklaringar har avslutats",
                "Kannatusilmoitusten kerääminen on päättynyt",
                "Aloite keräsi 49,000 kannatusilmoitusta, joista 39,000 palvelussa kansalaisaloite.fi ja muissa palveluissa 10,000 kpl",
                "Kerääminen jäi 1,000 kpl vaille vaaditun 50,000 kannatusilmoituksen, jotta aloite etenisi eduskunnan käsittelyyn");

        assertMailsSentOnlyForDate(
                initiativeEndDate.plusDays(1),
                2,
                initiativeStartDate.minusDays(10),
                initiativeEndDate.plusDays(10)
        );

    }


    @Test
    public void send_emails_for_vevs_and_followers_when_initiative_ends_due_not_enough_support_votes_in_month() {

        LocalDate initiativeStartDate = LocalDate.now().minusMonths(1);
        LocalDate initiativeEndDate = initiativeStartDate.plusMonths(6);

        final Long initiative = testHelper.create(
                new TestHelper.InitiativeDraft(userId, AUTHOR_EMAIL)
                        .withName("Testialoite")
                        .withState(InitiativeState.PROPOSAL)
                        .isRunning(initiativeStartDate, initiativeEndDate)
                        .withSupportCount(49)
        );

        testHelper.addFollower(initiative, FOLLOWER_EMAIL);

        assertMailsSentOnlyForDate(
                initiativeStartDate.plusMonths(1).plusDays(1),
                2,
                initiativeStartDate.minusDays(10),
                initiativeEndDate.plusDays(10)
        );

        clearAllSentEmails();
        followService.sendEmailsForEndedInitiatives(initiativeStartDate.plusMonths(1).plusDays(1));
        assertSentEmailCount(2);
        assertSentEmail(FOLLOWER_EMAIL, "Kannatusilmoitusten keruuaika on päättynyt / Insamlingen av stödförklaringar har avslutats");

        assertSentEmail(AUTHOR_EMAIL, "Kannatusilmoitusten keruuaika on päättynyt / Insamlingen av stödförklaringar har avslutats",
                "Kannatusilmoitusten kerääminen on päättynyt",
                "Aloite ei kerännyt vaadittua 50 kannatusilmoitusta 1 kuukauden aikana",
                "Aloite keräsi 49 kannatusilmoitusta, joista 49 kpl palvelussa kansalaisaloite.fi ja muissa palveluissa 0 kpl.");

    }

    /**
     * Assert that when sending emails daily from dateSpanStart to dateSpanEnd, emails are only sent once on given date and given amount.
     */
    private void assertMailsSentOnlyForDate(LocalDate dateToSendMails, int amountOfEmailsToSend, LocalDate dateSpanStart, LocalDate dateSpanEnd) {

        LocalDate dateIteration = dateSpanStart;
        while (!dateIteration.isAfter(dateSpanEnd)) {
            clearAllSentEmails();
            followService.sendEmailsForEndedInitiatives(dateIteration);

            if (dateToSendMails.equals(dateIteration)) {
                assertThat("Emails sent for " + dateIteration, getAllSentEmails(), hasSize(amountOfEmailsToSend));
            }
            else {
                assertThat("Emails sent for " + dateIteration, getAllSentEmails(), hasSize(0));
            }
            dateIteration = dateIteration.plusDays(1);

        }

    }


}
