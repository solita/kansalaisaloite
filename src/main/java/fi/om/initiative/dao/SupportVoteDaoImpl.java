package fi.om.initiative.dao;

import com.mysema.commons.lang.Assert;
import com.mysema.query.Tuple;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.sql.postgres.PostgresQuery;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.Expression;
import com.mysema.query.types.ExpressionUtils;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.expr.DateExpression;
import com.mysema.query.types.expr.Wildcard;
import com.mysema.query.types.template.DateTemplate;
import fi.om.initiative.dto.SupportVote;
import fi.om.initiative.dto.SupportVoteBatch;
import fi.om.initiative.dto.initiative.InitiativeState;
import fi.om.initiative.sql.QInitiative;
import fi.om.initiative.sql.QSupportVote;
import fi.om.initiative.sql.QSupportVoteBatch;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

public class SupportVoteDaoImpl implements SupportVoteDao {
    
    private static final QInitiative qInitiative = QInitiative.initiative;
    
    private static final QSupportVote qSupportVote = QSupportVote.supportVote;
    
    private static final QSupportVoteBatch qBatch = QSupportVoteBatch.supportVoteBatch;

    private static final Expression<Long> supportVoteCountExpr = qSupportVote.supportid.count();
    
    private static final MappingProjection<SupportVote> voteMapping = 
            new MappingProjection<SupportVote>(SupportVote.class, qSupportVote.all()) {

                private static final long serialVersionUID = 1335562461010430935L;

                @Override
                protected SupportVote map(Tuple tuple) {
                    SupportVote vote = new SupportVote(
                            tuple.get(qSupportVote.initiativeId), 
                            tuple.get(qSupportVote.supportid), 
                            tuple.get(qSupportVote.details), 
                            tuple.get(qSupportVote.created)
                        );
                    vote.setVerificationBatch(tuple.get(qSupportVote.batchId));
                    return vote;
                }
            };
            
    private static final MappingProjection<SupportVoteBatch> batchWithVoteCountMapping = 
            new MappingProjection<SupportVoteBatch>(SupportVoteBatch.class, 
                    InitiativeDaoImpl.projection(supportVoteCountExpr, qBatch.all())) {

                        private static final long serialVersionUID = 1933394221571032539L;

                        @Override
                        protected SupportVoteBatch map(Tuple row) {
                            return new SupportVoteBatch(row.get(qBatch.id), row.get(qBatch.created), row.get(supportVoteCountExpr));
                        }
            };

    @Resource PostgresQueryFactory queryFactory;

    public SupportVoteDaoImpl() {}
    
    public SupportVoteDaoImpl(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @Transactional(readOnly=false)
    public void insertSupportVote(SupportVote vote) {
        // Insert vote
        SQLInsertClause insert = queryFactory.insert(qSupportVote)
                .set(qSupportVote.created, vote.getCreated())
                .set(qSupportVote.details, vote.getEncryptedDetails())
                .set(qSupportVote.initiativeId, vote.getInitiativeId())
                .set(qSupportVote.supportid, vote.getSupportId());
        insert.execute();
    }
    
    @Override
    @Transactional(readOnly=false)
    public void incrementSupportCount(Long initiativeId) {
        // Increment Initiative.supportCount
        SQLUpdateClause update = queryFactory.update(qInitiative)
                .set(qInitiative.supportcount, qInitiative.supportcount.add(1))
                .where(qInitiative.id.eq(initiativeId));
        update.execute();
    }
    
    @Override
    @Transactional(readOnly=true)
    public SupportVote getVote(Long initiativeId, String supportId) {
        PostgresQuery qry = queryFactory
                .from(qSupportVote)
                .where(qSupportVote.initiativeId.eq(initiativeId), qSupportVote.supportid.eq(supportId));
        return qry.uniqueResult(voteMapping);
    }

    @Override
    @Transactional(readOnly=false)
    public int createBatch(Long initiativeId) {
        // Insert batch
        Long batchId = queryFactory
                .insert(qBatch)
                .set(qBatch.initiativeId, initiativeId)
                .executeWithKey(qBatch.id);
        
        // Attach unsent votes to batch
        long batchSize = queryFactory
                .update(qSupportVote)
                .set(qSupportVote.batchId, batchId)
                .where(
                        qSupportVote.initiativeId.eq(initiativeId),
                        qSupportVote.batchId.isNull()
                ).execute();
        
        if (batchSize == 0) {
            throw new EmptyBatchException();
        }
        
        // Update initiative sentSupportCount
        queryFactory.update(qInitiative)
        .set(qInitiative.sentsupportcount, qInitiative.sentsupportcount.add(batchSize))
        .where(qInitiative.id.eq(initiativeId))
        .execute();
        
        return (int) batchSize;
    }

    @Override
    @Transactional(readOnly=true)
    public List<SupportVote> getSupportVotes(Long batchId) {
        return queryFactory.from(qSupportVote).where(qSupportVote.batchId.eq(batchId)).list(voteMapping);
    }

    @Override
    @Transactional(readOnly=true)
    public List<SupportVoteBatch> getSupportVoteBatches(Long initiativeId) {
        return queryFactory
                .from(qBatch)
                .leftJoin(qBatch._supportVoteBatchIdFk, qSupportVote)
                .orderBy(qBatch.id.asc())
                .groupBy(qBatch.id)
                .where(qBatch.initiativeId.eq(initiativeId)).list(batchWithVoteCountMapping);
    }

    @Override
    @Transactional(readOnly=false)
    public void removeSupportVotes(Long initiativeId, DateTime supportStatementsRemoved, Long userId) {
        Assert.notNull(initiativeId, "initiativeId");
        
        //remove all remaining invitations from initiative: unsent, unanswered and expired  
        queryFactory
                .delete(qSupportVote)
                .where(qSupportVote.initiativeId.eq(initiativeId))
                .execute();

        // Increment Initiative.supportCount
        queryFactory
        .update(qInitiative)
        .set(qInitiative.modifierId, userId)
        .set(qInitiative.supportstatementsremoved, supportStatementsRemoved)
        .where(qInitiative.id.eq(initiativeId))
        .execute();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<LocalDate, Long> getSupportVoteCountByDateUntil(Long initiativeId, LocalDate tillDay) {

        DateExpression<LocalDate> createDate = DateTemplate.create(LocalDate.class, "date({0})", qSupportVote.created);
        Expression<LocalDate> createDateAlias = ExpressionUtils.as(createDate, "createDate");

        return queryFactory
                .from(qSupportVote)
                .where(qSupportVote.initiativeId.eq(initiativeId))
                .where(createDate.loe(tillDay))
                .groupBy(createDateAlias)
                .map(createDateAlias, Wildcard.count);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveDenormalizedSupportCountData(Long initiativeid, String denormalizedData) {
        queryFactory.update(qInitiative)
                .set(qInitiative.supportCountData, denormalizedData)
                .where(qInitiative.id.eq(initiativeid))
                .execute();
    }

    @Override
    @Transactional(readOnly = true)
    public String getDernormalizedSupportCountData(Long initiativeId) {
        return queryFactory.from(qInitiative)
                .where(qInitiative.id.eq(initiativeId))
                .singleResult(qInitiative.supportCountData);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getInitiativeIdsForSupportVoteDenormalization(LocalDate runningTillDate) {
        return queryFactory.from(qInitiative)
                .where(qInitiative.state.in(InitiativeState.ACCEPTED, InitiativeState.DONE),
                        qInitiative.enddate.goe(runningTillDate).or(qInitiative.supportCountData.isNull()))
                .list(qInitiative.id);
    }
}
