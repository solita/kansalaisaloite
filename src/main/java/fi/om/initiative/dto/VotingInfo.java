package fi.om.initiative.dto;

import com.mysema.commons.lang.Assert;
import fi.om.initiative.dto.initiative.InitiativeInfo;
import fi.om.initiative.dto.initiative.InitiativeState;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.ReadablePeriod;


public class VotingInfo {

    private final boolean allowVotingAction; // voting link/button is shown also to unauthenticated users if initiative is in the right state
    
    private final boolean allowVoting;       // actual voting is allowed only for authenticated and valid users
    
    private final DateTime votingTime;
    
    private final LocalDate startDate;
    
    private final LocalDate endDate;
    
    private final boolean votingStarted;
    
    private final boolean votingEnded;
    
    private final boolean votingInProggress;

    private final boolean votingSuspended;

    public VotingInfo() {
        allowVotingAction = allowVoting = votingInProggress = votingSuspended = votingStarted = votingEnded = false;
        votingTime = null;
        startDate = endDate = null;
    }
    
    public VotingInfo(InitiativeInfo initiative, User user, DateTime now, DateTime votingTime,
            int minSupportCountForSearch, ReadablePeriod requiredMinSupportCountDuration) {
        Assert.notNull(initiative.getId(), "initiative.id");
        this.votingTime = votingTime;
        this.startDate = initiative.getStartDate();
        this.endDate = initiative.getEndDate();
        
        if (initiative.getState() == InitiativeState.ACCEPTED) {
            LocalDate today = now.toLocalDate();

            votingStarted = initiative.isVotingStarted(today); // startDate <= today
            votingEnded = initiative.isVotingEnded(today);     // endDate <= today
            
            votingInProggress = initiative.isVotingInProggress(today);

            // Voting suspended
            votingSuspended = initiative.isVotingSuspended(minSupportCountForSearch, requiredMinSupportCountDuration, today);

            boolean initiativeInVotableState = votingInProggress && !votingSuspended;
            if (user.isAuthenticated()) {
                allowVoting = initiativeInVotableState 
                                && votingTime == null
                                && user.isAllowedToVote(now); // User is Finnish citizen and adult
                allowVotingAction = allowVoting;
            } else {
                allowVoting = false; 
                allowVotingAction = initiativeInVotableState;
            }

        } else {
            allowVotingAction = allowVoting = votingInProggress = votingSuspended = votingStarted = votingEnded = false;
        }
    }

    public boolean isAllowVotingAction() {
        return allowVotingAction;
    }
    
    public boolean isAllowVoting() {
        return allowVoting;
    }

    public DateTime getVotingTime() {
        return votingTime;
    }

    public boolean isVotingInProggress() {
        return votingInProggress;
    }

    public boolean isVotingSuspended() {
        return votingSuspended;
    }

    public boolean isVotingStarted() {
        return votingStarted;
    }

    public boolean isVotingEnded() {
        return votingEnded;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

}
