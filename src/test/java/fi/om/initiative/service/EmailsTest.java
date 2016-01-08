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
    public void emails_after_publication_are_sent() {

        String followerEmail = "follower@example.com";
        String authorEmail = "author@example.com";

        final Long initiative = testHelper.create(
                new TestHelper.InitiativeDraft(userId, authorEmail)
                        .withState(InitiativeState.ACCEPTED)
                        .withSupportCount(50001)

        );

        testHelper.createSupport(initiative, LocalDate.now());
        testHelper.addFollower(initiative, followerEmail);

        // When sent to VRK: Assert that emails to VRK and follower are sent

        supportVoteService.sendToVRK(initiative);

        assertSentEmailCount(3);
        assertSentEmail("kansalaisaloite.tarkastus@vrk.fi", "Kannatusilmoitusten määrän vahvistuspyyntö / Ansökan om bekräftelse av antalet stödförklaringar");
        assertSentEmail(followerEmail, "Pyyntö kannatusilmoitusten tarkastamisesta on lähetetty Väestörekisterikeskukseen / Begäran om att kontrollera stödförklaringarna har skickats till Befolkningsregistercentralen");
        assertSentEmail(authorEmail, "Kannatusilmoitusten tarkastuspyyntö on lähetetty Väestörekisterikeskukseen / Ansökan om att granska stödförklaringarna har skickats till Befolkningsregistercentralen");
        clearAllSentEmails();

        // When accepted by VRK: Assert that emails for follower are sent

        InitiativeManagement vrkData = new InitiativeManagement(initiative);

        vrkData.setVerificationIdentifier("lol");
        vrkData.setVerified(LocalDate.now());
        vrkData.setVerifiedSupportCount(51000);

        assertTrue(initiativeService.updateVRKResolution(vrkData, mock(Errors.class)));

        assertSentEmailCount(2);
        assertSentEmail(followerEmail, "Väestörekisterikeskus on vahvistanut kannatusilmoitusten määrän / Befolkningsregistercentralen har bekräftat antalet stödförklaringar");
        assertSentEmail(authorEmail, "Väestörekisterikeskus on vahvistanut kannatusilmoitusten määrän / Befolkningsregistercentralen har bekräftat antalet stödförklaringar");

        clearAllSentEmails();

        // When marking as sent to parliament, assert that emails are sent.

        InitiativeManagement omToParliament = new InitiativeManagement(initiative);
        omToParliament.setParliamentURL("url");
        omToParliament.setParliamentIdentifier("identifier");
        omToParliament.setParliamentSentTime(LocalDate.now());

        initiativeService.updateSendToParliament(omToParliament, mock(BindingResult.class));

//        assertSentEmailCount(2);
//        assertSentEmail(followerEmail, "Väestörekisterikeskus on vahvistanut kannatusilmoitusten määrän / Befolkningsregistercentralen har bekräftat antalet stödförklaringar");
//        assertSentEmail(authorEmail, "Väestörekisterikeskus on vahvistanut kannatusilmoitusten määrän / Befolkningsregistercentralen har bekräftat antalet stödförklaringar");

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
