package fi.om.initiative.dto.initiative;

public enum InitiativeState {
    /**
     * Open for editing: There's only one confirmed author.
     * 
     * Every save confirms current author.
     */
    DRAFT,

    /**
     * Intivations have been sent. Waiting for organizers to organize.
     * 
     * Limited editing: Opening limited fields for editing resets _all_ confirmations, i.e. opens 
     * initiative for editing.
     * 
     * Every save confirms current author.
     * 
     * If there's enough VEVs, allows sending to OM.
     */
    PROPOSAL,

    /**
     * Sent to review at OM.
     * <ul>
     * <li>Confirming role (via invitation or reconfirmation request) not allowed.</li>
     * <li>If rejected, returns to DRAFT state (limited editing). </li>
     * <li>If accepted, moves to ACCEPTED state</li>
     * </ul> 
     */
    REVIEW,

    /**
     * Accepted by OM - not sent to parliament.
     * Ready to receive support votes as of startDate until endDate.
     * Initiative also ends if less than 50 supports in the first month.
     * Note that there is no ENDED state - initiative has endDate and might also end
     * after 1month if it has less than 50 supports.
     */
    ACCEPTED,

    /**
     * Marked as sent to parliament
     */
    DONE,

    /**
     * Currently this is actually not featured at all.
     */
    CANCELED;

    public boolean isPublicState() {
        switch (this) {
            case ACCEPTED:
            case DONE:
            case CANCELED:
                return true;

            default:
            return false;
        }
    }

    public boolean isNotPublicState() {
        return !isPublicState();
    }
}
