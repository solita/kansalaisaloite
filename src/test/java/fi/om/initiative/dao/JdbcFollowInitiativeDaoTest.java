package fi.om.initiative.dao;


import com.mysema.query.QueryException;
import fi.om.initiative.conf.IntegrationTestConfiguration;
import fi.om.initiative.dto.initiative.InitiativeState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={IntegrationTestConfiguration.class})
@Transactional(readOnly = false)
public class JdbcFollowInitiativeDaoTest {

    public static final String TESTEMAIL = "test@test.fi";
    public static final String RANDOM_HASH = "randomHash";

    @Resource
    private TestHelper testHelper;

    @Resource
    FollowInitiativeDao followInitiativeDao;
    private Long initiativeId;
    private Long userId;

    @Before
    public void setup() {
        testHelper.dbCleanup();
        userId = testHelper.createTestUser();
        initiativeId = testHelper.create(
                new TestHelper.InitiativeDraft(userId)
                        .withState(InitiativeState.ACCEPTED)
        );

    }

    @Test
    public void follow_initiative() {
        Map<String, String> followers = followInitiativeDao.listFollowers(initiativeId);
        int before = followers.size();
        String hash = RANDOM_HASH;
        followInitiativeDao.addFollow(initiativeId, TESTEMAIL, hash);

        followers = followInitiativeDao.listFollowers(initiativeId);

        assertThat(followers.values(), hasSize(before +1));

    }

    @Test
    public void cant_follow_initiative_twice() {
        Map<String, String> followers = followInitiativeDao.listFollowers(initiativeId);
        int before = followers.size();

        followInitiativeDao.addFollow(initiativeId, TESTEMAIL, RANDOM_HASH);
        followers = followInitiativeDao.listFollowers(initiativeId);

        try{

            followInitiativeDao.addFollow(initiativeId, TESTEMAIL, RANDOM_HASH);
            followers = followInitiativeDao.listFollowers(initiativeId);
        } catch (QueryException e) {
            e.printStackTrace();
        }finally {
            assertThat(followers.values(), hasSize(before +1));
        }



    }

    @Test
    public void remove_follow() {
        Map<String, String> followers = followInitiativeDao.listFollowers(initiativeId);
        int before = followers.size();

        followInitiativeDao.addFollow(initiativeId, TESTEMAIL, RANDOM_HASH);

        followers = followInitiativeDao.listFollowers(initiativeId);

        assertThat(followers.values(), hasSize(before +1));

        followInitiativeDao.removeFollow(followers.get(TESTEMAIL));

        followers = followInitiativeDao.listFollowers(initiativeId);

        assertThat(followers.values(), hasSize(before));
        assertThat(followers.values(), not(contains(TESTEMAIL)));
    }
}
