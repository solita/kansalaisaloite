package fi.om.initiative.service;

import fi.om.initiative.conf.IntegrationTestConfiguration;
import fi.om.initiative.dao.SupportVoteDao;
import fi.om.initiative.dao.TestHelper;
import fi.om.initiative.dto.initiative.InitiativeState;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static fi.om.initiative.dao.TestHelper.InitiativeDraft.DEFAULT_DENORMALIZED_SUPPORTCOUNT_DATA;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={IntegrationTestConfiguration.class})
public class JobExecutorTest {

    @Resource
    private JobExecutor jobExecutor;

    @Resource
    private SupportVoteDao supportVoteDao;

    @Resource
    private TestHelper testHelper;

    private Long testUserId;

    private final LocalDate today = new LocalDate();
    private final LocalDate longTimeAgo = today.minusYears(1);
    private final LocalDate yesterday = today.minusDays(1);
    private final LocalDate twoDaysAgo = today.minusDays(2);
    private final LocalDate tomorrow = today.plusDays(1);

    @Before
    public void setup() {
        testHelper.dbCleanup();
        testUserId = testHelper.createTestUser();
    }

    @Test
    public void stores_denormalized_support_counts_to_initiatives() {

        Long testInitiative = testHelper.createRunningPublicInitiative(testUserId, "Some initiative");

        LocalDate longTimeAgo = new LocalDate(2000, 1, 15);
        testHelper.createSupport(testInitiative, longTimeAgo);
        testHelper.createSupport(testInitiative, longTimeAgo);
        testHelper.createSupport(testInitiative, longTimeAgo.minusDays(2));
        testHelper.createSupport(testInitiative, longTimeAgo.minusDays(1));

        jobExecutor.updateDenormalizedSupportCountForInitiatives();

        assertThat(supportVoteDao.getDenormalizedSupportCountDataJson(testInitiative), is(
                "[{\"d\":\"2000-01-13\",\"n\":1},{\"d\":\"2000-01-14\",\"n\":1},{\"d\":\"2000-01-15\",\"n\":2}]"
        ));

        assertThat(supportVoteDao.getDenormalizedSupportCountData(testInitiative).size(), is(3));

    }

    @Test
    public void updates_running_initiative() {
        Long runningInitiative = testHelper.create(new TestHelper.InitiativeDraft(testUserId)
                .isRunning(twoDaysAgo, tomorrow)
                .withState(InitiativeState.ACCEPTED)
                .withRandomDenormalizedSupportCount());

        assertThat(supportVoteDao.getDenormalizedSupportCountDataJson(runningInitiative), is(DEFAULT_DENORMALIZED_SUPPORTCOUNT_DATA));
        jobExecutor.updateDenormalizedSupportCountForInitiatives();
        assertThat(supportVoteDao.getDenormalizedSupportCountDataJson(runningInitiative), is("[]"));
    }

    @Test
    public void updates_initiatives_ended_yesterday() {
        Long endedYesterday = testHelper.create(new TestHelper.InitiativeDraft(testUserId)
                .isRunning(twoDaysAgo, yesterday)
                .withState(InitiativeState.ACCEPTED)
                .withRandomDenormalizedSupportCount());

        assertThat(supportVoteDao.getDenormalizedSupportCountDataJson(endedYesterday), is(DEFAULT_DENORMALIZED_SUPPORTCOUNT_DATA));
        jobExecutor.updateDenormalizedSupportCountForInitiatives();
        assertThat(supportVoteDao.getDenormalizedSupportCountDataJson(endedYesterday), is("[]"));
    }

    @Test
    public void does_not_update_initiatives_ended_before_yesterday() {

        Long endedTwoDaysAgo = testHelper.create(new TestHelper.InitiativeDraft(testUserId)
                .isRunning(longTimeAgo, twoDaysAgo)
                .withState(InitiativeState.ACCEPTED)
                .withRandomDenormalizedSupportCount());            Fiu

        assertThat(supportVoteDao.getDenormalizedSupportCountDataJson(endedTwoDaysAgo), is(DEFAULT_DENORMALIZED_SUPPORTCOUNT_DATA));
        jobExecutor.updateDenormalizedSupportCountForInitiatives();
        assertThat(supportVoteDao.getDenormalizedSupportCountDataJson(endedTwoDaysAgo), is(DEFAULT_DENORMALIZED_SUPPORTCOUNT_DATA));
    }

}
