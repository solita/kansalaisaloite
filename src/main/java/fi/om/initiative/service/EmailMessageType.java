package fi.om.initiative.service;

public enum EmailMessageType {
    INVITATION_ACCEPTED,
    INVITATION_REJECTED,
    CONFIRM_ROLE,
    AUTHOR_CONFIRMED,
    AUTHOR_REMOVED,
    SENT_TO_OM,
    ACCEPTED_BY_OM,
    REJECTED_BY_OM,
    SENT_TO_VRK,
    VRK_RESOLUTION,
    SENT_TO_PARLIAMENT, 
    REMOVED_SUPPORT_VOTES,
    VOTING_ENDED;

    private final String messageKeySuffix;
    
    EmailMessageType() {
        this.messageKeySuffix = name().replaceAll("_","-").toLowerCase();
    }
    
    public String toString() {
        return messageKeySuffix;
    }
}
