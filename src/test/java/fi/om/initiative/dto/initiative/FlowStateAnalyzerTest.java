package fi.om.initiative.dto.initiative;

import fi.om.initiative.dto.InitiativeSettings;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.ReadablePeriod;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static fi.om.initiative.dto.initiative.FlowState.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FlowStateAnalyzerTest {

    private FlowStateAnalyzer flowStateAnalyzer;

    private static final LocalDate TODAY = new LocalDate(2011, 1, 1);
    private static final LocalDate YESTERDAY = TODAY.minusDays(1);
    private static final LocalDate TOMORROW = TODAY.plusDays(1);
    private static final LocalDate TWO_DAYS_AGO = TODAY.minusDays(2);
    private static final Long ID = -5L;

    // Settings:
    public static final int INVITATION_EXPIRATION_DAYS = 30;
    public static final int MIN_SUPPORT_COUNT_FOR_SEARCH = 50;
    public static final int REQUIRED_VOTE_COUNT = 1000;
    public static final ReadablePeriod REQUIRED_MIN_SUPPORT_COUNT_DURATION = Days.days(1);
    public static final ReadablePeriod VOTING_DURATION = null;
    public static final ReadablePeriod SEND_TO_VRK_DURATION = null;
    public static final ReadablePeriod SEND_TO_PARLIAMENT_DURATION = Days.days(1);
    public static final ReadablePeriod VOTES_REMOVAL_DURATION = null;
    public static final ReadablePeriod OM_SEARCH_BEFORE_VOTES_REMOVAL_DURATION = null;

    private InitiativeInfo initiativeInfo;
    private InitiativeSettings settings;

    @Before
    public void setup() {
        settings = new InitiativeSettings(
                INVITATION_EXPIRATION_DAYS,
                MIN_SUPPORT_COUNT_FOR_SEARCH,
                REQUIRED_VOTE_COUNT,
                REQUIRED_MIN_SUPPORT_COUNT_DURATION,
                VOTING_DURATION,
                SEND_TO_VRK_DURATION,
                SEND_TO_PARLIAMENT_DURATION,
                VOTES_REMOVAL_DURATION,
                OM_SEARCH_BEFORE_VOTES_REMOVAL_DURATION);

        flowStateAnalyzer = new FlowStateAnalyzer(settings);
    }

    @After
    public void tearDown() {
        initiativeInfo = null;
        settings = null;
        flowStateAnalyzer = null;
    }

    @Test
    public void state_is_always_draft_if_no_startDate_is_not_set() {
        createInitiativeStartingAt(null);
        assertFlowState(DRAFT);
        initiativeInfo.assignState(InitiativeState.ACCEPTED);

        assertFlowState(DRAFT);
    }

    @Test
    public void flowState_is_same_as_initiative_state_if_state_is_not_accepted() {
        createInitiativeStartingAt(TODAY);
        for (InitiativeState state : InitiativeState.values()) {
            if (state != InitiativeState.ACCEPTED) {
                initiativeInfo.assignState(state);
                assertFlowState(FlowState.valueOf(state.name()));
            }
        }
    }

    @Test
    public void acceptedNotStarted_if_voting_not_started() {
        createAcceptedInitiativeStartingAt(null);
        setVotingNotStarted();

        assertFlowState(ACCEPTED_NOT_STARTED);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - -
    // Has enough verified support counts:
    // - - - - - - - - - - - - - - - - - - - - - - - -

    @Test
    public void confirmedRunning_if_voting_not_ended() {
        createAcceptedInitiativeStartingAt(TODAY);
        setEnoughVerifiedSupports();
        setVotingNotEnded();

        assertFlowState(ACCEPTED_CONFIRMED_RUNNING);
    }

    @Test
    public void confirmedFailed_if_sentToParliament_time_ended() {
        createAcceptedInitiativeStartingAt(TWO_DAYS_AGO);
        setEnoughVerifiedSupports();
        setVotingEnded();
        setEndToParliamentEnded(true);

        assertFlowState(ACCEPTED_CONFIRMED_FAILED);
    }

    @Test
    public void confirmed_if_sentToParliament_time_not_ended() {
        createAcceptedInitiativeStartingAt(TWO_DAYS_AGO);
        setEnoughVerifiedSupports();
        setVotingEnded();
        setEndToParliamentEnded(false);

        assertFlowState(ACCEPTED_CONFIRMED);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - -
    // NOT enough VERIFIED support counts:
    // - - - - - - - - - - - - - - - - - - - - - - - -

    // - - - - - - - - - - - - - - - - - - - - - - - -
    // Voting ended:
    // - - - - - - - - - - - - - - - - - - - - - - - -

    @Test
    public void failed_if_voting_ended_not_enough_support_votes() {
        createAcceptedInitiativeStartingAt(TWO_DAYS_AGO);
        setVotingEnded();

        assertFlowState(ACCEPTED_FAILED);
    }

    @Test
    public void funconfirmed_if_voting_ended_and_has_enough_support_votes() {
        createAcceptedInitiativeStartingAt(TWO_DAYS_AGO);
        setVotingEnded();
        setEnoughTotalSupports();

        assertFlowState(ACCEPTED_UNCONFIRMED);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - -
    // Voting not ended:
    // - - - - - - - - - - - - - - - - - - - - - - - -

    @Test
    public void running_if_hasRequiredMinSupportCount() {
        createAcceptedInitiativeStartingAt(null);
        setRequiredMinSupportCounts(true);
        setMinSupportcountDurationEnded(true);

        assertFlowState(ACCEPTED_RUNNING);
    }

    @Test
    public void acceptedFirstMonth_if_min_support_count_duration_not_ended() {
        createAcceptedInitiativeStartingAt(null);
        setMinSupportcountDurationEnded(false);

        assertFlowState(ACCEPTED_FIRST_MONTH);
    }

    @Test
    public void firstMonthFailed_if_min_support_count_duration_ended_and_not_enough_support_counts() {
        createAcceptedInitiativeStartingAt(null);
        setMinSupportcountDurationEnded(true);
        setRequiredMinSupportCounts(false);

        assertFlowState(ACCEPTED_FIRST_MONTH_FAILED);
    }


    // Utilities


    private void setMinSupportcountDurationEnded(boolean isEnded) {
        if (isEnded) {
            safeAssignStartDate(TODAY.minus(REQUIRED_MIN_SUPPORT_COUNT_DURATION).minusDays(1));
        }
        else {
            safeAssignStartDate(TODAY.minus(REQUIRED_MIN_SUPPORT_COUNT_DURATION));
        }

        assert(initiativeInfo.isMinSupportCountDurationEnded(REQUIRED_MIN_SUPPORT_COUNT_DURATION, TODAY) == isEnded);
    }

    private void setEnoughTotalSupports() {
        initiativeInfo.assignSupportCount(REQUIRED_VOTE_COUNT+1);
        assert(initiativeInfo.getTotalSupportCount() > REQUIRED_VOTE_COUNT);
    }

    private void setEnoughVerifiedSupports() {
        initiativeInfo.setVerifiedSupportCount(REQUIRED_VOTE_COUNT + 1);
        assert(initiativeInfo.getVerifiedSupportCount() > settings.getRequiredVoteCount());
    }

    private void createAcceptedInitiativeStartingAt(LocalDate startDate) {
        createInitiativeStartingAt(startDate);
        initiativeInfo.assignState(InitiativeState.ACCEPTED);
    }

    private void createInitiativeStartingAt(LocalDate startDate) {
        initiativeInfo = new InitiativeInfo(ID);
        safeAssignStartDate(startDate);
    }

    private void setVotingNotStarted() {
        safeAssignStartDate(TODAY.plusDays(1));
        assert (!initiativeInfo.isVotingStarted(TODAY));
    }

    private void safeAssignStartDate(LocalDate startDate) {
        if (initiativeInfo.getStartDate() != null) {
            throw new RuntimeException("Trying to re-initialize startDate. Give null for " +
                    "createAcceptedInitiativeStartingAt(null) if you're about to use utilityfunctions which " +
                    "need to assign startDate for them to work correctly." );
        }
        initiativeInfo.setStartDate(startDate);
    }

    private void setVotingNotEnded() {
        initiativeInfo.assignEndDate(TOMORROW);
        assert(!initiativeInfo.isVotingEnded(TODAY));
    }

    private void setVotingEnded() {
        initiativeInfo.assignEndDate(YESTERDAY);
        assert(initiativeInfo.isVotingEnded(TODAY));
    }

    private void setEndToParliamentEnded(boolean isEnded) {
        if (isEnded) {
            initiativeInfo.setVerified(initiativeInfo.getStartDate());
        }
        else {
            initiativeInfo.setVerified(TODAY);
        }
        assert(initiativeInfo.isSendToParliamentEnded(settings.getSendToParliamentDuration(), TODAY) == isEnded);
    }

    private void setRequiredMinSupportCounts(boolean hasRequired) {
        if (hasRequired) {
            initiativeInfo.assignSupportCount(MIN_SUPPORT_COUNT_FOR_SEARCH+1);
        }
        else {
            initiativeInfo.assignSupportCount(MIN_SUPPORT_COUNT_FOR_SEARCH-1);
        }
        assert(initiativeInfo.hasTotalSupportCountAtLeast(MIN_SUPPORT_COUNT_FOR_SEARCH) == hasRequired);
    }

    private void assertFlowState(FlowState expected) {
        assertThat(flowStateAnalyzer.getFlowState(initiativeInfo, TODAY), is(expected));
    }



}
