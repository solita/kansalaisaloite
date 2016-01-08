package fi.om.initiative.dao;

import com.google.common.base.Optional;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.postgres.PostgresQuery;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.Expression;
import com.mysema.query.types.expr.DateTimeExpression;
import fi.om.initiative.dto.InfoTextCategory;
import fi.om.initiative.dto.LanguageCode;
import fi.om.initiative.dto.LocalizedString;
import fi.om.initiative.dto.ProposalType;
import fi.om.initiative.dto.author.AuthorRole;
import fi.om.initiative.dto.initiative.InitiativeInfo;
import fi.om.initiative.dto.initiative.InitiativeManagement;
import fi.om.initiative.dto.initiative.InitiativeState;
import fi.om.initiative.service.EncryptionService;
import fi.om.initiative.sql.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.ReadablePeriod;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

import static fi.om.initiative.sql.QInitiativeAuthor.initiativeAuthor;
import static fi.om.initiative.util.Locales.asLocalizedString;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class TestHelper {

    @Resource
    PostgresQueryFactory queryFactory;
    @Resource
    EncryptionService encryptionService;

    private static final QInitiative qInitiative = QInitiative.initiative;

    private static LocalDate DOB = new LocalDate().minusYears(20);
    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    private static final Expression<DateTime> CURRENT_TIME = DateTimeExpression.currentTimestamp(DateTime.class);

    public TestHelper() {
    }

    public TestHelper(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Transactional(readOnly=true)
    public DateTime getDbCurrentTime() {
        PostgresQuery qry = queryFactory.query();

        return qry.singleResult(CURRENT_TIME);
    }
    
    @Transactional(readOnly=false)
    public void dbCleanup() {
        queryFactory.delete(QSupportVote.supportVote).execute();
        queryFactory.delete(QSupportVoteBatch.supportVoteBatch).execute();
        queryFactory.delete(initiativeAuthor).execute();
        queryFactory.delete(QInitiativeSupportVoteDay.initiativeSupportVoteDay).execute();
        queryFactory.delete(QReviewHistory.reviewHistory).execute();
        queryFactory.delete(QFollowInitiative.followInitiative).execute();
        queryFactory.delete(QInitiative.initiative).execute();
        queryFactory.delete(QInituser.inituser).execute();
        queryFactory.delete(QInfoText.infoText).execute();
    }
    
    @Transactional(readOnly=false)
    public Long createTestUser() {
        UserDaoImpl userDao = new UserDaoImpl(queryFactory);
        return userDao.register("TEST"+atomicInteger.incrementAndGet(), new DateTime(), "Test", "User", DOB);
    }

    @Transactional(readOnly=false)
    public Long createOMTestUserWithHash(String ssn) {
        UserDaoImpl userDao = new UserDaoImpl(queryFactory);
        String ssnHash = encryptionService.registeredUserHash(ssn);
        Long id = userDao.register(ssnHash, new DateTime(), "OM Test", "User", DOB);
        userDao.setUserRoles(id, false, true);
        return id;
    }

    @Transactional(readOnly=false)
    public Long createVRKTestUserWithHash(String ssn) {
        UserDaoImpl userDao = new UserDaoImpl(queryFactory);
        String ssnHash = encryptionService.registeredUserHash(ssn);
        Long id = userDao.register(ssnHash, new DateTime(), "VRK Test", "User", DOB);
        userDao.setUserRoles(id, true, false);
        return id;
    }

    @Transactional(readOnly=false)
    public void timeMachine(Long initiativeId, ReadablePeriod elapsedTimePeriod) {
        InitiativeDaoImpl initiativeDao = new InitiativeDaoImpl(queryFactory);
        InitiativeManagement initiative = initiativeDao.getInitiativeForManagement(initiativeId, true);
        timeMachine(initiative, elapsedTimePeriod);
        queryFactory
            .update(qInitiative)
            .set(qInitiative.startdate, initiative.getStartDate())
            .set(qInitiative.enddate, initiative.getEndDate())
            .set(qInitiative.verified, initiative.getVerified())
            .set(qInitiative.statedate, initiative.getStateDate())
            .set(qInitiative.supportstatementsremoved, initiative.getSupportStatementsRemoved())
            .where(qInitiative.id.eq(initiativeId))
            .execute();
    }
    
    private void timeMachine(InitiativeInfo initiative, ReadablePeriod elapsedTimePeriod) {
        // dates
        initiative.setStartDate(timeMachine(initiative.getStartDate(), elapsedTimePeriod));
        initiative.assignEndDate(timeMachine(initiative.getEndDate(), elapsedTimePeriod));
        initiative.setVerified(timeMachine(initiative.getVerified(), elapsedTimePeriod));
        // timestamps
        initiative.assignStateDate(timeMachine(initiative.getStateDate(), elapsedTimePeriod));
        initiative.assignSupportStatementsRemoved(timeMachine(initiative.getSupportStatementsRemoved(), elapsedTimePeriod));
        //  modified timestamp  <-- currently it is not necessary to change this
    }

    private LocalDate timeMachine(LocalDate originalDate, ReadablePeriod elapsedTimePeriod) {
        return originalDate != null ? originalDate.minus(elapsedTimePeriod): null; 
    }
    private DateTime timeMachine(DateTime originalDate, ReadablePeriod elapsedTimePeriod) {
        return originalDate != null ? originalDate.minus(elapsedTimePeriod): null; 
    }
    
    public static LocalizedString createDefaultMunicipality() {
        return asLocalizedString("Helsinki", "Helsingfors");
    }

    @Transactional
    public Long createRunningPublicInitiative(Long userId, String name) {
        return create(new InitiativeDraft(userId)
                .withName(name)
                .withState(InitiativeState.ACCEPTED)
                .withSupportCount(10000)
                .isRunning()
        );
    }

    @Transactional
    // This should be used for testing purposes only, due it changes fields that the any user should not be able to change.
    public void updateForTesting(InitiativeManagement initiative) {
        queryFactory.update(qInitiative).set(qInitiative.modifierId, initiative.getModifierId())
                .set(qInitiative.acceptanceidentifier, initiative.getAcceptanceIdentifier())
                .set(qInitiative.modified, CURRENT_TIME)
                .set(qInitiative.state, initiative.getState())
                .set(qInitiative.nameFi, initiative.getName().getFi())
                .set(qInitiative.nameSv, initiative.getName().getSv())
                .set(qInitiative.startdate, initiative.getStartDate())
                .set(qInitiative.enddate, initiative.getEndDate())
                .set(qInitiative.proposaltype, initiative.getProposalType())
                .set(qInitiative.proposalFi, initiative.getProposal().getFi())
                .set(qInitiative.proposalSv, initiative.getProposal().getSv())
                .set(qInitiative.rationaleFi, initiative.getRationale().getFi())
                .set(qInitiative.rationaleSv, initiative.getRationale().getSv())
                .set(qInitiative.primarylanguage, initiative.getPrimaryLanguage())
                .set(qInitiative.financialsupport, initiative.isFinancialSupport())
                .set(qInitiative.supportstatementsonpaper, initiative.isSupportStatementsOnPaper())
                .set(qInitiative.supportstatementsinweb, initiative.isSupportStatementsInWeb())
                .set(qInitiative.externalsupportcount, initiative.getExternalSupportCount())
                .set(qInitiative.financialsupporturl, initiative.getFinancialSupportURL() == null ? null : initiative.getFinancialSupportURL().toString())
                .set(qInitiative.supportcount, initiative.getSupportCount())
                .where(qInitiative.id.eq(initiative.getId()))
                .execute();
    }

    @Transactional
    // Not that initiatives created with this function cannot be updated with updateForTesting() for some reason.
    // Other issues might also occur...?
    public Long create(InitiativeDraft initiativeDraft) {
        LocalDate startDate;
        LocalDate endDate;
        if (initiativeDraft.startTime != null) {
            startDate = initiativeDraft.startTime;
            endDate = initiativeDraft.endTime;
        }
        else if (initiativeDraft.running == null || !initiativeDraft.running) {
            startDate = getDbCurrentTime().minusDays(2).toLocalDate();
            endDate = startDate;
        }
        else {
            startDate = getDbCurrentTime().minusDays(1).toLocalDate();
            endDate = startDate.plusDays(2);
        }

        SQLInsertClause insert = queryFactory.insert(qInitiative)
                .set(qInitiative.startdate, startDate)
                .set(qInitiative.acceptanceidentifier, initiativeDraft.acceptedByOm ? "acceptance number" : null)
                .set(qInitiative.enddate, endDate)
                .set(qInitiative.state, initiativeDraft.state)
                .set(qInitiative.modifierId, initiativeDraft.representativeId)
                .set(qInitiative.supportcount, initiativeDraft.supportCount)
                .set(qInitiative.proposaltype, ProposalType.LAW)
                .set(qInitiative.nameFi, initiativeDraft.name)
                .set(qInitiative.rationaleFi, "rationale")
                .set(qInitiative.proposalFi, "proposal")
                .set(qInitiative.primarylanguage, LanguageCode.FI);

        if (initiativeDraft.hasDenormalizedSupportCounts) {
            insert.set(qInitiative.supportCountData, InitiativeDraft.DEFAULT_DENORMALIZED_SUPPORTCOUNT_DATA);
        }

        Long initiativeId = insert.executeWithKey(qInitiative.id);

        SQLInsertClause authorInsert = queryFactory.insert(initiativeAuthor)
                .set(initiativeAuthor.userId, initiativeDraft.representativeId)
                .set(initiativeAuthor.initiativeId, initiativeId)
                .set(initiativeAuthor.lastname, randomAlphabetic(10))
                .set(initiativeAuthor.firstnames, randomAlphabetic(10))
                .set(initiativeAuthor.homemunicipalityFi, "Helsinki")
                .set(initiativeAuthor.homemunicipalitySv, "Helsinki")
                .set(initiativeAuthor.role, AuthorRole.REPRESENTATIVE)
                .set(initiativeAuthor.confirmed, DateTime.now())
                .set(initiativeAuthor.initiator, false)
                .set(initiativeAuthor.phone, "040");

        if (initiativeDraft.representativeEmail.isPresent()) {
            authorInsert.set(initiativeAuthor.email, initiativeDraft.representativeEmail.get());
        }
        authorInsert.execute();

        return initiativeId;

    }

    @Transactional
    public void makeAuthorFor(Long initiativeId, String email, AuthorRole role, Long userId) {
        queryFactory.insert(initiativeAuthor)
                .set(initiativeAuthor.userId, userId)
                .set(initiativeAuthor.initiativeId, initiativeId)
                .set(initiativeAuthor.lastname, randomAlphabetic(10))
                .set(initiativeAuthor.firstnames, randomAlphabetic(10))
                .set(initiativeAuthor.homemunicipalityFi, "Helsinki")
                .set(initiativeAuthor.homemunicipalitySv, "Helsinki")
                .set(initiativeAuthor.role, role)
                .set(initiativeAuthor.confirmed, DateTime.now())
                .set(initiativeAuthor.initiator, role == AuthorRole.INITIATOR)
                .set(initiativeAuthor.email, email)
                .execute();
    }

    @Transactional(readOnly = false)
    public Long createInfoText(LanguageCode languageCode,
                               InfoTextCategory category,
                               int orderPosition,
                               String uri_fi,
                               String subject_fi,
                               String draft_subject_fi,
                               String text_fi,
                               String draft_fi,
                               DateTime modified,
                               String modifierName) {
        return createInfoText(languageCode, category, orderPosition, uri_fi, subject_fi, draft_subject_fi, text_fi, draft_fi, modified, modifierName, false);
    }

    @Transactional(readOnly = false)
    public Long createInfoText(LanguageCode languageCode,
                               InfoTextCategory category,
                               int orderPosition,
                               String uri_fi,
                               String subject_fi,
                               String draft_subject_fi,
                               String text_fi,
                               String draft_fi,
                               DateTime modified,
                               String modifierName,
                               boolean footerDisplay) {
        return queryFactory.insert(QInfoText.infoText)
                .set(QInfoText.infoText.languagecode, languageCode)
                .set(QInfoText.infoText.category, category)
                .set(QInfoText.infoText.draft, draft_fi)
                .set(QInfoText.infoText.uri, uri_fi)
                .set(QInfoText.infoText.publishedSubject, subject_fi)
                .set(QInfoText.infoText.draftSubject, draft_subject_fi)
                .set(QInfoText.infoText.published, text_fi)
                .set(QInfoText.infoText.orderposition, orderPosition)
                .set(QInfoText.infoText.modified, modified)
                .set(QInfoText.infoText.modifier, modifierName)
                .set(QInfoText.infoText.footerDisplay, footerDisplay)
                .executeWithKey(QInfoText.infoText.id);
    }

    @Transactional(readOnly = false)
    public void createSupport(Long initiativeId, LocalDate supportVoteDate) {
        SQLInsertClause insert = queryFactory.insert(QSupportVote.supportVote)
                .set(QSupportVote.supportVote.created, supportVoteDate.toDateTime(LocalTime.now()))
                .set(QSupportVote.supportVote.details, "anyDetails")
                .set(QSupportVote.supportVote.initiativeId, initiativeId)
                .set(QSupportVote.supportVote.supportid, RandomStringUtils.random(64));
        insert.execute();

    }

    @Transactional(readOnly = false)
    public void addFollower(Long initiative, String email) {
        queryFactory.insert(QFollowInitiative.followInitiative)
                .set(QFollowInitiative.followInitiative.initiativeId, initiative)
                .set(QFollowInitiative.followInitiative.email, email)
                .set(QFollowInitiative.followInitiative.unsubscribeHash, randomAlphabetic(20))
                .execute();

    }

    public static class InitiativeDraft {

        public static final String DEFAULT_NAME = "dummy name";
        public static final String DEFAULT_DENORMALIZED_SUPPORTCOUNT_DATA = "some-uninitialized-data";
        private final Optional<String> representativeEmail;

        private Long representativeId;
        private String name = DEFAULT_NAME;
        private InitiativeState state = InitiativeState.ACCEPTED;
        private Boolean running = true;
        private Integer supportCount = 0;
        private boolean acceptedByOm;
        private LocalDate startTime;
        private LocalDate endTime;
        private boolean hasDenormalizedSupportCounts = false;

        public InitiativeDraft(Long representativeId) {
            this.representativeId = representativeId;
            this.representativeEmail = Optional.absent();
        }

        public InitiativeDraft(Long representativeId, String representativeEmail) {
            this.representativeId = representativeId;
            this.representativeEmail = Optional.of(representativeEmail);
        }

        public InitiativeDraft withState(InitiativeState state) {
            this.state = state;
            return this;
        }

        public InitiativeDraft withName(String name) {
            this.name = name;
            return this;
        }

        public InitiativeDraft isRunning() {
            running = true;
            return this;
        }

        public InitiativeDraft isRunning(LocalDate from, LocalDate to) {
            this.startTime = from;
            this.endTime = to;
            return this;
        }

        public InitiativeDraft isEnded() {
            running = false;
            return this;
        }

        public InitiativeDraft notStarted() {
            this.running = null;
            return this;
        }


        public InitiativeDraft isAcceptedByOm() {
            this.acceptedByOm = true;
            return this;
        }

        public InitiativeDraft withSupportCount(int supportCount) {
            this.supportCount = supportCount;
            return this;
        }

        public InitiativeDraft withRandomDenormalizedSupportCount() {
            this.hasDenormalizedSupportCounts = true;
            return this;
        }
    }

}
