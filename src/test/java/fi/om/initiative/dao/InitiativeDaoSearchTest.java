package fi.om.initiative.dao;

import com.google.common.collect.Lists;
import fi.om.initiative.dto.InitURI;
import fi.om.initiative.dto.InitiativeSettings;
import fi.om.initiative.dto.LanguageCode;
import fi.om.initiative.dto.ProposalType;
import fi.om.initiative.dto.author.Author;
import fi.om.initiative.dto.author.ContactInfo;
import fi.om.initiative.dto.initiative.InitiativeInfo;
import fi.om.initiative.dto.initiative.InitiativeManagement;
import fi.om.initiative.dto.initiative.InitiativeState;
import fi.om.initiative.dto.initiative.Link;
import fi.om.initiative.dto.search.InitiativeSearch;
import fi.om.initiative.dto.search.OrderBy;
import fi.om.initiative.dto.search.SearchView;
import fi.om.initiative.dto.search.Show;
import fi.om.initiative.service.EmailSpyConfiguration;
import fi.om.initiative.util.OptionalHashMap;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

import static fi.om.initiative.util.Locales.asLocalizedString;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Note that some of these tests are not necessary because of
 * fi.om.initiative.service.InitiativeServiceFindPageTest
 */

public class InitiativeDaoSearchTest extends EmailSpyConfiguration {

    @Resource
    private InitiativeDao initiativeDao;

    private Long userId;
    private Long anotherUserId;

    private static LocalDate DOB = new LocalDate().minusYears(20);

    // NOTE: Tests are fragile if system localdate and database localdate are not in sync.
    private static DateTime testStartTime = new DateTime(); // XXX: Is re-initialized by init() - function.

    private static LocalDate today = testStartTime.toLocalDate();
    private static LocalDate tomorrow = today.plusDays(1);
    private static LocalDate yesterday = today.minusDays(1);
    private static LocalDate day_before_yesterday = yesterday.minusDays(1);
    private static LocalDate far_in_the_future = yesterday.plusYears(1000);

    private static Integer stringChangerIndex = 0;
    private static InitiativeSettings.MinSupportCountSettings neverClosedBecauseOfMinSupportCount = new InitiativeSettings.MinSupportCountSettings(0, Days.days(0));

    @Resource
    private TestHelper testHelper;

    @Before
    public void init() {
        testHelper.dbCleanup();
        userId = testHelper.createTestUser();
        anotherUserId = testHelper.createTestUser();
        //NOTE: testStartTime should use db server time so that comparisons to trigger updated fields don't fail
        testStartTime = testHelper.getDbCurrentTime();
        today = testStartTime.toLocalDate();
        tomorrow = today.plusDays(1);
        yesterday = today.minusDays(1);
        day_before_yesterday = yesterday.minusDays(1);
    }

    @Test
    public void find_by_public_initiatives() {
        Long notPublicInitiative = testHelper.create(
                new TestHelper.InitiativeDraft(userId)
                        .withState(InitiativeState.REVIEW)
        );

        Long publicInitiative = testHelper.create(
                new TestHelper.InitiativeDraft(userId)
                        .withState(InitiativeState.ACCEPTED)
        );

        List<InitiativeInfo> publicInitiatives = initiativeDao.findInitiatives(initiativeSearch(), userId, neverClosedBecauseOfMinSupportCount).list;

        assertThat(publicInitiatives.size(), is(1));
        assertThat(publicInitiatives.get(0).getId(), is(publicInitiative));

    }

    private InitiativeSearch initiativeSearch() {
        return new InitiativeSearch().setMinSupportCount(0);
    }

    @Test
    public void find_by_own_initiatives() {
        Long notPublicOwnInitiative = testHelper.create(
                new TestHelper.InitiativeDraft(userId)
                        .withState(InitiativeState.REVIEW)
        );

        Long publicOwnInitiative = testHelper.create(
                new TestHelper.InitiativeDraft(userId)
                        .withState(InitiativeState.ACCEPTED)
        );

        Long notPublicNotOwnInitiative = testHelper.create(
                new TestHelper.InitiativeDraft(anotherUserId)
                        .withState(InitiativeState.REVIEW)
        );

        InitiativeSearch search = initiativeSearch().setSearchView(SearchView.own);
        search.setOrderBy(OrderBy.id);

        List<InitiativeInfo> publicInitiatives = initiativeDao.findInitiatives(search, userId, neverClosedBecauseOfMinSupportCount).list;

        assertThat(publicInitiatives.size(), is(2));
        assertThat(publicInitiatives.get(0).getId(), is(publicOwnInitiative));
        assertThat(publicInitiatives.get(1).getId(), is(notPublicOwnInitiative));

    }

