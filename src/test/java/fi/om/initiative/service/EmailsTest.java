package fi.om.initiative.service;


import fi.om.initiative.dao.TestHelper;
import fi.om.initiative.dto.FollowInitiativeDto;
import fi.om.initiative.dto.User;
import fi.om.initiative.dto.author.AuthorRole;
import fi.om.initiative.dto.initiative.InitiativeManagement;
import fi.om.initiative.dto.initiative.InitiativeState;
import fi.om.initiative.sql.QInitiative;
import fi.om.initiative.web.HttpUserServiceImpl;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Errors;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertFalse;
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
                        .withState(InitiativeState.ACCEPTED)
                        .isRunning(initiativeStartDate, initiativeEndDate)
                        .withExternalSupportCount(1)
                        .withSupportCount(51000)
        );

        testHelper.addFollower(initiative, FOLLOWER_EMAIL);

        followService.sendEmailsForEndedInitiatives(initiativeEndDate.plusDays(1));
        assertSentEmailCount(2);
        assertSentEmail(FOLLOWER_EMAIL, "Kannatusilmoitusten kerääminen on päättynyt / Insamlingen av stödförklaringar har avslutats");

        assertSentEmail(AUTHOR_EMAIL, "Kannatusilmoitusten kerääminen on päättynyt / Insamlingen av stödförklaringar har avslutats",
                "Kannatusilmoitusten kerääminen on päättynyt",
                "Aloite keräsi 51,001 kannatusilmoitusta, joista 51,000 kpl kansalaisaloite.fi-palvelussa ja 1 kpl muilla menetelmillä",
                "Aloitteen vastuuhenkilöiden tulee toimittaa kannatusilmoitukset Väestörekisterikeskuksen tarkastettaviksi kuuden kuukauden sisällä keräyksen päättymisestä, jotta aloite voi edetä eduskunnan käsiteltäväksi");

        assertEndingEmailsAreSentOnlyForDate(
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
                        .withState(InitiativeState.ACCEPTED)
                        .isRunning(initiativeStartDate, initiativeEndDate)
                        .withExternalSupportCount(10000)
                        .withSupportCount(39000)
        );

        testHelper.addFollower(initiative, FOLLOWER_EMAIL);

        followService.sendEmailsForEndedInitiatives(initiativeEndDate.plusDays(1));
        assertSentEmailCount(2);
        assertSentEmail(FOLLOWER_EMAIL, "Kannatusilmoitusten kerääminen on päättynyt / Insamlingen av stödförklaringar har avslutats");

        assertSentEmail(AUTHOR_EMAIL, "Kannatusilmoitusten kerääminen on päättynyt / Insamlingen av stödförklaringar har avslutats",
                "Kannatusilmoitusten kerääminen on päättynyt",
                "Aloite keräsi 49,000 kannatusilmoitusta, joista 39,000 kpl kansalaisaloite.fi-palvelussa ja 10,000 kpl muilla menetelmillä.",
                "Aloitteen olisi pitänyt kerätä vähintään 50,000 kannatusilmoitusta edetäkseen eduskunnan käsittelyyn.");

        assertEndingEmailsAreSentOnlyForDate(
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
                        .withState(InitiativeState.ACCEPTED)
                        .isRunning(initiativeStartDate, initiativeEndDate)
                        .withSupportCount(49)
        );

        testHelper.addFollower(initiative, FOLLOWER_EMAIL);

        assertEndingEmailsAreSentOnlyForDate(
                initiativeStartDate.plusMonths(1).plusDays(1),
                2,
                initiativeStartDate.minusDays(10),
                initiativeEndDate.plusDays(10)
        );

        clearAllSentEmails();
        followService.sendEmailsForEndedInitiatives(initiativeStartDate.plusMonths(1).plusDays(1));
        assertSentEmailCount(2);
        assertSentEmail(FOLLOWER_EMAIL, "Kannatusilmoitusten kerääminen on päättynyt / Insamlingen av stödförklaringar har avslutats");

        assertSentEmail(AUTHOR_EMAIL, "Kannatusilmoitusten kerääminen on päättynyt / Insamlingen av stödförklaringar har avslutats",
                "Kannatusilmoitusten kerääminen on päättynyt",
                "Aloite ei kerännyt vaadittua 50 kannatusilmoitusta yhden kuukauden aikana.",
                "Aloite keräsi 49 kannatusilmoitusta, joista 49 kpl kansalaisaloite.fi-palvelussa ja 0 kpl muilla menetelmillä.");

    }

    @Test
    public void do_not_send_ending_emails_for_initiatives_that_are_not_published() {

        LocalDate initiativeStartDate = LocalDate.now().minusMonths(1);
        LocalDate initiativeEndDate = initiativeStartDate.plusMonths(6);

        final Long initiative = testHelper.create(
                new TestHelper.InitiativeDraft(userId, AUTHOR_EMAIL)
                        .withName("Testialoite")
                        .withState(InitiativeState.PROPOSAL)
                        .isRunning(initiativeStartDate, initiativeEndDate)
                        .withExternalSupportCount(0)
                        .withSupportCount(0)
        );

        testHelper.addFollower(initiative, FOLLOWER_EMAIL);

        LocalDate theDayTosend = initiativeStartDate.plusMonths(1).plusDays(1);
        assertEndingEmailsAreSentOnlyForDate(
                theDayTosend,
                0,
                initiativeStartDate.minusDays(10),
                initiativeEndDate.plusDays(10)
        );

        clearAllSentEmails();
        testHelper.updateForTesting(initiative, QInitiative.initiative.state, InitiativeState.ACCEPTED);
        followService.sendEmailsForEndedInitiatives(theDayTosend);
        assertEndingEmailsAreSentOnlyForDate(
                theDayTosend,
                2,
                initiativeStartDate.minusDays(10),
                initiativeEndDate.plusDays(10)
        );

    }

    /**
     * Assert that when sending emails daily from dateSpanStart to dateSpanEnd, emails are only sent once on given date and given amount.
     */
    private void assertEndingEmailsAreSentOnlyForDate(LocalDate dateToSendMails, int amountOfEmailsToSend, LocalDate dateSpanStart, LocalDate dateSpanEnd) {

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

    /**
     * Assert that when sending half-way emails daily from dateSpanStart to dateSpanEnd, emails are only sent once on given date and given amount.
     */
    private void assertHalfwayEmailsAreSentOnlyForDate(LocalDate dateToSendMails, int amountOfEmailsToSend, LocalDate dateSpanStart, LocalDate dateSpanEnd) {

        LocalDate dateIteration = dateSpanStart;
        while (!dateIteration.isAfter(dateSpanEnd)) {
            clearAllSentEmails();
            followService.sendEmailsHalfwayBetweenForStillRunningInitiatives(dateIteration);

            if (dateToSendMails.equals(dateIteration)) {
                assertThat("Emails sent for " + dateIteration, getAllSentEmails(), hasSize(amountOfEmailsToSend));
            }
            else {
                assertThat("Emails sent for " + dateIteration, getAllSentEmails(), hasSize(0));
            }
            dateIteration = dateIteration.plusDays(1);

        }

    }

    @Test
    public void follow_initiative_email_is_validated() {
        Long initiativeId = testHelper.createRunningPublicInitiative(userId, "test");

        FollowInitiativeDto followInitiativeDto = new FollowInitiativeDto();
        followInitiativeDto.setEmail("INVALID EMAIL");

        assertFalse(followService.followInitiative(initiativeId, followInitiativeDto, emailValidationErrors(followInitiativeDto)));

        assertSentEmailCount(0);
    }

    @Test
    public void follow_initiative_sends_confirmation_email() {

        Long initiativeId = testHelper.createRunningPublicInitiative(userId, "test");

        FollowInitiativeDto followInitiativeDto = new FollowInitiativeDto();
        followInitiativeDto.setEmail("follower@example.com");

        assertTrue(followService.followInitiative(initiativeId, followInitiativeDto, emailValidationErrors(followInitiativeDto)));

        assertSentEmailCount(1);
        assertSentEmail("follower@example.com", "Olet tilannut aloitteen sähköpostitiedotteet / SV Olet tilannut aloitteen sähköpostitiedotteet",
                "localhost:8095/fi/unsubscribe");

    }

    @Test
    public void vevs_and_followers_get_email_half_way_between_if_the_initiative_is_still_running_static_test(){

        LocalDate startDate = new LocalDate(2015, 12, 1);
        LocalDate endDate = new LocalDate(2016, 6, 1);
        LocalDate notificationDate = new LocalDate(2016, 3, 1);

        final Long initiative = testHelper.create(
                new TestHelper.InitiativeDraft(userId, AUTHOR_EMAIL)
                        .withName("Testialoite")
                        .withState(InitiativeState.ACCEPTED)
                        .isRunning(startDate, endDate)
                        .withSupportCount(5000)
        );

        testHelper.addFollower(initiative, FOLLOWER_EMAIL);

        followService.sendEmailsHalfwayBetweenForStillRunningInitiatives(notificationDate);

        assertSentEmailCount(2);

        assertSentEmail(FOLLOWER_EMAIL, "Kannatusten kerääminen on nyt puolivälissä / Insamling av stödförklaringar är halvvägs",
                "Aloite on kerännyt 5,000 kannatusilmoitusta, joista 5,000 kpl kansalaisaloite.fi-palvelussa ja 0 kpl muilla menetelmillä",
                "Keruuaika päättyy 1.6.2016. Edetäkseen eduskunnan käsittelyyn aloitteen tulee kerätä vähintään 50,000 kannatusilmoitusta kuuden kuukauden aikana.",
                "Sait tämän viestin, koska"
//                "Tämä on " +String.valueOf((int)(5000.0 / 50000 * 100))+ " prosenttia kokonaistavoitteesta"

        );

        assertSentEmail(AUTHOR_EMAIL, "Kannatusten kerääminen on nyt puolivälissä / Insamling av stödförklaringar är halvvägs",
//                "Tämä on " +String.valueOf((int)(5000.0 / 50000 * 100))+ " prosenttia kokonaistavoitteesta"
                "Keruuaika päättyy 1.6.2016."
        );

        clearAllSentEmails();

        assertHalfwayEmailsAreSentOnlyForDate(notificationDate, 2, startDate.minusMonths(1), endDate.plusMonths(1));

    }

    @Test
    public void follower_email_contains_unsubscribe_link() {


        final Long initiative = testHelper.create(
                new TestHelper.InitiativeDraft(userId, AUTHOR_EMAIL)
                        .withState(InitiativeState.ACCEPTED)
                        .withSupportCount(50001)

        );

        String unsubscribeHash = testHelper.addFollower(initiative, FOLLOWER_EMAIL);

        testHelper.createSupport(initiative, LocalDate.now());

        // When sent to VRK: Assert that emails to VRK and follower are sent

        supportVoteService.sendToVRK(initiative);


        Optional<EmailHelper> emailToFollower = getAllSentEmails().stream().filter(a -> a.to.equals(FOLLOWER_EMAIL)).findFirst();
        Optional<EmailHelper> mailToAuthor = getAllSentEmails().stream().filter(a -> a.to.equals(AUTHOR_EMAIL)).findFirst();

        assertThat(emailToFollower.isPresent(), is(true));
        assertThat(mailToAuthor.isPresent(), is(true));

        assertThat(emailToFollower.get().html, containsString("olet tilannut aloitteen sähköposti-ilmoitukset"));
        assertThat(emailToFollower.get().html, containsString("http://localhost:8095/fi/unsubscribe/" + initiative + "/" + unsubscribeHash));
        assertThat(emailToFollower.get().html, containsString("http://localhost:8095/sv/unsubscribe/" + initiative + "/" + unsubscribeHash));

        assertThat(mailToAuthor.get().html, not(containsString("olet tilannut aloitteen sähköposti-ilmoitukset")));
        assertThat(mailToAuthor.get().html, not(containsString("http://localhost:8095/fi/unsubscribe/" + initiative + "/" + unsubscribeHash)));
        assertThat(mailToAuthor.get().html, not(containsString("http://localhost:8095/sv/unsubscribe/" + initiative + "/" + unsubscribeHash)));
    }


    // This tries to be the BindingResult created by spring while validating.
    private static DirectFieldBindingResult emailValidationErrors(FollowInitiativeDto followInitiativeDto) {
        return new DirectFieldBindingResult(followInitiativeDto, "email");
    }



}
