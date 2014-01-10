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
     * Ready to receive support votes as of startDate.  
     * <ul>
     * <li>Pending invitations removed.</li>
     * <li>Unconfirmed VEVs removed.</li>
     * <li>Indexed for public visibility when support vote threshold reached.</li>
     * </ul>
     */
    ACCEPTED,
    /**
     * Out of our hands: sent forward etc.
     */
    DONE,
    /**
     * Out of our hands: expired, rejected, etc.
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
