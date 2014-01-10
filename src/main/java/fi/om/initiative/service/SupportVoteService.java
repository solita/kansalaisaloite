package fi.om.initiative.service;

import fi.om.initiative.dto.SupportVoteBatch;
import fi.om.initiative.dto.VotingInfo;
import fi.om.initiative.dto.initiative.InitiativeBase;
import fi.om.initiative.dto.initiative.InitiativeManagement;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Locale;

public interface SupportVoteService {
    
    int sendToVRK(Long initiativeId);
    
    void vote(Long initiativeId, Locale locale);

    DateTime getVotingTime(Long initiativeId);
    
    List<String> getVoteDetails(Long batchId);

    VotingInfo getVotingInfo(InitiativeBase initiative);

    List<SupportVoteBatch> getSupportVoteBatches(InitiativeManagement initiative);

    void removeSupportVotes(Long initiativeId);

}
