package fi.om.initiative.service;

import com.google.common.collect.Lists;
import fi.om.initiative.conf.IntegrationTestConfiguration;
import fi.om.initiative.dao.InitiativeDao;
import fi.om.initiative.dao.TestHelper;
import fi.om.initiative.dto.InitiativeCountByState;
import fi.om.initiative.dto.InitiativeCountByStateOm;
import fi.om.initiative.dto.InitiativeSettings;
import fi.om.initiative.dto.User;
import fi.om.initiative.dto.initiative.InitiativeInfo;
import fi.om.initiative.dto.initiative.InitiativeState;
import fi.om.initiative.dto.search.InitiativeSearch;
import fi.om.initiative.dto.search.Show;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={IntegrationTestConfiguration.class})
public class InitiativeServiceFindPageTest {

    private InitiativeServiceImpl initiativeService;

    private UserService userServiceFake;

    @Resource
    private InitiativeDao initiativeDao;

    @Resource
    private TestHelper testHelper;
    private Long userId;

    private static DateTime testStartTime = new DateTime();
    private static LocalDate today = testStartTime.toLocalDate();
    private static LocalDate tomorrow = today.plusDays(1);
    private static LocalDate yesterday = today.minusDays(1);
    private static LocalDate day_before_yesterday = yesterday.minusDays(1);
    private static LocalDate far_in_the_future = yesterday.plusYears(1000);

    final InitiativeSettings INITIATIVE_SETTINGS = new InitiativeSettings(0, 50, 0, Days.days(30), null, null, null, Months.months(2), Months.months(1));

    @Before
    public void init() {

        userServiceFake = new UserServiceFake();

        initiativeService = new InitiativeServiceImpl(initiativeDao, userServiceFake, null, null, null, INITIATIVE_SETTINGS, new HashCreator(""));
        testHelper.dbCleanup();
        userId = testHelper.createTestUser();
        testStartTime = testHelper.getDbCurrentTime();
        today = testStartTime.toLocalDate();
        tomorrow = today.plusDays(1);
        yesterday = today.minusDays(1);
        day_before_yesterday = yesterday.minusDays(1);
    }

    // CHECK ALL STATES

