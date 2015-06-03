package fi.om.initiative.dao;

import com.mysema.query.Tuple;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.Expression;
import com.mysema.query.types.MappingProjection;
import fi.om.initiative.dto.ReviewHistoryRow;
import fi.om.initiative.util.ReviewHistoryType;

import javax.annotation.Resource;
import java.util.List;

import static fi.om.initiative.sql.QReviewHistory.reviewHistory;

public class ReviewHistoryDaoImpl implements ReviewHistoryDao {

    @Resource
    PostgresQueryFactory queryFactory;

    public void addRejected(Long initiativeId, String moderatorComment) {
        queryFactory.insert(reviewHistory)
                .set(reviewHistory.initiativeId, initiativeId)
                .set(reviewHistory.message, moderatorComment)
                .set(reviewHistory.type, ReviewHistoryType.REVIEW_REJECT)
                .executeWithKey(reviewHistory.id);

    }

    public void addAccepted(Long initiativeId, String moderatorComment){
        queryFactory.insert(reviewHistory)
                .set(reviewHistory.initiativeId, initiativeId)
                .set(reviewHistory.message, moderatorComment)
                .set(reviewHistory.type, ReviewHistoryType.REVIEW_ACCEPT)
                .executeWithKey(reviewHistory.id);
    }

    public void addReviewComment(Long initiativeId, String moderatorComment){
        queryFactory.insert(reviewHistory)
                .set(reviewHistory.initiativeId, initiativeId)
                .set(reviewHistory.message, moderatorComment)
                .set(reviewHistory.type, ReviewHistoryType.REVIEW_COMMENT)
                .executeWithKey(reviewHistory.id);
    }

    public void addReviewSent(Long initiativeId, String initiativeSnapshot){
        queryFactory.insert(reviewHistory)
                .set(reviewHistory.initiativeId, initiativeId)
                .set(reviewHistory.initiativeSnapshot, initiativeSnapshot)
                .set(reviewHistory.type, ReviewHistoryType.REVIEW_SENT)
                .executeWithKey(reviewHistory.id);
    }

    public List<ReviewHistoryRow> findReviewHistoriesAndCommentsOrderedByTime(Long initiativeId) {
        return queryFactory.from(reviewHistory)
                .where(reviewHistory.initiativeId.eq(initiativeId))
                .orderBy(reviewHistory.created.desc())
                .list(reviewHistoryRowWrapper);
    }

    public List<ReviewHistoryRow> findReviewHistoriesOrderedByTime(Long initiativeId){
        return queryFactory.from(reviewHistory)
                .where(reviewHistory.initiativeId.eq(initiativeId))
                .where(reviewHistory.type.ne(ReviewHistoryType.REVIEW_COMMENT))
                .orderBy(reviewHistory.created.desc())
                .list(reviewHistoryRowWrapper);
    }
    public static Expression<ReviewHistoryRow> reviewHistoryRowWrapper =
            new MappingProjection<ReviewHistoryRow>(ReviewHistoryRow.class, reviewHistory.all()) {

                @Override
                protected ReviewHistoryRow map(Tuple row) {
                    ReviewHistoryRow reviewHistoryRow = new ReviewHistoryRow();
                    reviewHistoryRow.setId(row.get(reviewHistory.id));
                    reviewHistoryRow.setCreated(row.get(reviewHistory.created));

                    if (row.get(reviewHistory.message) != null) {
                        reviewHistoryRow.setMessage(row.get(reviewHistory.message));
                    }
                    if (row.get(reviewHistory.initiativeSnapshot) != null) {
                        reviewHistoryRow.setSnapshot(row.get(reviewHistory.initiativeSnapshot));
                    }
                    reviewHistoryRow.setType(row.get(reviewHistory.type));
                    return reviewHistoryRow;
                }
            };

}
