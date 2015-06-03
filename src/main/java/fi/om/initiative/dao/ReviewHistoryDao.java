package fi.om.initiative.dao;

import fi.om.initiative.dto.ReviewHistoryRow;

import java.util.List;


public interface ReviewHistoryDao {

    void addRejected(Long initiativeId, String moderatorComment);

    void addAccepted(Long initiativeId, String moderatorComment);

    void addReviewComment(Long initiativeId, String message);

    List<ReviewHistoryRow> findReviewHistoriesOrderedByTime(Long initiativeId);

    void addReviewSent(Long initiativeId, String initiativeSnapshot);

    List<ReviewHistoryRow> findReviewHistoriesAndCommentsOrderedByTime(Long initiativeId);
}
