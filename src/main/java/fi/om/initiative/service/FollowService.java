package fi.om.initiative.service;


import com.google.common.collect.Lists;
import fi.om.initiative.dao.FollowInitiativeDao;
import fi.om.initiative.dao.InitiativeDao;
import fi.om.initiative.dto.Follower;
import fi.om.initiative.dto.InitiativeSettings;
import fi.om.initiative.dto.initiative.InitiativeInfo;
import fi.om.initiative.dto.initiative.InitiativeManagement;
import org.joda.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

public class FollowService {

    @Resource
    private InitiativeDao initiativeDao;

    @Resource
    private FollowInitiativeDao followInitiativeDao;

    @Resource
    private EmailService emailService;

    @Resource
    InitiativeSettings initiativeSettings;

    @Transactional(readOnly = true)
    public void sendEmailsForEndedInitiatives(LocalDate today) {

        LocalDate yesterday = today.minusDays(1);

        class InitiativeForSending {
            private final InitiativeManagement initiativeManagement;
            private final List<Follower> followers;

            public InitiativeForSending(InitiativeManagement initiativeManagement, List<Follower> followers) {
                this.initiativeManagement = initiativeManagement;
                this.followers = followers;
            }
        }

        List<InitiativeForSending> initiativeForSendingList = Lists.newArrayList();

        for (InitiativeInfo initiative : initiativeDao.listAllInitiatives()) {
            if (hasEndedBetween(today, yesterday, initiative)) {
                initiativeForSendingList.add(
                        new InitiativeForSending(
                                initiativeDao.getInitiativeForManagement(initiative.getId(), false),
                                followInitiativeDao.listFollowers(initiative.getId()))
                );
            }
        }

        for (InitiativeForSending initiativeForSending : initiativeForSendingList) {
            emailService.sendStatusInfoToVEVs(initiativeForSending.initiativeManagement, EmailMessageType.VOTING_ENDED);
            emailService.sendStatusInfoToFollowers(initiativeForSending.initiativeManagement, EmailMessageType.VOTING_ENDED, initiativeForSending.followers);
        }
    }

    private static boolean hasEndedBetween(LocalDate today, LocalDate yesterday, InitiativeInfo initiative) {
        return (initiative.isVotingSuspended(0, null, today)
                && !initiative.isVotingSuspended(0, null, yesterday))
                ||
                (initiative.isVotingEnded(today)
                && !initiative.isVotingEnded(yesterday));
    }
}
