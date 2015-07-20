package fi.om.initiative.service;

import fi.om.initiative.dto.SupportVoteBatch;
import fi.om.initiative.dto.VotingInfo;
import fi.om.initiative.dto.initiative.InitiativeBase;
import fi.om.initiative.dto.initiative.InitiativeManagement;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.Locale;
import java.util.SortedMap;

public interface SupportVoteService {
    
    int sendToVRK(Long initiativeId);
    
    void vote(Long initiativeId, Locale locale);

    DateTime getVotingTime(Long initiativeId);
    
    List<String> getVoteDetails(Long batchId);

    VotingInfo getVotingInfo(InitiativeBase initiative);

    VotingInfo getVotingInfoWithoutUserData(InitiativeBase initiative);

    List<SupportVoteBatch> getSupportVoteBatches(InitiativeManagement initiative);

    String getSupportVotesPerDateJson(Long initiativeId);

    void removeSupportVotes(Long initiativeId);

    SortedMap<LocalDate, Integer> getDenormalizedSupportCountData(Long initiativeId);

}