    @Test
    public void find_with_offset_given() {

        int count = 5;

        List<Long> ids = Lists.newArrayList();
        for (int i = 0; i < count; ++i) {
            Long id = createNotEndedInitiativeWithState(InitiativeState.ACCEPTED);
            ids.add(id);
        }
        ids = Lists.reverse(ids);

        InitiativeSearch search = initiativeSearch();
        search.setOffset(1);
        search.setOrderBy(OrderBy.id);

        List<InitiativeInfo> result = initiativeDao.findInitiatives(search, null, neverClosedBecauseOfMinSupportCount).list;

        assertThat(result.get(0).getId(), is(ids.get(1)));
    }

    @Test
    public void find_with_limit_given() {

        int count = 5;

        List<Long> ids = Lists.newArrayList();
        for (int i = 0; i < count; ++i) {
            Long id = createNotEndedInitiativeWithState(InitiativeState.ACCEPTED);
            ids.add(id);
        }
        ids = Lists.reverse(ids);

        InitiativeSearch search = initiativeSearch();
        search.setLimit(3);
        search.setOrderBy(OrderBy.id);

        List<InitiativeInfo> result = initiativeDao.findInitiatives(search, null, neverClosedBecauseOfMinSupportCount).list;

        assertThat(result.size(), is(3));
        assertThat(result.get(0).getId(), is(ids.get(0)));
        assertThat(result.get(2).getId(), is(ids.get(2)));
    }

    @Test
    public void find_with_restrict_given() {

        int count = 5;

        List<Long> ids = Lists.newArrayList();
        for (int i = 0; i < count; ++i) {
            Long id = createNotEndedInitiativeWithState(InitiativeState.ACCEPTED);
            ids.add(id);
        }
        ids = Lists.reverse(ids);

        InitiativeSearch search = initiativeSearch();
        search.setRestrict(1, 3);
        search.setOrderBy(OrderBy.id);

        List<InitiativeInfo> result = initiativeDao.findInitiatives(search, null, neverClosedBecauseOfMinSupportCount).list;

        assertThat(result.size(), is(3));
        assertThat(result.get(0).getId(), is(ids.get(1)));
        assertThat(result.get(2).getId(), is(ids.get(3)));
    }

