package fi.om.initiative.service;


import fi.om.initiative.conf.IntegrationTestConfiguration;
import fi.om.initiative.dao.TestHelper;
import fi.om.initiative.dto.initiative.InitiativeState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={IntegrationTestConfiguration.class})
public class FollowServiceTest extends ServiceTestBase {

    @Resource
    private TestHelper testHelper;

    private Long userId;

    @Resource
    FollowService followService;

    @Before
    public void init() {
        testHelper.dbCleanup();
        userId = testHelper.createTestUser();
    }

    @Test
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
    public void send_email_to_followers_when_initiative_goes_to_VRK(){

    }

    @Test
    public void send_email_to_followers_when_initiative_goes_to_Parliament(){

    }
}
