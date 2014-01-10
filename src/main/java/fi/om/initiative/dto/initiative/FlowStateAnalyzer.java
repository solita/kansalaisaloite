package fi.om.initiative.dto.initiative;

import fi.om.initiative.dto.InitiativeSettings;
import org.joda.time.LocalDate;

public class FlowStateAnalyzer {

    private final InitiativeSettings settings;

    public FlowStateAnalyzer(InitiativeSettings settings) {
        this.settings = settings;
    }

    public FlowState getFlowState(InitiativeInfo initiative) {
        return getFlowState(initiative, new LocalDate());
    }
    
    public FlowState getFlowState(InitiativeInfo initiative, LocalDate now) {
        if (initiative.getStartDate() == null) {
            return FlowState.DRAFT;
        }

        switch (initiative.getState()) {
            case DRAFT:
                return FlowState.DRAFT;
            case PROPOSAL:
                return FlowState.PROPOSAL;
            case REVIEW:
                return FlowState.REVIEW;
            case DONE:
                return FlowState.DONE;
            case CANCELED:
                return FlowState.CANCELED;
            case ACCEPTED:
                return acceptedFlowState(initiative, now);
            default: throw new IllegalArgumentException("Unknown InitiativeState: " + initiative.getState());
        }
    }

    private FlowState acceptedFlowState(InitiativeInfo initiative, LocalDate now) {
        if (!initiative.isVotingStarted(now)) {
            return FlowState.ACCEPTED_NOT_STARTED;
        }
        else if (hasEnoughVerifiedSupports(initiative)) {
            return enoughVerifiedSupportsFlowState(initiative, now);
        }
        else if (initiative.isVotingEnded(now)) {
            return votingEndedNotEnoughtVerifiedSupportsFlowState(initiative);
        }
        else if (!initiative.isMinSupportCountDurationEnded(settings.getRequiredMinSupportCountDuration(), now)) {
            return FlowState.ACCEPTED_FIRST_MONTH;
        }
        else if (initiative.hasTotalSupportCountAtLeast(settings.getMinSupportCountForSearch())) {
            return FlowState.ACCEPTED_RUNNING;
        }
        else {
            return FlowState.ACCEPTED_FIRST_MONTH_FAILED;
        }
    }

    private FlowState votingEndedNotEnoughtVerifiedSupportsFlowState(InitiativeInfo initiative) {
        if (initiative.hasTotalSupportCountAtLeast(settings.getRequiredVoteCount())) {
            return FlowState.ACCEPTED_UNCONFIRMED;
        } else {
            return FlowState.ACCEPTED_FAILED;
        }
    }

    private FlowState enoughVerifiedSupportsFlowState(InitiativeInfo initiative, LocalDate now) {
        if (initiative.isVotingEnded(now)) {
            if (isSentToParliamentEnded(initiative, now)) {
                return FlowState.ACCEPTED_CONFIRMED_FAILED;
            } else {
                return FlowState.ACCEPTED_CONFIRMED;
            }
        } else {
            return FlowState.ACCEPTED_CONFIRMED_RUNNING;
        }
    }

    private boolean isSentToParliamentEnded(InitiativeInfo initiative, LocalDate now) {
        return initiative.isSendToParliamentEnded(settings.getSendToParliamentDuration(), now);
    }

    private boolean hasEnoughVerifiedSupports(InitiativeInfo initiative) {
        return initiative.getVerifiedSupportCount() >= settings.getRequiredVoteCount();
    }

}
