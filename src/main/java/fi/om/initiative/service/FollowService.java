package fi.om.initiative.service;


import com.google.common.collect.Lists;
import fi.om.initiative.dao.DuplicateException;
import fi.om.initiative.dao.FollowInitiativeDao;
import fi.om.initiative.dao.InitiativeDao;
import fi.om.initiative.dto.Follower;
import fi.om.initiative.dto.InitiativeSettings;
import fi.om.initiative.dto.initiative.InitiativeInfo;
import fi.om.initiative.dto.initiative.InitiativeManagement;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger log = LoggerFactory.getLogger(FollowService.class);

    @Transactional(readOnly = false)
    public void followInitiative(String email, Long initiativeId) {

        String unsubscribeHash = new HashCreator(email).hash(initiativeId);

        try {
            followInitiativeDao.addFollow(initiativeId, new Follower(email, unsubscribeHash));
            emailService.sendFollowConfirmationEmail(initiativeDao.getInitiativeForManagement(initiativeId, false), email, unsubscribeHash);
        } catch (DuplicateException e) {
            log.warn("Duplicate following on " + initiativeId + ": " + email);
        }

    }

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

    private boolean hasEndedBetween(LocalDate today, LocalDate yesterday, InitiativeInfo initiative) {
        return (initiative.isVotingSuspended(initiativeSettings.getMinSupportCountForSearch(), initiativeSettings.getRequiredMinSupportCountDuration(), today)
                && !initiative.isVotingSuspended(initiativeSettings.getMinSupportCountForSearch(), initiativeSettings.getRequiredMinSupportCountDuration(), yesterday))
                ||
                (initiative.isVotingEnded(today)
                && !initiative.isVotingEnded(yesterday)
                && !initiative.isVotingSuspended(initiativeSettings.getMinSupportCountForSearch(), initiativeSettings.getRequiredMinSupportCountDuration(), yesterday));
    }
}
