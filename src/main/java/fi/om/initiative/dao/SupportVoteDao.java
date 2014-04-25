package fi.om.initiative.dao;

import fi.om.initiative.dto.SupportVote;
import fi.om.initiative.dto.SupportVoteBatch;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.Map;

public interface SupportVoteDao {

    void insertSupportVote(SupportVote vote);

    void incrementSupportCount(Long initiativeId);
    
    List<SupportVote> getSupportVotes(Long batchId);

    SupportVote getVote(Long initiativeId, String supportId);
    
    int createBatch(Long initiativeId);

    List<SupportVoteBatch> getSupportVoteBatches(Long initiativeId);

    void removeSupportVotes(Long initiativeId, DateTime supportStatementsRemoved, Long userId);

    Map<LocalDate,Long> getSupportVoteCountByDateUntil(Long initiativeId, LocalDate tillDay);

    void saveDenormalizedSupportCountDataJson(Long initiativeid, String denormalizedData);

    String getDenormalizedSupportCountDataJson(Long initiativeId);

    void saveDenormalizedSupportCountData(Long initiativeId, Map<LocalDate, Long> denormalizedData);

    Map<LocalDate, Integer> getDenormalizedSupportCountData(Long initiativeId);

    List<Long> getInitiativeIdsForSupportVoteDenormalization(LocalDate runningTillDate);
}
