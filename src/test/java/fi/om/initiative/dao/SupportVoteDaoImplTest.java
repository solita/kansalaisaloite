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
import java.util.HashMap;
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
    public void save_and_get_denormalized_support_count_json_data() {
        Long initiativeId = testHelper.create(new TestHelper.InitiativeDraft(userId));

        String denormalizedData = "some denormalized data";

        supportVoteDao.saveDenormalizedSupportCountDataJson(initiativeId, denormalizedData);

        assertThat(supportVoteDao.getDenormalizedSupportCountDataJson(initiativeId), is(denormalizedData));
    }

    @Test
    public void get_initiative_ids_for_support_vote_denormalization_returns_running_initiatives() {
        Long runningTillToday = testHelper.create(new TestHelper.InitiativeDraft(userId)
                .isRunning(yesterday, today)
                .withState(InitiativeState.ACCEPTED));
        supportVoteDao.saveDenormalizedSupportCountDataJson(runningTillToday, "some previously saved supportvotedata");

        Long reviewTillToday = testHelper.create(new TestHelper.InitiativeDraft(userId)
                .isRunning(yesterday, today)
                .withState(InitiativeState.REVIEW));

        List<Long> idsForRunningTillYesterday = supportVoteDao.getInitiativeIdsForSupportVoteDenormalization(yesterday);
        assertThat(idsForRunningTillYesterday.size(), is(1));
        assertThat(idsForRunningTillYesterday.get(0), is(runningTillToday));

        List<Long> idsForRunningTillToday = supportVoteDao.getInitiativeIdsForSupportVoteDenormalization(today);
        assertThat(idsForRunningTillToday.get(0), is(runningTillToday));

        assertThat(supportVoteDao.getInitiativeIdsForSupportVoteDenormalization(tomorrow).size(), is(0));
    }

    @Test
    public void get_initiative_ids_for_support_vote_denormalization_returns_initiatives_with_empty_supportCountData() {
        Long endedInitiative = testHelper.create(new TestHelper.InitiativeDraft(userId)
                .isRunning(twoDaysAgo, twoDaysAgo)
                .withState(InitiativeState.DONE));

        assertThat(supportVoteDao.getInitiativeIdsForSupportVoteDenormalization(tomorrow).size(), is(1));

        supportVoteDao.saveDenormalizedSupportCountDataJson(endedInitiative, "some data");

        assertThat(supportVoteDao.getInitiativeIdsForSupportVoteDenormalization(tomorrow).size(), is(0));
    }

    @Test
    public void rewrite_and_get_denormalized_support_count_data() {


        Long initiativeId = testHelper.createRunningPublicInitiative(userId, "Initiative name");

        Map<LocalDate, Long> supportCounts = new HashMap<>();
        supportCounts.put(new LocalDate(2010, 1, 1), 10L);
        supportCounts.put(new LocalDate(2010, 1, 2), 15L);
        supportVoteDao.saveDenormalizedSupportCountData(initiativeId, supportCounts);

        Map<LocalDate, Integer> denormalizedSupportCountData = supportVoteDao.getDenormalizedSupportCountData(initiativeId);
        assertThat(denormalizedSupportCountData.size(), is(2));
        assertThat(denormalizedSupportCountData.get(new LocalDate(2010, 1, 1)), is(10));
        assertThat(denormalizedSupportCountData.get(new LocalDate(2010, 1, 2)), is(15));

        supportCounts.put(new LocalDate(2010, 1, 3), 20L);
        supportVoteDao.saveDenormalizedSupportCountData(initiativeId, supportCounts);

        denormalizedSupportCountData = supportVoteDao.getDenormalizedSupportCountData(initiativeId);
        assertThat(denormalizedSupportCountData.size(), is(3));
        assertThat(denormalizedSupportCountData.get(new LocalDate(2010, 1, 1)), is(10));
        assertThat(denormalizedSupportCountData.get(new LocalDate(2010, 1, 2)), is(15));
        assertThat(denormalizedSupportCountData.get(new LocalDate(2010, 1, 3)), is(20));


    }
}