    @Test
    public void check_is_shown_draft() {
        testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.DRAFT)
                .isRunning());

        checkIsShownOn(Show.preparation, Show.omAll);

        checkPublicCount(zeroPublicCounts());
        checkOmCount(zeroOmCounts().setPreparation(1));
    }

    @Test
    public void check_is_shown_proposal() {
        testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.PROPOSAL)
                .isRunning());

        checkIsShownOn(Show.preparation, Show.omAll);
        checkPublicCount(zeroPublicCounts());
        checkOmCount(zeroOmCounts().setPreparation(1));
    }

    @Test
    public void check_is_shown_review() {
        testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.REVIEW)
                .isRunning());

        checkIsShownOn(Show.review, Show.omAll);
        checkPublicCount(zeroPublicCounts());
        checkOmCount(zeroOmCounts().setReview(1));
    }

    @Test
    public void check_is_shown_accepted_waiting() {
        testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.ACCEPTED)
                .isRunning(tomorrow, far_in_the_future));

        checkIsShownOn(Show.waiting, Show.all, Show.omAll);
        checkPublicCount(zeroPublicCounts().setWaiting(1));
        checkOmCount(zeroOmCounts().setWaiting(1));
    }

    @Test
    public void check_is_shown_accepted_running() {
        testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.ACCEPTED)
                .isRunning());

        checkIsShownOn(Show.running, Show.all, Show.omAll);
        checkPublicCount(zeroPublicCounts().setRunning(1));
        checkOmCount(zeroOmCounts().setRunning(1));
    }

    @Test
    public void check_is_shown_accepted_ended() {
        testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.ACCEPTED)
                .isEnded());

        checkIsShownOn(Show.ended, Show.all, Show.omAll);
        checkPublicCount(zeroPublicCounts().setEnded(1));
        checkOmCount(zeroOmCounts().setEnded(1));
    }

    @Test
    public void check_is_shown_accepted_ended_because_not_enough_supports() {
        testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.ACCEPTED)
                .isRunning(today.minusDays(35), far_in_the_future)
                .withSupportCount(49));

        checkIsShownOn(Show.ended, Show.all, Show.omAll);
        checkPublicCount(zeroPublicCounts().setEnded(1));
        checkOmCount(zeroOmCounts().setEnded(1));
    }

    @Test
    public void check_is_shown_sent_to_parliament() {
        testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.DONE)
                .isEnded());

        checkIsShownOn(Show.sentToParliament, Show.all, Show.omAll);
        checkPublicCount(zeroPublicCounts().setSentToParliament(1));
        checkOmCount(zeroOmCounts().setSentToParliament(1));
    }

    @Test
    public void check_is_shown_closed_with_om_acceptance() {
        testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.CANCELED)
                .isAcceptedByOm()
                .isEnded());

        checkIsShownOn(Show.canceled, Show.omCanceled, Show.all, Show.omAll);
        checkPublicCount(zeroPublicCounts().setCanceled(1));
        checkOmCount(zeroOmCounts().setOmCanceled(1));
    }

    @Test
    public void check_is_shown_closed_without_om_acceptance() {
        testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.CANCELED)
                .isEnded());

        checkIsShownOn(Show.omCanceled, Show.omAll);
        checkPublicCount(zeroPublicCounts());
        checkOmCount(zeroOmCounts().setOmCanceled(1));
    }

    @Test
    public void check_is_shown_on_close_to_termination() {
        testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.ACCEPTED)
                .withSupportCount(500)
                .isRunning(today.minusMonths(3), today.minusMonths(1)));

        checkIsShownOn(Show.ended, Show.all, Show.closeToTermination, Show.omAll);
        checkPublicCount(zeroPublicCounts().setEnded(1));
        checkOmCount(zeroOmCounts().setCloseToTermination(1).setEnded(1));
    }

    @Test
    public void minSupportCount_restricts_results_if_given() {
        testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.DRAFT)
                .isRunning());

        checkIsShownOn(0, Show.preparation, Show.omAll);
        checkIsShownOn(50);

        checkOmCount(zeroOmCounts().setPreparation(1));
        checkPublicCount(zeroPublicCounts());


    }

    private AtomicInteger currentlyMiningDatabase = new AtomicInteger(0);

    @Test
    @Ignore
    public void efficiency_test() {
        for (int i = 0; i < 500; ++i) {
            testHelper.create(new TestHelper.InitiativeDraft(userId)
                    .withState(InitiativeState.ACCEPTED)
                    .isRunning());

            testHelper.create(new TestHelper.InitiativeDraft(userId)
                    .withState(InitiativeState.ACCEPTED)
                    .isEnded());

            testHelper.create(new TestHelper.InitiativeDraft(userId)
                    .withState(InitiativeState.ACCEPTED)
                    .isRunning(tomorrow, far_in_the_future));

            testHelper.create(new TestHelper.InitiativeDraft(userId)
                    .withState(InitiativeState.ACCEPTED)
                    .isRunning(today.minusDays(35), far_in_the_future)
                    .withSupportCount(49));

            testHelper.create(new TestHelper.InitiativeDraft(userId)
                    .withState(InitiativeState.CANCELED)
                    .isAcceptedByOm()
                    .isEnded());

            testHelper.create(new TestHelper.InitiativeDraft(userId)
                    .withState(InitiativeState.DONE)
                    .isEnded());
        }

        List<Callable<List<InitiativeInfo>>> threads = Lists.newArrayList();

        System.out.println("Beginning");
        for (int i = 0; i < 1000; ++i) {
            threads.add(new Callable<List<InitiativeInfo>>() {
                @Override
                public List<InitiativeInfo> call() throws Exception {
                    currentlyMiningDatabase.incrementAndGet();
                     initiativeService.getPublicInitiativeCountByState();
                    List<InitiativeInfo> initiatives = initiativeService.findInitiatives(new InitiativeSearch().setShow(Show.running)).list;
                    System.out.println(currentlyMiningDatabase);
                    currentlyMiningDatabase.decrementAndGet();

                    return initiatives;
                }
            });
        }


        ExecutorService executor = Executors.newCachedThreadPool();

        long l = System.currentTimeMillis();
        try {
            for (Future<List<InitiativeInfo>> future : executor.invokeAll(threads)) {
                future.get();

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdownNow();
        }

        System.out.println("Duration in ms: " + (System.currentTimeMillis() - l));


    }

    private void checkIsShownOn(Show... givenPages) {
        checkIsShownOn(null, givenPages);
    }
    private void checkIsShownOn(Integer minSupportCount, Show... givenPages) {

        InitiativeSearch search = new InitiativeSearch();
        search.setMinSupportCount(0);
        if (minSupportCount != null) {
            search.setMinSupportCount(minSupportCount);
        }


        for (Show show : Show.values()) {
            search.setShow(show);
            List<InitiativeInfo> initiatives = initiativeService.findInitiatives(search).list;

            if (givenPages.length == 0) {
                assertThat("Initiative was shown on page: " + show, initiatives.size(), is(0));
            }

            else {
                if (isInGivenPages(show, givenPages)) {
                    assertThat("Initiative was not shown on page: " + show, initiatives.size(), is(1));
                }
                else {
                    assertThat("Initiative was shown on page: " + show, initiatives.size(), is(0));
                }

            }
        }
    }

    private boolean isInGivenPages(Show page, Show[] pages) {
        for (Show i : pages) {
            if (i == page)
                return true;
        }
        return false;
    }

    private void checkPublicCount(InitiativeCountByState expectedCount) {
        assertThat("Counts were incorrect", ReflectionToStringBuilder.reflectionToString(initiativeService.getPublicInitiativeCountByState(), ToStringStyle.SHORT_PREFIX_STYLE),
                is(ReflectionToStringBuilder.reflectionToString(expectedCount, ToStringStyle.SHORT_PREFIX_STYLE)));
    }

    private void checkOmCount(InitiativeCountByStateOm expectedCount) {
        assertThat("Counts were incorrect", ReflectionToStringBuilder.reflectionToString(initiativeService.getOmInitiativeCountByState(), ToStringStyle.SHORT_PREFIX_STYLE),
                is(ReflectionToStringBuilder.reflectionToString(expectedCount, ToStringStyle.SHORT_PREFIX_STYLE)));
    }


    private InitiativeCountByState zeroPublicCounts() {
        return new InitiativeCountByState();
    }

    private InitiativeCountByStateOm zeroOmCounts() {
        return new InitiativeCountByStateOm();
    }

    private class UserServiceFake implements UserService{

        @Override
        public User getCurrentUser() {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public User getCurrentUser(boolean verifyCSRF) {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public User currentAsRegisteredUser() {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public User getUserInRole(Role... roles) {
            return new User(userId, null, null, null, null, false, false);
        }

        @Override
        public void requireUserInRole(Role... roles) {
            throw new RuntimeException("Not implemented");
        }
    }

}
