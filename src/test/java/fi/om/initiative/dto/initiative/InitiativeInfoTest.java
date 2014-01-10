package fi.om.initiative.dto.initiative;


import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InitiativeInfoTest {

    private static final Long ID = -5L;
    private static final LocalDate TODAY = new LocalDate(2010, 1, 1);
    private static final LocalDate TOMORROW = TODAY.plusDays(1);
    private static final LocalDate YESTERDAY = TODAY.minusDays(1);
    private static final LocalDate TWO_DAYS_AGO = TODAY.minusDays(2);
    private static final LocalDate TWO_DAYS_LATER = TODAY.plusDays(2);

    private InitiativeInfo initiativeInfo;

    @Before
    public void setup() {
        initiativeInfo = new InitiativeInfo(ID);
    }
    @After
    public void tearDown() {
        initiativeInfo = null;
    }

    @Test
    public void isVotingStarted_is_false_if_startDate_is_null() {
        initiativeInfo.setStartDate(null);
        assertThat(initiativeInfo.isVotingStarted(anyLocalDate()), is(false));
    }

    @Test
    public void isVotingStarted_is_true_if_startDate_is_in_the_past() {
        initiativeInfo.setStartDate(YESTERDAY);
        assertThat(initiativeInfo.isVotingStarted(TODAY), is(true));
    }

    @Test
    public void isVotingStarted_is_true_if_startDate_is_today() {
        initiativeInfo.setStartDate(TODAY);
        assertThat(initiativeInfo.isVotingStarted(TODAY), is(true));
    }

    @Test
    public void isVotingStarted_is_false_if_startDate_is_in_the_future() {
        initiativeInfo.setStartDate(TOMORROW);
        assertThat(initiativeInfo.isVotingStarted(TODAY), is(false));
    }

    @Test
    public void isVotingEnded_is_false_if_endDate_is_null() {
        initiativeInfo.assignEndDate(null);
        assertThat(initiativeInfo.isVotingEnded(anyLocalDate()), is(false));
    }

    @Test
    public void isVotingEnded_is_true_if_endDate_is_in_the_past() {
        initiativeInfo.assignEndDate(YESTERDAY);
        assertThat(initiativeInfo.isVotingEnded(TODAY), is(true));
    }

    @Test
    public void isVotingEnded_is_false_if_endDate_is_in_the_future() {
        initiativeInfo.assignEndDate(TOMORROW);
        assertThat(initiativeInfo.isVotingEnded(TODAY), is(false));
    }

    @Test
    public void isVotingEnded_is_false_if_endDate_is_today() {
        initiativeInfo.assignEndDate(TODAY);
        assertThat(initiativeInfo.isVotingEnded(TODAY), is(false));
    }

    @Test
    public void isVotingInProgress_false_if_not_yeat_started() {
        initiativeInfo.setStartDate(TOMORROW);
        initiativeInfo.assignEndDate(TWO_DAYS_LATER);
        assertThat(initiativeInfo.isVotingInProggress(TODAY), is(false));
    }

    @Test
    public void isVotingInProgress_false_if_voting_already_ended() {
        initiativeInfo.setStartDate(TWO_DAYS_AGO);
        initiativeInfo.assignEndDate(YESTERDAY);
        assertThat(initiativeInfo.isVotingInProggress(TODAY), is(false));
    }

    @Test
    public void isVotingInProgress_true_if_between_begin_and_end_inclusive() {
        initiativeInfo.setStartDate(YESTERDAY);
        initiativeInfo.assignEndDate(TOMORROW);
        assertThat(initiativeInfo.isVotingInProggress(YESTERDAY), is(true));
        assertThat(initiativeInfo.isVotingInProggress(TODAY), is(true));
        assertThat(initiativeInfo.isVotingInProggress(TOMORROW), is(true));
    }

    @Test
    public void getEndDateForSendToParliament_is_verifyDate_plus_given_period() {
        initiativeInfo.setVerified(TODAY);
        assertThat(initiativeInfo.getEndDateForSendToParliament(Months.months(6)), is(TODAY.plusMonths(6)));
    }

    @Test
    public void getEndDateForSendToVRK_is_endDate_plus_given_period() {
        initiativeInfo.assignEndDate(TODAY);
        assertThat(initiativeInfo.getEndDateForSendToVrk(Months.months(3)), is(TODAY.plusMonths(3)));
    }

    @Test
    public void getEndDateForVotesRemoval_is_counted_from_VRKVerifyTime_if_greater_than_endDate() {
        initiativeInfo.setVerified((TOMORROW));
        initiativeInfo.assignEndDate(TODAY);
        assertThat(initiativeInfo.getEndDateForVotesRemoval(Months.months(1)), is(TOMORROW.plusMonths(1)));
    }

    @Test
    public void getEndDateForVotesRemoval_is_counted_from_endTime_if_greater_than_VRKVerifyTime() {
        initiativeInfo.setVerified((YESTERDAY));
        initiativeInfo.assignEndDate(TODAY);
        assertThat(initiativeInfo.getEndDateForVotesRemoval(Months.months(1)), is(TODAY.plusMonths(1)));
    }

    @Test
    public void isSentToParliamentEnded_is_false_if_endDateSentToParliament_is_today_or_in_the_future() {
        initiativeInfo.setVerified(TODAY);
        assertThat(initiativeInfo.isSendToParliamentEnded(Days.days(10), TODAY.plusDays(10)), is(false));
        assertThat(initiativeInfo.isSendToParliamentEnded(Days.days(20), TODAY.plusDays(10)), is(false));
    }

    @Test
    public void isSentToParliamentEnded_is_true_if_endDateSentToParliament_is_in_the_past() {
        initiativeInfo.setVerified(TODAY);
        assertThat(initiativeInfo.isSendToParliamentEnded(Days.days(10), TODAY.plusDays(11)), is(true));
    }

    @Test
    public void getTotalSupportCount_returns_verified_supportcount_if_greater_than_total() {
        initiativeInfo.assignSupportCount(10);
        initiativeInfo.setExternalSupportCount(5);
        initiativeInfo.setVerifiedSupportCount(30);
        assertThat(initiativeInfo.getTotalSupportCount(), is(30));
    }

    @Test
    public void getTotalSupportCount_returns_total_if_greater_than_verified_supportcount() {
        initiativeInfo.assignSupportCount(20);
        initiativeInfo.setExternalSupportCount(5);
        initiativeInfo.setVerifiedSupportCount(15);
        assertThat(initiativeInfo.getTotalSupportCount(), is(25));
    }

    @Test
    public void isVotesRemovalEndDateNear_returns_true_if_date_in_given_scope() {
        initiativeInfo.assignEndDate(TWO_DAYS_AGO);

        Days fourDayRemovalDuration = Days.days(4);
        assertThat(initiativeInfo.getEndDateForVotesRemoval(fourDayRemovalDuration), is(TWO_DAYS_LATER));
        assertThat(initiativeInfo.isVotesRemovalEndDateNear(TODAY, fourDayRemovalDuration, Days.days(3)), is(true));
    }

    @Test
    public void isVotesRemovalEndDateNear_returns_false_if_date_before_given_scope() {
        initiativeInfo.assignEndDate(TWO_DAYS_AGO);

        Days fourDayRemovalDuration = Days.days(4);
        assertThat(initiativeInfo.getEndDateForVotesRemoval(fourDayRemovalDuration), is(TWO_DAYS_LATER));
        assertThat(initiativeInfo.isVotesRemovalEndDateNear(TODAY, fourDayRemovalDuration, Days.days(1)), is(false));
    }

    @Test
    public void isVotesRemovalEndDateNear_returns_true_if_date_in_given_scope_inclusive() {
        initiativeInfo.assignEndDate(TWO_DAYS_AGO);

        Days fourDayRemovalDuration = Days.days(4);
        assertThat(initiativeInfo.getEndDateForVotesRemoval(fourDayRemovalDuration), is(TWO_DAYS_LATER));
        assertThat(initiativeInfo.isVotesRemovalEndDateNear(TODAY, fourDayRemovalDuration, Days.days(2)), is(true));
    }

    @Test
    public void isVotesRemovalEndDateNear_returns_true_if_given_date_in_future_over_the_scope() {
        initiativeInfo.assignEndDate(TWO_DAYS_AGO);

        Days fourDayRemovalDuration = Days.days(4);
        assertThat(initiativeInfo.getEndDateForVotesRemoval(fourDayRemovalDuration), is(TWO_DAYS_LATER));
        assertThat(initiativeInfo.isVotesRemovalEndDateNear(TODAY.plusDays(10), fourDayRemovalDuration, Days.days(2)), is(true));

    }

    @Test
    public void isMinSupportCountDurationEnded_returns_true_if_duration_is_ended() {
        initiativeInfo.setStartDate(TWO_DAYS_AGO);

        assertThat(initiativeInfo.isMinSupportCountDurationEnded(Days.days(1), TODAY), is(true));
    }

    @Test
    public void isMinSupportCountDurationEnded_returns_false_if_duration_not_ended() {
        initiativeInfo.setStartDate(TWO_DAYS_AGO);

        assertThat(initiativeInfo.isMinSupportCountDurationEnded(Days.days(3), TODAY), is(false));
        assertThat(initiativeInfo.isMinSupportCountDurationEnded(Days.days(2), TODAY), is(false)); // Inclusive, current day
    }

    @Test
    public void hasTotalSupportCountAtLeast_returns_true_if_count_equal_or_greater() {
        setTotalSupportCount(100);
        assertThat(initiativeInfo.hasTotalSupportCountAtLeast(99), is(true));
        assertThat(initiativeInfo.hasTotalSupportCountAtLeast(100), is(true));
    }

    @Test
    public void hasTotalSupportCountAtLeast_returns_false_if_count_less() {
        setTotalSupportCount(100);
        assertThat(initiativeInfo.hasTotalSupportCountAtLeast(101), is(false));
    }

    // - - - - - - - - - - - - -
    // isVotingSuspended
    // - - - - - - - - - - - - -


    @Test
    public void isVotingSuspended_is_true_if_votingInProgress_and_notEnoughSupport_and_minSupportDurationEnded() {

        int minSupportCountForSearch = 50;
        Days minSupportCountDuration = Days.days(1);

        initiativeInfo.setStartDate(TWO_DAYS_AGO);
        initiativeInfo.assignEndDate(TODAY);
        initiativeInfo.assignSupportCount(minSupportCountForSearch-1);

        // Assert that the state is what we wanted.
        assert(initiativeInfo.isVotingInProggress(TODAY));
        assert(!initiativeInfo.hasTotalSupportCountAtLeast(minSupportCountForSearch));
        assert(initiativeInfo.isMinSupportCountDurationEnded(minSupportCountDuration, TODAY));

        assertThat(initiativeInfo.isVotingSuspended(minSupportCountForSearch, minSupportCountDuration, TODAY), is(true));
    }

    @Test
    public void isVotingSuspended_is_false_if_votingNotInProgress() {

        int minSupportCountForSearch = 50;
        Days minSupportCountDuration = Days.days(1);

        initiativeInfo.setStartDate(TWO_DAYS_AGO);
        initiativeInfo.assignEndDate(YESTERDAY);
        initiativeInfo.assignSupportCount(minSupportCountForSearch-1);

        // Assert that the state is what we wanted.
        assert(!initiativeInfo.isVotingInProggress(TODAY));
        assert(!initiativeInfo.hasTotalSupportCountAtLeast(minSupportCountForSearch));
        assert(initiativeInfo.isMinSupportCountDurationEnded(minSupportCountDuration, TODAY));

        assertThat(initiativeInfo.isVotingSuspended(minSupportCountForSearch, minSupportCountDuration, TODAY), is(false));
    }

    @Test
    public void isVotingSuspended_is_false_if_has_required_minSupportCount() {

        int minSupportCountForSearch = 50;
        Days minSupportCountDuration = Days.days(1);

        initiativeInfo.setStartDate(TWO_DAYS_AGO);
        initiativeInfo.assignEndDate(TOMORROW);
        initiativeInfo.assignSupportCount(minSupportCountForSearch+1);

        // Assert that the state is what we wanted.
        assert(initiativeInfo.isVotingInProggress(TODAY));
        assert(initiativeInfo.hasTotalSupportCountAtLeast(minSupportCountForSearch));
        assert(initiativeInfo.isMinSupportCountDurationEnded(minSupportCountDuration, TODAY));

        assertThat(initiativeInfo.isVotingSuspended(minSupportCountForSearch, minSupportCountDuration, TODAY), is(false));
    }

    @Test
    public void isVotingSuspended_is_false_if_minSupportCountDuration_not_ended() {

        int minSupportCountForSearch = 50;
        Days minSupportCountDuration = Days.days(1);

        initiativeInfo.setStartDate(YESTERDAY);
        initiativeInfo.assignEndDate(TODAY);
        initiativeInfo.assignSupportCount(minSupportCountForSearch-1);

        // Assert that the state is what we wanted.
        assert(initiativeInfo.isVotingInProggress(TODAY));
        assert(!initiativeInfo.hasTotalSupportCountAtLeast(minSupportCountForSearch));
        assert(!initiativeInfo.isMinSupportCountDurationEnded(minSupportCountDuration, TODAY));

        assertThat(initiativeInfo.isVotingSuspended(minSupportCountForSearch, minSupportCountDuration, TODAY), is(false));
    }



    // Helpers

    private void setTotalSupportCount(int supportCount) {
        initiativeInfo.setVerifiedSupportCount(supportCount);
        assert(initiativeInfo.getTotalSupportCount() == supportCount);
    }

    private static LocalDate anyLocalDate() {
        return new LocalDate(1990, 1, 1);
    }

}
