package fi.om.initiative.service;

import com.google.common.base.Strings;
import com.mysema.query.sql.postgres.PostgresQuery;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import fi.om.initiative.dao.InitiativeDao;
import fi.om.initiative.dao.UserDao;
import fi.om.initiative.dto.InitiativeSettings;
import fi.om.initiative.dto.User;
import fi.om.initiative.dto.author.Author;
import fi.om.initiative.dto.initiative.InitiativeManagement;
import fi.om.initiative.dto.initiative.InitiativeState;
import fi.om.initiative.sql.QInitiative;
import fi.om.initiative.sql.QInituser;
import fi.om.initiative.util.TestDataTemplates;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;

@Profile({"dev", "test", "vetumamock"})
public class TestDataServiceImpl implements TestDataService {

    @Resource UserDao userDao;
    @Resource InitiativeDao initiativeDao;
    @Resource EncryptionService encryptionService;
    @Resource InitiativeSettings initiativeSettings;

    @Resource PostgresQueryFactory queryFactory; // would be cleaner if this would be moved to a separete TestDataDao? Nevertheless this is just used in dev-mode.
    private static final QInituser qUser = QInituser.inituser;
    
    @Override
    @Transactional(readOnly=false)
    public void createTestUsersFromTemplates(List<User> userTemplates) {
        for (User user : userTemplates) {
            createTestUserWithHash(user);
        }
    }

    private Long createTestUserWithHash(User user) {
        String ssnHash = encryptionService.registeredUserHash(user.getSsn());
        Long id = getRegisteredUserId(ssnHash);
        if (id == null) {
            id = userDao.register(ssnHash, new DateTime(), user.getFirstNames(), user.getLastName(), user.getDateOfBirth());
        }
        if (user.isVrk() || user.isOm()) { //always update vrk/om roles, to make sure that also pre-existing users will have right roles
            userDao.setUserRoles(id, user.isVrk(), user.isOm());
        }
        return id;
    }

    @Override
    @Transactional(readOnly=false)
    public void createTestInitiativesFromTemplates(List<InitiativeManagement> initiatives, User currentUser, String initiatorEmail, String reserveEmail) {
        if (initiatives.size() == 0) {
            return; // nothing to create
        }
        
        Long reserveUserId;
        if (Strings.isNullOrEmpty(initiatorEmail) || Strings.isNullOrEmpty(reserveEmail)) {
            throw new IllegalArgumentException("Missing email address");
        }
        
        /* currentUserId = */ createTestUserWithHash(currentUser); 
        reserveUserId = createTestUserWithHash(TestDataTemplates.RESERVE_AUTHOR_USER);

        Author currentAuthor = new Author(currentUser);
        currentAuthor.setInitiator(true);
        currentAuthor.setRepresentative(true);
        currentAuthor.setReserve(false);
        currentAuthor.getContactInfo().setEmail(initiatorEmail);

        Author reserveAuthor = TestDataTemplates.RESERVE_AUTHOR;    //NOTE: not thread safe for static?
        reserveAuthor.assignUserId(reserveUserId);
        reserveAuthor.getContactInfo().setEmail(reserveEmail);

        for (InitiativeManagement initiative : initiatives) {
            createTestInitiativeFromTemplate(initiative, currentAuthor, reserveAuthor);
        }
    }
    
    private Long createTestInitiativeFromTemplate(InitiativeManagement initiative, Author currentAuthor, Author reserveAuthor) {
        initiative.assignEndDate(initiative.getStartDate(), initiativeSettings.getVotingDuration());
        Long id = initiativeDao.create(initiative, currentAuthor.getUserId());

        InitiativeManagement createdInitiative = initiativeDao.get(id);

        queryFactory.update(QInitiative.initiative)
                .set(QInitiative.initiative.supportcount, initiative.getSupportCount())
                .where(QInitiative.initiative.id.eq(createdInitiative.getId()))
                .execute();

        initiativeDao.insertAuthor(id, currentAuthor.getUserId(), currentAuthor);
        
        if (initiative.getState() != InitiativeState.DRAFT) {
            initiativeDao.insertAuthor(id, reserveAuthor.getUserId(), reserveAuthor);

            initiativeDao.updateInitiativeState(id, currentAuthor.getUserId(), initiative.getState(), null);
        }
        
        return id;
    }
    
    // ----------------------------------------------------------------

    public Long getRegisteredUserId(String ssnHash) {
        PostgresQuery qry = queryFactory.from(qUser)
                .where(qUser.hash.eq(ssnHash));

        return qry.uniqueResult(qUser.id);
    }

}
