package fi.om.initiative.dao;

import fi.om.initiative.conf.IntegrationTestConfiguration;
import fi.om.initiative.conf.IntegrationTestConfiguration;
import fi.om.initiative.dto.ReviewHistoryRow;
import fi.om.initiative.util.ReviewHistoryType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;

//import static fi.om.initiative.util.MaybeMatcher.isNotPresent;
//import static fi.om.initiative.util.MaybeMatcher.isPresent;
//import static org.hamcrest.Matchers.hasSize;
//import static org.hamcrest.Matchers.is;
import static fi.om.initiative.util.MaybeMatcher.isNotPresent;
import static fi.om.initiative.util.MaybeMatcher.isPresent;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={IntegrationTestConfiguration.class})
@Transactional(readOnly = false)
public class JdbcReviewHistoryDaoTest {

    @Resource
    TestHelper testHelper;

    @Resource
    ReviewHistoryDao reviewHistoryDao;

    private Long initiativeId;

    @Before
    public void setup() {
        testHelper.dbCleanup();
        Long userId = testHelper.createTestUser();
        initiativeId = testHelper.create(new TestHelper.InitiativeDraft(userId));
    }

    @Test
    public void add_rejected_review_history() {

        String moderatorComment = "Moderator comment";
        reviewHistoryDao.addRejected(initiativeId, moderatorComment);

        List<ReviewHistoryRow> histories = reviewHistoryDao.findReviewHistoriesOrderedByTime(initiativeId);

        assertThat(histories, hasSize(1));
        assertThat(histories.get(0).getMessage(), isPresent());
        assertThat(histories.get(0).getMessage().get(), is(moderatorComment));
        assertThat(histories.get(0).getType(), is(ReviewHistoryType.REVIEW_REJECT));
    }

    @Test
    public void add_accepted_review_history() {

        String moderatorComment = "Moderator comment";
        reviewHistoryDao.addAccepted(initiativeId, moderatorComment);

        List<ReviewHistoryRow> histories = reviewHistoryDao.findReviewHistoriesOrderedByTime(initiativeId);

        assertThat(histories, hasSize(1));
        assertThat(histories.get(0).getMessage(), isPresent());
        assertThat(histories.get(0).getMessage().get(), is(moderatorComment));
        assertThat(histories.get(0).getSnapshot(), isNotPresent());
        assertThat(histories.get(0).getType(), is(ReviewHistoryType.REVIEW_ACCEPT));
    }

    @Test
    public void add_send_to_review_history() {
        String initiativeSnapshot = "Some snapshot text";

        reviewHistoryDao.addReviewSent(initiativeId, initiativeSnapshot);

        List<ReviewHistoryRow> histories = reviewHistoryDao.findReviewHistoriesOrderedByTime(initiativeId);

        assertThat(histories, hasSize(1));
        assertThat(histories.get(0).getMessage(), isNotPresent());
        assertThat(histories.get(0).getSnapshot(), isPresent());
        assertThat(histories.get(0).getSnapshot().get(), is(initiativeSnapshot));
        assertThat(histories.get(0).getType(), is(ReviewHistoryType.REVIEW_SENT));
    }

    @Test
    public void added_review_comment_not_shown_on_review_history_list() {
        String message = "Some moderator comment";

        reviewHistoryDao.addReviewComment(initiativeId, message);

        assertThat(reviewHistoryDao.findReviewHistoriesOrderedByTime(initiativeId), hasSize(0));
    }

    @Test
    public void added_review_comment_is_shown_on_review_history_and_comment_list() {
        String message = "Some moderator comment";

        reviewHistoryDao.addReviewComment(initiativeId, message);

        List<ReviewHistoryRow> historiesAndComments = reviewHistoryDao.findReviewHistoriesAndCommentsOrderedByTime(initiativeId);
        assertThat(historiesAndComments, hasSize(1));
        assertThat(historiesAndComments.get(0).getType(), is(ReviewHistoryType.REVIEW_COMMENT));
        assertThat(historiesAndComments.get(0).getMessage(), isPresent());
        assertThat(historiesAndComments.get(0).getMessage().get(), is(message));
        assertThat(historiesAndComments.get(0).getSnapshot(), isNotPresent());

    }
}
