package fi.om.initiative.service;


import fi.om.initiative.conf.IntegrationTestConfiguration;
import fi.om.initiative.dao.TestHelper;
import fi.om.initiative.dto.initiative.InitiativeManagement;
import fi.om.initiative.dto.initiative.InitiativeState;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.MapBindingResult;

import javax.annotation.Resource;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={IntegrationTestConfiguration.class})
public class FollowServiceTest extends ServiceTestBase {

    @Resource
    private TestHelper testHelper;

    private Long userId;

    @Mocked
    FollowService followService;

    @Mocked
    InitiativeService initiativeService;

    @Mocked
    SupportVoteService supportVoteService;

    @Mocked
    EmailService emailService;

    @Before
    public void init() {
        testHelper.dbCleanup();
        userId = testHelper.createTestUser();
    }

    @Test
    @Ignore
    public void get_initiatives_that_have_ended_yesterday(){

        final Long initiativeEndedYesterDay = testHelper.create(
                new TestHelper.InitiativeDraft(userId)
                        .withState(InitiativeState.ACCEPTED)
                        .isRunning(testHelper.getDbCurrentTime().toLocalDate().minusMonths(6).minusDays(1), testHelper.getDbCurrentTime().toLocalDate().minusDays(1))

        );
        final Long initiativeEndedTwoDaysAgo = testHelper.create(
                new TestHelper.InitiativeDraft(userId)
                        .withState(InitiativeState.ACCEPTED)
                        .isRunning(testHelper.getDbCurrentTime().toLocalDate().minusMonths(6).minusDays(2), testHelper.getDbCurrentTime().toLocalDate().minusDays(2))

        );
        final Long initiativehasNotEnded = testHelper.create(
                new TestHelper.InitiativeDraft(userId)
                        .withState(InitiativeState.ACCEPTED)
                        .isRunning()

        );

        assertThat(followService.getInitiativesThatEndedYesterday().size(), is(1));
        assertThat(followService.getInitiativesThatEndedYesterday().get(0).getId(), is(initiativeEndedYesterDay));

    }


    @Test
    @Ignore
    public void send_email_to_followers_when_initiative_goes_to_VRK(){
        final Long initiative = testHelper.create(
                new TestHelper.InitiativeDraft(userId)
                        .withState(InitiativeState.ACCEPTED)

        );

        new Expectations() {{
            emailService.sendFollowersNotificationAboutVRK(new InitiativeManagement(initiative));
        }};

        supportVoteService.sendToVRK(initiative);
    }

    @Test
    @Ignore
    public void send_email_to_followers_when_initiative_goes_to_Parliament(){
        final Long initiative = testHelper.create(
                new TestHelper.InitiativeDraft(userId)
                        .withState(InitiativeState.ACCEPTED)

        );

        new Expectations() {{
            emailService.sendFollowersNotificationAboutParliament(new InitiativeManagement(initiative));
        }};

        initiativeService.updateSendToParliament(new InitiativeManagement(initiative), new MapBindingResult(new HashMap<Object, Object>(), ""));
    }
}
