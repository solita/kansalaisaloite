package fi.om.initiative.dao;


import fi.om.initiative.dto.initiative.InitiativeState;
import fi.om.initiative.service.EmailSpyConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;


@Transactional(readOnly = false)
public class JdbcFollowInitiativeDaoTest extends EmailSpyConfiguration {

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

        String hash = RANDOM_HASH;
        followInitiativeDao.addFollow(initiativeId, TESTEMAIL, hash);

        assertThat(followInitiativeDao.listFollowers(initiativeId), hasSize(1));

    }

    @Test
    public void cant_follow_initiative_twice() {

        followInitiativeDao.addFollow(initiativeId, TESTEMAIL, RANDOM_HASH);

        try{
            followInitiativeDao.addFollow(initiativeId, TESTEMAIL, RANDOM_HASH);
        } catch (DuplicateException e) {

        }

        assertThat(followInitiativeDao.listFollowers(initiativeId), hasSize(1));
    }

    @Test
    public void remove_follow() {

        followInitiativeDao.addFollow(initiativeId, TESTEMAIL, RANDOM_HASH);

        assertThat(followInitiativeDao.listFollowers(initiativeId), hasSize(1));

        followInitiativeDao.removeFollow(RANDOM_HASH);

        assertThat(followInitiativeDao.listFollowers(initiativeId), hasSize(0));
    }
}
