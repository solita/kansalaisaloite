package fi.om.initiative.service;

import com.google.common.collect.Lists;
import com.mysema.commons.lang.Assert;
import fi.om.initiative.dao.SupportVoteDao;
import fi.om.initiative.dto.*;
import fi.om.initiative.dto.initiative.InitiativeBase;
import fi.om.initiative.dto.initiative.InitiativeManagement;
import fi.om.initiative.dto.initiative.InitiativePublic;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.annotation.Resource;

import java.util.List;
import java.util.Locale;

public class SupportVoteServiceImpl implements SupportVoteService {
    
    private final Logger log = LoggerFactory.getLogger(SupportVoteServiceImpl.class);
    
    private static final String VRK_CSV_DELIM = "|";
    
    static DateTimeFormatter VRK_DTF = DateTimeFormat.forPattern("yyyyMMdd");
    
    @Resource UserService userService;

    @Resource SupportVoteDao supportVoteDao;

    @Resource EmailService emailService;
    
    @Resource EncryptionService encryptionService;
    
    @Resource InitiativeService initiativeService;

    @Resource InitiativeSettings initiativeSettings;
    
    public SupportVoteServiceImpl() {}
    
    public SupportVoteServiceImpl(SupportVoteDao supportVoteDao,
            UserService userService, 
            EmailService emailService,
            EncryptionService encryptionService,
            InitiativeService initiativeService,
            InitiativeSettings initiativeSettings) {
        this.supportVoteDao = supportVoteDao;
        this.userService = userService;
        this.emailService = emailService;
        this.encryptionService = encryptionService;
        this.initiativeService = initiativeService;
        this.initiativeSettings = initiativeSettings;
    }


    @Override
    @Transactional(readOnly=false)
    public void vote(Long initiativeId, Locale locale) {
        User user = userService.getUserInRole(Role.AUTHENTICATED);

        Assert.notNull(initiativeId, "initiativeId");
        
        DateTime now = new DateTime();
        
        InitiativePublic initiative = initiativeService.getInitiativeForPublic(initiativeId);

        VotingInfo votingInfo = getVotingInfo(initiative, user, now);

        if (votingInfo.isAllowVoting()) {
            // supportId: base64(sha256(initiative_id & ssn & sharedSecret))
            String supportId = encryptionService.initiativeSupportHash(initiativeId, user.getSsn());
            
            String encryptedDetails = encryptionService.encrypt(buildVoteDetails(user, locale, now));
            
            SupportVote vote = new SupportVote(initiativeId, supportId, encryptedDetails, now);
            
            supportVoteDao.incrementSupportCount(vote.getInitiativeId());
            
            supportVoteDao.insertSupportVote(vote);
            
            // NOTE: User is not logged!
            log("vote", initiativeId, null, true);
        } else {
            throw new IllegalStateException("Voting is not allowed");
        }
    }
    
    @Override
    @Transactional(readOnly=true)
    public VotingInfo getVotingInfo(InitiativeBase initiative) {
        return getVotingInfo(initiative, userService.getCurrentUser(), new DateTime());
    }

    private VotingInfo getVotingInfo(InitiativeBase initiative, User user, DateTime dateTime) {
        DateTime now = new DateTime();
        
        // Initiative doesn't allow voting
        if (initiative.getId() == null) {
            return new VotingInfo();
        } else {
            DateTime votingTime = null;
            if (user.isAuthenticated()) {
                votingTime = getVotingTime(initiative.getId());
            }
            return new VotingInfo(initiative, user, now, votingTime, 
                    initiativeSettings.getMinSupportCountForSearch(), 
                    initiativeSettings.getRequiredMinSupportCountDuration());
        }
    }
    
    @Override
    @Transactional(readOnly=true)
    public @Nullable DateTime getVotingTime(Long initiativeId) {
        User user = userService.getUserInRole(Role.AUTHENTICATED);

        Assert.notNull(initiativeId, "initiativeId");
        
        String supportId = encryptionService.initiativeSupportHash(initiativeId, user.getSsn());
        
        SupportVote vote = supportVoteDao.getVote(initiativeId, supportId);
        return vote != null ? vote.getCreated() : null;
    }
    
