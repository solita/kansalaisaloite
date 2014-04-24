package fi.om.initiative.dao;

import fi.om.initiative.conf.IntegrationTestConfiguration;
import fi.om.initiative.dto.initiative.InitiativeState;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={IntegrationTestConfiguration.class})
public class SupportVoteDaoImplTest {

    @Resource
    SupportVoteDao supportVoteDao;

    @Resource
    TestHelper testHelper;

    private Long userId;
    private LocalDate today;
    private LocalDate yesterday;
    private LocalDate twoDaysAgo;
    private LocalDate tomorrow;

    @Before
    public void setup() {
        testHelper.dbCleanup();
        userId = testHelper.createTestUser();
        //NOTE: testStartTime should use db server time so that comparisons to trigger updated fields don't fail
        today = testHelper.getDbCurrentTime().toLocalDate();
        yesterday = today.minusDays(1);
        twoDaysAgo = today.minusDays(2);
        tomorrow = today.plusDays(1);
    }

    @Test
    public void counts_support_vote_amounts_per_day() {

        Long initiativeId = testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.ACCEPTED)
                .isRunning(twoDaysAgo, tomorrow));

        testHelper.createSupport(initiativeId, twoDaysAgo);
        testHelper.createSupport(initiativeId, twoDaysAgo);
        testHelper.createSupport(initiativeId, twoDaysAgo);
        testHelper.createSupport(initiativeId, yesterday);
        testHelper.createSupport(initiativeId, yesterday);
        testHelper.createSupport(initiativeId, today);

        Map<LocalDate, Long> supportVoteCountByDate = supportVoteDao.getSupportVoteCountByDateUntil(initiativeId, yesterday);
        assertThat(supportVoteCountByDate.size(), is(2));
        assertThat(supportVoteCountByDate.get(twoDaysAgo), is(3L));
        assertThat(supportVoteCountByDate.get(yesterday), is(2L));
        assertThat(supportVoteCountByDate.get(today), is(nullValue()));

        supportVoteCountByDate = supportVoteDao.getSupportVoteCountByDateUntil(initiativeId, today);
        assertThat(supportVoteCountByDate.size(), is(3));
        assertThat(supportVoteCountByDate.get(twoDaysAgo), is(3L));
        assertThat(supportVoteCountByDate.get(yesterday), is(2L));
        assertThat(supportVoteCountByDate.get(today), is(1L));

        assertThat(supportVoteDao.getSupportVoteCountByDateUntil(initiativeId, twoDaysAgo.minusDays(1)).size(), is(0));
    }

    @Test
    public void save_and_get_denormalized_support_count_data() {
        Long initiativeId = testHelper.create(new TestHelper.InitiativeDraft(userId));

        String denormalizedData = "some denormalized data";

        supportVoteDao.saveDenormalizedSupportCountData(initiativeId, denormalizedData);

        assertThat(supportVoteDao.getDernormalizedSupportCountData(initiativeId), is(denormalizedData));
    }

    @Test
    public void get_running_initiatives_returns_initiatives_till_date() {
        Long runningTillToday = testHelper.create(new TestHelper.InitiativeDraft(userId)
                .isRunning(yesterday, today)
                .withState(InitiativeState.ACCEPTED));

        Long reviewTillToday = testHelper.create(new TestHelper.InitiativeDraft(userId)
                .isRunning(yesterday, today)
                .withState(InitiativeState.REVIEW));

        List<Long> idsForRunningTillYesterday = supportVoteDao.getInitiativeIdsForRunningInitiatives(yesterday);
        assertThat(idsForRunningTillYesterday.size(), is(1));
        assertThat(idsForRunningTillYesterday.get(0), is(runningTillToday));

        List<Long> idsForRunningTillToday = supportVoteDao.getInitiativeIdsForRunningInitiatives(today);
        assertThat(idsForRunningTillToday.get(0), is(runningTillToday));

        assertThat(supportVoteDao.getInitiativeIdsForRunningInitiatives(tomorrow).size(), is(0));
    }
}
