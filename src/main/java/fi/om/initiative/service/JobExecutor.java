package fi.om.initiative.service;

import fi.om.initiative.dao.SupportVoteDao;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class JobExecutor {

    private static final String EVERY_DAY_AT_MIDNIGHT = "0 0 0 * * *";

    private final Logger log = LoggerFactory.getLogger(JobExecutor.class);

    @Resource
    private SupportVoteDao supportVoteDao;

    @Resource
    private FollowService followService;

    @Scheduled(cron = EVERY_DAY_AT_MIDNIGHT)
    public void updateDenormalizedSupportCountForInitiatives() {

        // Support counts are denormalized in one-day-delay (today we will denormalize history until yesterday).
        // Therefore the last time we'll denormalize supports for initiative is the day after it's ended.
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate yesterday = LocalDate.now().minusDays(1);

        List<Long> initiativeIdsForRunningInitiatives = supportVoteDao.getInitiativeIdsForSupportVoteDenormalization(yesterday);

        log.info("About to denormalize supportcount for " + initiativeIdsForRunningInitiatives.size() + " initiatives.");
        for (Long initiativeForUpdating : initiativeIdsForRunningInitiatives) {

            Map<LocalDate, Long> supportVoteCountByDateUntil = supportVoteDao.getSupportVoteCountByDateUntil(initiativeForUpdating, yesterday);

            supportVoteDao.saveDenormalizedSupportCountDataJson(initiativeForUpdating, toJson(supportVoteCountByDateUntil));

            supportVoteDao.saveDenormalizedSupportCountData(initiativeForUpdating, supportVoteCountByDateUntil);

        }
        log.info("Supportcounts denormalized.");

    }

    @Scheduled(cron = EVERY_DAY_AT_MIDNIGHT)
    public void sendEmailsForEndedInitiatives() {
        followService.sendEmailsForEndedInitiatives(LocalDate.now());
    }

    @PostConstruct
    public void executeAllJobs() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateDenormalizedSupportCountForInitiatives();
            }
        }).start();

    }

    static String toJson(Map<LocalDate, Long> supportVoteCountByDateUntil) {

        TreeMap<LocalDate, Long> orderedMap = new TreeMap<>(supportVoteCountByDateUntil);

        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (Map.Entry<LocalDate, Long> localDateEntry : orderedMap.entrySet()) {
            if (builder.length() > 1) {
                builder.append(",");
            }
            builder.append(String.format("{\"d\":\"%s\",\"n\":%d}", localDateEntry.getKey(), localDateEntry.getValue()));
        }

        builder.append("]");
        return builder.toString();
    }

}