    private String buildVoteDetails(User user, Locale locale, DateTime now) {
        return new StringBuilder(128)
        // Last name
        .append(user.getLastName())
        .append(VRK_CSV_DELIM)
        
        // First names
        .append(user.getFirstNames())
        .append(VRK_CSV_DELIM)
        
        // DateOf Birth
        .append(user.getDateOfBirth().toString(VRK_DTF))
        .append(VRK_CSV_DELIM)
        
        // Home municipality
        .append(user.getHomeMunicipality().getTranslation(locale))
        .append(VRK_CSV_DELIM)
        
        // Voting time
        .append(now.toString(VRK_DTF))
        
        .toString();
    }
    
    @Override
    @Transactional(readOnly=false)
    public int sendToVRK(Long initiativeId) {
        final String METHOD_NAME = "sendToVRK"; 
        User user = userService.getUserInRole(Role.REGISTERED);
        
        InitiativeManagement initiative = initiativeService.getInitiativeForManagement(initiativeId);
        ManagementSettings managementSettings = initiativeSettings.getManagementSettings(initiative, user);
        
        // Allowed only for authors 
        if (!managementSettings.isAllowSendToVRK()) {
            throw new AccessDeniedException("Not allowed for current user or current state");
        }
        
        int batchSize = supportVoteDao.createBatch(initiativeId);
        
        emailService.sendNotificationToVRK(initiative, batchSize);
        emailService.sendStatusInfoToVEVs(initiative, EmailMessageType.SENT_TO_VRK);

        initiativeService.endInitiative(initiativeId);
        
        log(METHOD_NAME, initiativeId, user, true);
        return batchSize;
    }
    
    @Override
    @Transactional(readOnly=true)
    public List<SupportVoteBatch> getSupportVoteBatches(InitiativeManagement initiative) {
        userService.requireUserInRole(Role.REGISTERED);
        // SupportVoteBatch doesn't contain any critical information - trust given parameter for efficiency
        return supportVoteDao.getSupportVoteBatches(initiative.getId());
    }

    @Override
    @Transactional(readOnly=true)
    public List<String> getVoteDetails(Long batchId) {
        final String METHOD_NAME = "getVoteDetails"; 
        User user = userService.getUserInRole(Role.VRK);
        
        List<SupportVote> supportVotes = supportVoteDao.getSupportVotes(batchId);
        List<String> votes = Lists.newArrayListWithCapacity(supportVotes.size());
        
        for (SupportVote supportVote : supportVotes) {
            votes.add(encryptionService.decrypt(supportVote.getEncryptedDetails()));
        }

        log(METHOD_NAME, batchId, user, true);
        return votes;
    }

    @Override
    @Transactional(readOnly=false)
    public void removeSupportVotes(Long initiativeId) {
        final String METHOD_NAME = "removeSupportVotes"; 
        User user = userService.getUserInRole(Role.REGISTERED);
        
        Assert.notNull(initiativeId, "initiativeId");
        Assert.notNull(user.getId(), "userId");
        
        // NOTE: No need to lock initiative for this operation ("select for update")  
        InitiativeManagement initiative = initiativeService.getInitiativeForManagement(initiativeId); 
        ManagementSettings managementSettings = initiativeSettings.getManagementSettings(initiative, user);

        // Allowed only for authors
        if (!managementSettings.isAllowRemoveSupportVotes()) {
            throw new IllegalStateException("Not allowed for current user or current state");
        }
        DateTime now = new DateTime();
        
        supportVoteDao.removeSupportVotes(initiativeId, now, user.getId());

        emailService.sendStatusInfoToVEVs(initiative, EmailMessageType.REMOVED_SUPPORT_VOTES);

        log(METHOD_NAME, initiativeId, user, true);
    }

    private void log(final String method, final Long id, final User user, final boolean ok) {
        InitiativeServiceImpl.log(method, id, user, ok, log);
    }
}
