package fi.om.initiative.dto;

import org.joda.time.DateTime;

public class SupportVoteBatch {

    private Long id;
    
    private DateTime created;
    
    private long voteCount;

    public SupportVoteBatch(Long id, DateTime created, long voteCount) {
        this.id = id;
        this.created = created;
        this.voteCount = voteCount;
    }

    public Long getId() {
        return id;
    }

    public DateTime getCreated() {
        return created;
    }

    public long getVoteCount() {
        return voteCount;
    }
    
}
