package fi.om.initiative.dto;

import fi.om.initiative.dto.initiative.InitiativeManagement;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDate;
import org.joda.time.ReadablePeriod;


public class InitiativeSettings {

    private final int invitationExpirationDays;
    
    private final int minSupportCountForSearch;

    private final int requiredVoteCount;
    
    private final ReadablePeriod requiredMinSupportCountDuration;
    
    private final ReadablePeriod votingDuration;

    private final ReadablePeriod sendToVrkDuration;

    private final ReadablePeriod sendToParliamentDuration;

    private final ReadablePeriod votesRemovalDuration;

    private final ReadablePeriod omSearchBeforeVotesRemovalDuration;

    public InitiativeSettings(int invitationExpirationDays, int minSupportCountForSearch, 
            int requiredVoteCount, ReadablePeriod requiredMinSupportCountDuration, ReadablePeriod votingDuration,
            ReadablePeriod sendToVrkDuration, ReadablePeriod sendToParliamentDuration,
            ReadablePeriod votesRemovalDuration, ReadablePeriod omSearchBeforeVotesRemovalDuration) {
        this.invitationExpirationDays = invitationExpirationDays;
        this.minSupportCountForSearch = minSupportCountForSearch;
        this.requiredVoteCount = requiredVoteCount;
        this.requiredMinSupportCountDuration = requiredMinSupportCountDuration;
        this.votingDuration = votingDuration;
        this.sendToVrkDuration = sendToVrkDuration;
        this.sendToParliamentDuration = sendToParliamentDuration;
        this.votesRemovalDuration = votesRemovalDuration;
        this.omSearchBeforeVotesRemovalDuration = omSearchBeforeVotesRemovalDuration;
    }

    public ManagementSettings getManagementSettings(InitiativeManagement initiative, User currentUser) {
        return getManagementSettings(initiative, EditMode.NONE, currentUser);
    }
    
    public ManagementSettings getManagementSettings(InitiativeManagement initiative, EditMode editMode, User currentUser) {
        return new ManagementSettings(initiative, editMode, currentUser, requiredVoteCount, sendToVrkDuration);
    }

    public int getInvitationExpirationDays() {
        return invitationExpirationDays;
    }

    public int getMinSupportCountForSearch() {
        return minSupportCountForSearch;
    }

    public int getRequiredVoteCount() {
        return requiredVoteCount;
    }

    public ReadablePeriod getRequiredMinSupportCountDuration() {


        int kuukaudet = requiredMinSupportCountDuration.get(DurationFieldType.months());

        return requiredMinSupportCountDuration;
    }

    public ReadablePeriod getVotingDuration() {
        return votingDuration;
    }

    public ReadablePeriod getSendToVrkDuration() {
        return sendToVrkDuration;
    }

    public ReadablePeriod getSendToParliamentDuration() {
        return sendToParliamentDuration;
    }

    public ReadablePeriod getVotesRemovalDuration() {
        return votesRemovalDuration;
    }

    public ReadablePeriod getOmSearchBeforeVotesRemovalDuration() {
        return omSearchBeforeVotesRemovalDuration;
    }

    public MinSupportCountSettings getMinSupportCountSettings() {
        return new MinSupportCountSettings(minSupportCountForSearch, requiredMinSupportCountDuration);
    }

    public static class MinSupportCountSettings {

        public final int requiredMinSupportCountForSearch;

        public final ReadablePeriod requiredMinSupportCountDuration;

        public final LocalDate startDateNotBefore;

        public MinSupportCountSettings(int requiredMinSupportCountForSearch, ReadablePeriod requiredMinSupportCountDuration) {
            this.requiredMinSupportCountForSearch = requiredMinSupportCountForSearch;
            this.requiredMinSupportCountDuration = requiredMinSupportCountDuration;
            this.startDateNotBefore = LocalDate.now().minus(requiredMinSupportCountDuration);
        }

//        @Override
//        // This is for testing/mocking purposes
//        public boolean equals(Object obj) {
//
//            if (obj == null || obj.getClass() != this.getClass())
//                return false;
//
//            MinSupportCountSettings that = (MinSupportCountSettings) obj;
//
//            return (that.startDateNotBefore.equals(this.startDateNotBefore)
//                    && that.requiredMinSupportCountForSearch == this.requiredMinSupportCountForSearch
//                    && that.requiredMinSupportCountDuration.equals(requiredMinSupportCountDuration));
//
//        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MinSupportCountSettings)) return false;

            MinSupportCountSettings that = (MinSupportCountSettings) o;

            if (requiredMinSupportCountForSearch != that.requiredMinSupportCountForSearch) return false;
            if (requiredMinSupportCountDuration != null ? !requiredMinSupportCountDuration.equals(that.requiredMinSupportCountDuration) : that.requiredMinSupportCountDuration != null)
                return false;
            if (startDateNotBefore != null ? !startDateNotBefore.equals(that.startDateNotBefore) : that.startDateNotBefore != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = requiredMinSupportCountForSearch;
            result = 31 * result + (requiredMinSupportCountDuration != null ? requiredMinSupportCountDuration.hashCode() : 0);
            result = 31 * result + (startDateNotBefore != null ? startDateNotBefore.hashCode() : 0);
            return result;
        }
    }
}