    @Test
    public void find_with_show_only_running_shows_only_accepted_and_not_ended() {
        Long acceptedNotEndedInitiative = createNotEndedInitiativeWithState(InitiativeState.ACCEPTED);
        createNotEndedInitiativeWithState(InitiativeState.DONE);
        createNotEndedInitiativeWithState(InitiativeState.REVIEW);
        createNotStartedinitiativeWithState(InitiativeState.ACCEPTED);
        createEndedInitiativeWithState(InitiativeState.ACCEPTED);

        InitiativeSearch search = initiativeSearch();
        search.setShow(Show.running);

        List<InitiativeInfo> result = initiativeDao.findInitiatives(search, userId, neverClosedBecauseOfMinSupportCount).list;
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getId(), is(acceptedNotEndedInitiative));
    }

    @Test
    public void find_with_show_only_running_does_not_show_if_not_enough_supports_and_minSupportCountDuration_has_ended() {
        testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.ACCEPTED)
                .withSupportCount(49)
                .isRunning(day_before_yesterday, far_in_the_future));

        Long hasEnoughtSupports = testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.ACCEPTED)
                .withSupportCount(50)
                .isRunning(day_before_yesterday, far_in_the_future));

        InitiativeSearch search = initiativeSearch();
        search.setShow(Show.running);

        InitiativeSettings.MinSupportCountSettings supportCountSettings
                = new InitiativeSettings.MinSupportCountSettings(50, Days.days(1)); // Ended yesterday
        List<InitiativeInfo> result = initiativeDao.findInitiatives(search, userId, supportCountSettings).list;

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getId(), is(hasEnoughtSupports));
    }

    @Test
    public void find_with_show_only_running_shows_if_not_enough_supports_and_if_time_has_not_ended_inclusive() {
        testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.ACCEPTED)
                .withSupportCount(49)
                .isRunning(day_before_yesterday, far_in_the_future));

        testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.ACCEPTED)
                .withSupportCount(50)
                .isRunning(day_before_yesterday, far_in_the_future));

        InitiativeSearch search = initiativeSearch();
        search.setShow(Show.running);

        InitiativeSettings.MinSupportCountSettings supportCountSettings
                = new InitiativeSettings.MinSupportCountSettings(50, Days.days(2)); // Ending today, is inclusive
        List<InitiativeInfo> result = initiativeDao.findInitiatives(search, userId, supportCountSettings).list;

        assertThat(result.size(), is(2));
    }

    @Test
    public void find_with_show_only_ended_shows_only_accepted_initiatives_that_have_ended() {
        createNotEndedInitiativeWithState(InitiativeState.ACCEPTED);
        createNotEndedInitiativeWithState(InitiativeState.DONE);
        createNotEndedInitiativeWithState(InitiativeState.CANCELED);
        Long endedInitiative = createEndedInitiativeWithState(InitiativeState.ACCEPTED);

        InitiativeSearch search = initiativeSearch();
        search.setShow(Show.ended);

        List<InitiativeInfo> result = initiativeDao.findInitiatives(search, userId, neverClosedBecauseOfMinSupportCount).list;
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getId(), is(endedInitiative));
    }

    @Test
    public void find_with_show_only_ended_shows_if_not_enough_supports_and_minSupportDuration_time_has_ended() {
        Long notEnoughSupportsAndMinSupportTimeEnded = testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.ACCEPTED)
                .withSupportCount(49)
                .isRunning(day_before_yesterday, far_in_the_future));

        testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.ACCEPTED)
                .withSupportCount(50)
                .isRunning(day_before_yesterday, far_in_the_future));

        InitiativeSearch search = initiativeSearch();
        search.setShow(Show.ended);

        InitiativeSettings.MinSupportCountSettings supportCountSettings
                = new InitiativeSettings.MinSupportCountSettings(50, Days.days(1)); // Ended yesterday
        List<InitiativeInfo> result = initiativeDao.findInitiatives(search, userId, supportCountSettings).list;

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getId(), is(notEnoughSupportsAndMinSupportTimeEnded));
    }

    @Test
    public void find_with_show_only_ended_does_not_show_if_not_enough_supports_but_minSupportDuration_time_has_not_ended() {
        testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.ACCEPTED)
                .withSupportCount(49)
                .isRunning(day_before_yesterday, far_in_the_future));

        testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.ACCEPTED)
                .withSupportCount(50)
                .isRunning(day_before_yesterday, far_in_the_future));

        InitiativeSearch search = initiativeSearch();
        search.setShow(Show.ended);

        InitiativeSettings.MinSupportCountSettings supportCountSettings
                = new InitiativeSettings.MinSupportCountSettings(50, Days.days(2)); // Ending today, inclusive
        List<InitiativeInfo> result = initiativeDao.findInitiatives(search, userId, supportCountSettings).list;

        assertThat(result.size(), is(0));
    }

    @Test
    public void find_with_show_only_waiting_shows_only_not_started_initiatives() {

        testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.ACCEPTED)
                .isRunning(today, far_in_the_future));

        Long notYetStarted = testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.ACCEPTED)
                .isRunning(tomorrow, far_in_the_future));

        InitiativeSearch search = initiativeSearch();
        search.setShow(Show.waiting);

        List<InitiativeInfo> result = initiativeDao.findInitiatives(search, userId, neverClosedBecauseOfMinSupportCount).list;

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getId(), is(notYetStarted));

    }

    @Test
    public void find_with_show_only_canceled() {
        createNotEndedInitiativeWithState(InitiativeState.ACCEPTED, true);
        createNotEndedInitiativeWithState(InitiativeState.REVIEW, true);
        createEndedInitiativeWithState(InitiativeState.ACCEPTED, true);
        createNotEndedInitiativeWithState(InitiativeState.CANCELED, false);
        createEndedInitiativeWithState(InitiativeState.CANCELED, false);

        Long confirmedNotEndedCanceled = createNotEndedInitiativeWithState(InitiativeState.CANCELED, true);
        Long confirmedEndedCanceled = createNotEndedInitiativeWithState(InitiativeState.CANCELED, true);

        InitiativeSearch search = initiativeSearch();
        search.setShow(Show.canceled);
        search.setOrderBy(OrderBy.id);

        List<InitiativeInfo> result = initiativeDao.findInitiatives(search, userId, neverClosedBecauseOfMinSupportCount).list;
        assertThat(result.size(), is(2));
        assertThat(result.get(0).getId(), is(confirmedEndedCanceled));
        assertThat(result.get(1).getId(), is(confirmedNotEndedCanceled));
    }

    @Test
    public void find_with_show_only_sentToParliament() {
        createNotEndedInitiativeWithState(InitiativeState.ACCEPTED);
        createNotEndedInitiativeWithState(InitiativeState.REVIEW);
        createEndedInitiativeWithState(InitiativeState.ACCEPTED);

        Long endedInitiative = createNotEndedInitiativeWithState(InitiativeState.DONE);
        Long doneInitiative = createEndedInitiativeWithState(InitiativeState.DONE);

        InitiativeSearch search = initiativeSearch();
        search.setShow(Show.sentToParliament);
        search.setOrderBy(OrderBy.id);

        List<InitiativeInfo> result = initiativeDao.findInitiatives(search, userId, neverClosedBecauseOfMinSupportCount).list;
        assertThat(result.size(), is(2));
        assertThat(result.get(0).getId(), is(doneInitiative));
        assertThat(result.get(1).getId(), is(endedInitiative));
    }

    @Test
    public void find_with_show_all() {
        createNotEndedInitiativeWithState(InitiativeState.ACCEPTED);
        createEndedInitiativeWithState(InitiativeState.ACCEPTED);
        createNotEndedInitiativeWithState(InitiativeState.DONE);
        createNotEndedInitiativeWithState(InitiativeState.CANCELED, true);

        createNotEndedInitiativeWithState(InitiativeState.CANCELED); // Should not be found (not confirmed by om)
        createNotEndedInitiativeWithState(InitiativeState.REVIEW); // Should not be found

        InitiativeSearch search = initiativeSearch();
        search.setShow(Show.all);

        List<InitiativeInfo> result = initiativeDao.findInitiatives(search, userId, neverClosedBecauseOfMinSupportCount).list;
        assertThat(result.size(), is(4));
    }

    @Test
    public void find_all_will_find_if_not_enough_supports_if_om_view() {

        createNotEndedInitiativeWithState(InitiativeState.ACCEPTED);
        createEndedInitiativeWithState(InitiativeState.ACCEPTED);
        createNotEndedInitiativeWithState(InitiativeState.DONE);
        createNotEndedInitiativeWithState(InitiativeState.CANCELED, true);

        InitiativeSearch search = initiativeSearch();
        search.setShow(Show.all);
        search.setSearchView(SearchView.om);
        assertThat(initiativeDao.findInitiatives(search, userId, neverClosedBecauseOfMinSupportCount).list.size(), is(4));
    }

    @Test
    public void orderById() {
        InitiativeManagement firstAdded = initiativeDao.get(createNotEndedInitiativeWithState(InitiativeState.ACCEPTED));
        InitiativeManagement secondAdded = initiativeDao.get(createNotEndedInitiativeWithState(InitiativeState.ACCEPTED));
        InitiativeManagement lastAdded = initiativeDao.get(createNotEndedInitiativeWithState(InitiativeState.ACCEPTED));

        InitiativeSearch initiativeSearch = initiativeSearch();

        initiativeSearch.setOrderBy(OrderBy.id);
        List<InitiativeInfo> listById = initiativeDao.findInitiatives(initiativeSearch, null, neverClosedBecauseOfMinSupportCount).list;
        assertThat(listById.get(0).getId(), is(lastAdded.getId()));
        assertThat(listById.get(1).getId(), is(secondAdded.getId()));
        assertThat(listById.get(2).getId(), is(firstAdded.getId()));
    }

    @Test
    public void order_by_time_left() {

        InitiativeManagement yesterdayInitiative = initiativeDao.get(createNotEndedInitiativeWithState(InitiativeState.ACCEPTED));
        yesterdayInitiative.assignEndDate(yesterday);
        testHelper.updateForTesting(yesterdayInitiative);

        InitiativeManagement todayInitiative = initiativeDao.get(createNotEndedInitiativeWithState(InitiativeState.ACCEPTED));
        todayInitiative.assignEndDate(today);
        testHelper.updateForTesting(todayInitiative);

        InitiativeManagement twoDaysAgoInitiative = initiativeDao.get(createNotEndedInitiativeWithState(InitiativeState.ACCEPTED));
        twoDaysAgoInitiative.assignEndDate(day_before_yesterday);
        testHelper.updateForTesting(twoDaysAgoInitiative);

        InitiativeSearch initiativeSearch = initiativeSearch().setShow(Show.all);

        initiativeSearch.setOrderBy(OrderBy.mostTimeLeft);
        List<InitiativeInfo> listByMostTimeLeft = initiativeDao.findInitiatives(initiativeSearch, null, neverClosedBecauseOfMinSupportCount).list;
        assertThat(listByMostTimeLeft.get(0).getId(), is(todayInitiative.getId()));
        assertThat(listByMostTimeLeft.get(1).getId(), is(yesterdayInitiative.getId()));
        assertThat(listByMostTimeLeft.get(2).getId(), is(twoDaysAgoInitiative.getId()));

        initiativeSearch.setOrderBy(OrderBy.leastTimeLeft);
        List<InitiativeInfo> listByLeastTimeLeft = initiativeDao.findInitiatives(initiativeSearch, null, neverClosedBecauseOfMinSupportCount).list;
        assertThat(listByLeastTimeLeft.get(0).getId(), is(twoDaysAgoInitiative.getId()));
        assertThat(listByLeastTimeLeft.get(1).getId(), is(yesterdayInitiative.getId()));
        assertThat(listByLeastTimeLeft.get(2).getId(), is(todayInitiative.getId()));
    }

    @Test
    public void order_by_start_time() {

        InitiativeManagement yesterdayInitiative = initiativeDao.get(createNotEndedInitiativeWithState(InitiativeState.ACCEPTED));
        yesterdayInitiative.setStartDate(yesterday);
        testHelper.updateForTesting(yesterdayInitiative);

        InitiativeManagement todayInitiative = initiativeDao.get(createNotEndedInitiativeWithState(InitiativeState.ACCEPTED));
        todayInitiative.setStartDate(today);
        testHelper.updateForTesting(todayInitiative);

        InitiativeManagement twoDaysAgoInitiative = initiativeDao.get(createNotEndedInitiativeWithState(InitiativeState.ACCEPTED));
        twoDaysAgoInitiative.setStartDate(day_before_yesterday);
        testHelper.updateForTesting(twoDaysAgoInitiative);

        InitiativeSearch initiativeSearch = initiativeSearch().setShow(Show.all);

        initiativeSearch.setOrderBy(OrderBy.createdNewest);
        List<InitiativeInfo> listByMostTimeLeft = initiativeDao.findInitiatives(initiativeSearch, null, neverClosedBecauseOfMinSupportCount).list;
        assertThat(listByMostTimeLeft.get(0).getId(), is(todayInitiative.getId()));
        assertThat(listByMostTimeLeft.get(1).getId(), is(yesterdayInitiative.getId()));
        assertThat(listByMostTimeLeft.get(2).getId(), is(twoDaysAgoInitiative.getId()));

        initiativeSearch.setOrderBy(OrderBy.createdOldest);
        List<InitiativeInfo> listByLeastTimeLeft = initiativeDao.findInitiatives(initiativeSearch, null, neverClosedBecauseOfMinSupportCount).list;
        assertThat(listByLeastTimeLeft.get(0).getId(), is(twoDaysAgoInitiative.getId()));
        assertThat(listByLeastTimeLeft.get(1).getId(), is(yesterdayInitiative.getId()));
        assertThat(listByLeastTimeLeft.get(2).getId(), is(todayInitiative.getId()));
    }

    @Test
    public void orderBy_supportCount() {
        InitiativeManagement loadsOfSupports = initiativeDao.get(createNotEndedInitiativeWithState(InitiativeState.ACCEPTED));
        loadsOfSupports.assignSupportCount(50);
        loadsOfSupports.setExternalSupportCount(40);
        testHelper.updateForTesting(loadsOfSupports);

        InitiativeManagement leastSupports = initiativeDao.get(createNotEndedInitiativeWithState(InitiativeState.ACCEPTED));
        leastSupports.assignSupportCount(10);
        testHelper.updateForTesting(leastSupports);

        InitiativeManagement someSupports = initiativeDao.get(createNotEndedInitiativeWithState(InitiativeState.ACCEPTED));
        someSupports.assignSupportCount(60);
        testHelper.updateForTesting(someSupports);

        InitiativeSearch initiativeSearch = initiativeSearch();

        initiativeSearch.setOrderBy(OrderBy.mostSupports);
        List<InitiativeInfo> listByMostTimeLeft = initiativeDao.findInitiatives(initiativeSearch, null, neverClosedBecauseOfMinSupportCount).list;
        assertThat(listByMostTimeLeft.get(0).getId(), is(loadsOfSupports.getId()));
        assertThat(listByMostTimeLeft.get(1).getId(), is(someSupports.getId()));
        assertThat(listByMostTimeLeft.get(2).getId(), is(leastSupports.getId()));

        initiativeSearch.setOrderBy(OrderBy.leastSupports);
        List<InitiativeInfo> listByLeastTimeLeft = initiativeDao.findInitiatives(initiativeSearch, null, neverClosedBecauseOfMinSupportCount).list;
        assertThat(listByLeastTimeLeft.get(0).getId(), is(leastSupports.getId()));
        assertThat(listByLeastTimeLeft.get(1).getId(), is(someSupports.getId()));
        assertThat(listByLeastTimeLeft.get(2).getId(), is(loadsOfSupports.getId()));

    }

    @Test
    public void counts_amount_of_initiatives_on_every_state() {
        createNotEndedInitiativeWithState(InitiativeState.DONE);
        createNotEndedInitiativeWithState(InitiativeState.CANCELED, false); // Not accepted by om, not shown
        createNotEndedInitiativeWithState(InitiativeState.CANCELED, true);
        createNotEndedInitiativeWithState(InitiativeState.ACCEPTED);
        createNotEndedInitiativeWithState(InitiativeState.ACCEPTED);
        createEndedInitiativeWithState(InitiativeState.ACCEPTED);

        OptionalHashMap<InitiativeState, Long> result = initiativeDao.getInitiativeCountByState();
        assertThat(result.get(InitiativeState.DONE).get(), is(1L));
        assertThat(result.get(InitiativeState.CANCELED).get(), is(1L));
        assertThat(result.get(InitiativeState.ACCEPTED).get(), is(3L));
        assertThat(result.get(InitiativeState.REVIEW).isPresent(), is(false));
    }

    @Test
    public void returns_only_initiatives_with_given_minsupportcount_if_set() {
        Long enoughSupports = testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.ACCEPTED)
                .withSupportCount(50));

        testHelper.create(new TestHelper.InitiativeDraft(userId)
                .withState(InitiativeState.ACCEPTED)
                .withSupportCount(49));

                InitiativeSearch search = initiativeSearch().setMinSupportCount(50);
        List<InitiativeInfo> initiatives = initiativeDao.findInitiatives(search, userId, neverClosedBecauseOfMinSupportCount).list;

        assertThat(initiatives.size(), is(1));
        assertThat(initiatives.get(0).getId(), is(enoughSupports));

    }

    private Long createNotStartedinitiativeWithState(InitiativeState state) {
        InitiativeManagement initiative = initiativeDao.get(createNotEndedInitiativeWithState(state));
        initiative.setStartDate(tomorrow);
        initiative.assignEndDate(tomorrow);
        testHelper.updateForTesting(initiative);
        return initiative.getId();
    }

    private Long createEndedInitiativeWithState(InitiativeState state, boolean isConfirmedByOm) {
        Long id = createNotEndedInitiativeWithState(state);
        if (isConfirmedByOm) {
            InitiativeManagement initiative = initiativeDao.get(id);
            initiative.setAcceptanceIdentifier("accepted by om");
            testHelper.updateForTesting(initiative);
        }
        return id;
    }
    private Long createEndedInitiativeWithState(InitiativeState state) {
        InitiativeManagement initiative = initiativeDao.get(createNotEndedInitiativeWithState(state));
        initiative.setStartDate(day_before_yesterday);
        initiative.assignEndDate(yesterday);
        testHelper.updateForTesting(initiative);
        return initiative.getId();
    }

    private Long createNotEndedInitiativeWithState(InitiativeState state, boolean isConfirmedByOm) {
        Long id = createNotEndedInitiativeWithState(state);
        if (isConfirmedByOm) {
            InitiativeManagement initiative = initiativeDao.get(id);
            initiative.setAcceptanceIdentifier("accepted by om");
            testHelper.updateForTesting(initiative);
        }
        return id;
    }

    private Long createNotEndedInitiativeWithState(InitiativeState state) {
        Long id = create(createNotEndedInitiative(null), userId);
        initiativeDao.updateInitiativeState(id, userId, state, "");
        return id;
    }

    private Long create(InitiativeManagement initiative, Long userId) {
        Long initiativeId = initiativeDao.create(initiative, userId);

        initiativeDao.updateLinks(initiativeId, initiative.getLinks());

        initiativeDao.updateInvitations(initiativeId, initiative.getInitiatorInvitations(),
                initiative.getRepresentativeInvitations(),
                initiative.getReserveInvitations());

        return initiativeId;
    }

    private void assertContacInfoEquals(ContactInfo expected, ContactInfo actual) {
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getPhone(), actual.getPhone());
    }


    public static InitiativeManagement createNotEndedInitiative(Long id) {
        String chg = getChanger();
        InitiativeManagement initiative = new InitiativeManagement();
        initiative.assignId(id);
        initiative.setFinancialSupport(true);
        initiative.setFinancialSupportURL(new InitURI("http://www.solita.fi" + chg));
        initiative.setName(asLocalizedString("Nimi" + chg, null));
        initiative.setProposal(asLocalizedString("Ehdotus" + chg, null));
        initiative.setAcceptanceIdentifier("some acceptance identifier");
        initiative.setProposalType(ProposalType.LAW);
        initiative.setRationale(asLocalizedString("Perustelut"+chg, null));
        initiative.setPrimaryLanguage(LanguageCode.FI);
        initiative.setStartDate(today);
        initiative.assignEndDate(today.plusMonths(6));
        initiative.setSupportStatementsInWeb(true);
        initiative.setSupportStatementsOnPaper(true);

        initiative.setLinks(Lists.newArrayList(intiativeLinkCreateValues(), intiativeLinkCreateValues()));

        return initiative;
    }

    public static Link intiativeLinkCreateValues() {
        String chg = getChanger();
        Link link = new Link();
        link.setLabel("Solita"+chg);
        link.setUri(new InitURI("http://www.solita.fi"+chg));
        return link;
    }


    public static Author createAuthor(Long userId, boolean initiator, boolean representative, boolean reserve) {
        String chg = getChanger();
        Author author = new Author(userId, "Etunimi"+chg, "Sukunimi"+chg, DOB, TestHelper.createDefaultMunicipality());
        author.setInitiator(initiator);
        author.setRepresentative(representative);
        author.setReserve(reserve);

        author.assignAddress("Kotikatu 5"+chg);
        author.assignEmail("email"+chg+"@domain.fi");
        author.assignPhone("123456"+chg);
        return author;
    }

    public static Author createAuthor(Long userId) {
        return createAuthor(userId, true, true, false);
    }

    private static String getChanger() {
        //increments changer string to ensure that each version of test data is different
        stringChangerIndex++;
        return stringChangerIndex.toString();
    }

    private void assertBeforeOrEqual(DateTime before, DateTime after) {
        assertTrue(before.isEqual(after) || before.isBefore(after));
    }

    private void assertBeforeOrEqualNow(DateTime value) {
        assertTrue("Not true: "+value+"<="+new DateTime(), value.isBeforeNow() || value.isEqualNow());
    }

}
