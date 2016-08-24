package fi.om.initiative.service;


import com.google.common.collect.Lists;
import fi.om.initiative.dao.DuplicateException;
import fi.om.initiative.dao.FollowInitiativeDao;
import fi.om.initiative.dao.InitiativeDao;
import fi.om.initiative.dto.FollowInitiativeDto;
import fi.om.initiative.dto.Follower;
import fi.om.initiative.dto.InitiativeSettings;
import fi.om.initiative.dto.initiative.InitiativeInfo;
import fi.om.initiative.dto.initiative.InitiativeManagement;
import fi.om.initiative.dto.initiative.InitiativeState;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;

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
    private InitiativeSettings initiativeSettings;

    @Resource
    private SmartValidator validator;

    private final Logger log = LoggerFactory.getLogger(FollowService.class);

    private class InitiativeForSending {
        private final InitiativeManagement initiativeManagement;
        private final List<Follower> followers;

        public InitiativeForSending(InitiativeManagement initiativeManagement, List<Follower> followers) {
            this.initiativeManagement = initiativeManagement;
            this.followers = followers;
        }
    }

    @Transactional(readOnly = true)
    public void sendEmailsForEndedInitiatives(LocalDate today) {

        LocalDate yesterday = today.minusDays(1);

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
        return (initiative.getState() == InitiativeState.ACCEPTED &&
                (initiative.isVotingSuspended(initiativeSettings.getMinSupportCountForSearch(), initiativeSettings.getRequiredMinSupportCountDuration(), today)
                && !initiative.isVotingSuspended(initiativeSettings.getMinSupportCountForSearch(), initiativeSettings.getRequiredMinSupportCountDuration(), yesterday))
                ||
                (initiative.isVotingEnded(today)
                && !initiative.isVotingEnded(yesterday)
                && !initiative.isVotingSuspended(initiativeSettings.getMinSupportCountForSearch(), initiativeSettings.getRequiredMinSupportCountDuration(), yesterday)));
    }

    @Transactional
    public boolean followInitiative(long id, FollowInitiativeDto followInitiativeDto, BindingResult bindingResult) {
        validator.validate(followInitiativeDto, bindingResult);

        if (bindingResult.hasErrors()) {
            return false;
        }

        try {
            String unsubscribeHash = RandomHashCreator.randomString(40);
            followInitiativeDao.addFollow(id, new Follower(followInitiativeDto.getEmail(), unsubscribeHash));
            emailService.sendFollowConfirmationEmail(initiativeDao.getInitiativeForManagement(id, false), followInitiativeDto.getEmail(), unsubscribeHash);
        } catch (DuplicateException e) {
            log.warn("Duplicate following on " + id + ": " + followInitiativeDto.getEmail());
        }

        return true;
    }

    @Transactional(readOnly = true)
    public void sendEmailsHalfwayBetweenForStillRunningInitiatives(LocalDate today) {

        List<InitiativeForSending> initiativeForSendingList = Lists.newArrayList();

        for (InitiativeInfo initiative : initiativeDao.listAllInitiatives()) {
            if (hasBeenOpenForHalfOfTheVotingTime(initiative, today) && !initiative.isVotingSuspended(initiativeSettings.getMinSupportCountForSearch(), initiativeSettings.getRequiredMinSupportCountDuration(), today)) {
                initiativeForSendingList.add(
                        new InitiativeForSending(
                                initiativeDao.getInitiativeForManagement(initiative.getId(), false),
                                followInitiativeDao.listFollowers(initiative.getId()))
                );
            }
        }
        for (InitiativeForSending initiativeForSending : initiativeForSendingList) {
            emailService.sendStatusInfoToVEVs(initiativeForSending.initiativeManagement, EmailMessageType.VOTING_HALFWAY);
            emailService.sendStatusInfoToFollowers(initiativeForSending.initiativeManagement, EmailMessageType.VOTING_HALFWAY, initiativeForSending.followers);
        }
    }

    private boolean hasBeenOpenForHalfOfTheVotingTime(InitiativeInfo initiative, LocalDate today) {

        int daysBetweenStartAndEnd = Days.daysBetween(initiative.getStartDate(), initiative.getEndDate()).getDays();
        LocalDate notificationDate = initiative.getStartDate().plusDays(daysBetweenStartAndEnd / 2);
        return notificationDate.equals(today);
    }


}
