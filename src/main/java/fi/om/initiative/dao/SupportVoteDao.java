package fi.om.initiative.dao;

import java.util.List;

import org.joda.time.DateTime;

import fi.om.initiative.dto.SupportVote;
import fi.om.initiative.dto.SupportVoteBatch;

public interface SupportVoteDao {

    void insertSupportVote(SupportVote vote);

    void incrementSupportCount(Long initiativeId);
    
    List<SupportVote> getSupportVotes(Long batchId);

    SupportVote getVote(Long initiativeId, String supportId);
    
    int createBatch(Long initiativeId);

    List<SupportVoteBatch> getSupportVoteBatches(Long initiativeId);

    void removeSupportVotes(Long initiativeId, DateTime supportStatementsRemoved, Long userId);

}
