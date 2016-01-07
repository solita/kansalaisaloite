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
import org.springframework.validation.Errors;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class EmailsTest extends EmailSpyConfiguration {

    @Resource
    private TestHelper testHelper;

    private Long userId;

    @Mocked
    HttpUserServiceImpl userService;

    @Resource
    private SupportVoteService supportVoteService;

    @Resource
    private InitiativeService initiativeService;

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
    public void send_initiative_to_vrk_sends_email_to_vrk() throws InterruptedException {

        final Long initiative = testHelper.create(
                new TestHelper.InitiativeDraft(userId)
                        .withState(InitiativeState.ACCEPTED)
                        .withSupportCount(50001)

        );

        // There must be at least one support to avoid EmptyBatchException. It's ok to throw it, because in production its really a problem if batch is empty.
        testHelper.createSupport(initiative, LocalDate.now());

        supportVoteService.sendToVRK(initiative);

        assertSentEmailCount(1);
        assertSentEmail("kansalaisaloite.tarkastus@vrk.fi", "Kannatusilmoitusten määrän vahvistuspyyntö / Ansökan om bekräftelse av antalet stödförklaringar");
    }

    @Test
    public void send_initiative_to_vrk_sends_emails_to_followers() throws IOException, MessagingException {

        final Long initiative = testHelper.create(
                new TestHelper.InitiativeDraft(userId)
                        .withState(InitiativeState.ACCEPTED)
                        .withSupportCount(50001)

        );

        testHelper.createSupport(initiative, LocalDate.now());
        testHelper.addFollower(initiative, "follower@example.com");

        supportVoteService.sendToVRK(initiative);

        assertSentEmailCount(2);
        assertSentEmail("kansalaisaloite.tarkastus@vrk.fi", "Kannatusilmoitusten määrän vahvistuspyyntö / Ansökan om bekräftelse av antalet stödförklaringar");
        assertSentEmail("follower@example.com", "Pyyntö kannatusilmoitusten tarkastamisesta on lähetetty Väestörekisterikeskukseen / Begäran om att kontrollera stödförklaringarna har skickats till Befolkningsregistercentralen");
    }

    @Test
    public void vrk_accept_supports_sents_email_to_followers() {

        final Long initiative = testHelper.create(
                new TestHelper.InitiativeDraft(userId)
                        .withState(InitiativeState.ACCEPTED)
                        .withSupportCount(50001)

        );

        testHelper.createSupport(initiative, LocalDate.now());
        supportVoteService.sendToVRK(initiative);
        clearAllSentEmails();

        // Action and assertion

        testHelper.addFollower(initiative, "follower@example.com");

        InitiativeManagement vrkData = new InitiativeManagement(initiative);
        // = initiativeService.getInitiativeForManagement(initiative);

        vrkData.setVerificationIdentifier("lol");
        vrkData.setVerified(LocalDate.now());
        vrkData.setVerifiedSupportCount(10);

        assertTrue(initiativeService.updateVRKResolution(vrkData, mock(Errors.class)));

        assertSentEmailCount(1);
        assertSentEmail("follower@example.com", "Väestörekisterikeskus on vahvistanut kannatusilmoitusten määrän / Befolkningsregistercentralen har bekräftat antalet stödförklaringar");


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

}
