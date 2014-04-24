package fi.om.initiative.service;

import fi.om.initiative.conf.IntegrationTestConfiguration;
import fi.om.initiative.dao.SupportVoteDao;
import fi.om.initiative.dao.TestHelper;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={IntegrationTestConfiguration.class})
public class JobExecutorTest {

    @Resource
    private JobExecutor jobExecutor;

    @Resource
    private SupportVoteDao supportVoteDao;

    @Resource
    private TestHelper testHelper;

    private Long testInitiative;

    @Before
    public void setup() {
        testHelper.dbCleanup();
        Long userId = testHelper.createTestUser();
        testInitiative = testHelper.createRunningPublicInitiative(userId, "Some initiative");
    }

    @Test
    public void stores_denormalized_support_counts_to_initiatives() {

        LocalDate longTimeAgo = new LocalDate(2000, 1, 15);
        testHelper.createSupport(testInitiative, longTimeAgo);
        testHelper.createSupport(testInitiative, longTimeAgo);
        testHelper.createSupport(testInitiative, longTimeAgo.minusDays(2));
        testHelper.createSupport(testInitiative, longTimeAgo.minusDays(1));

        jobExecutor.updateDenormalizedSupportCountForInitiatives();

        assertThat(supportVoteDao.getDernormalizedSupportCountData(testInitiative), is(
                "[{\"d\":\"2000-01-13\",\"n\":1},{\"d\":\"2000-01-14\",\"n\":1},{\"d\":\"2000-01-15\",\"n\":2}]"
        ));

    }

}
