package fi.om.initiative.dto;

import static com.mysema.commons.lang.Assert.notNull;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

public class SupportVote {

    @NotNull
    private final Long initiativeId;
    
    @NotNull
    private final String supportId;
    
    @NotNull
    private final String encryptedDetails;
    
    @NotNull
    private final DateTime created;

    private Long verificationBatch;
    
    public SupportVote(Long initiativeId, String supportId,
            String encryptedDetails, DateTime created) {
        this.initiativeId = notNull(initiativeId, "initiativeId");
        this.supportId = notNull(supportId, "supportId");
        this.encryptedDetails = notNull(encryptedDetails, "encryptedDetails");
        this.created = notNull(created, "created");
    }

    public Long getInitiativeId() {
        return initiativeId;
    }

    public String getSupportId() {
        return supportId;
    }

    public String getEncryptedDetails() {
        return encryptedDetails;
    }

    public DateTime getCreated() {
        return created;
    }

    public Long getVerificationBatch() {
        return verificationBatch;
    }

    public void setVerificationBatch(Long verificationBatch) {
        this.verificationBatch = verificationBatch;
    }
    
}
