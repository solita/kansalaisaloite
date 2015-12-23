package fi.om.initiative.dao;

import com.mysema.query.sql.postgres.PostgresQueryFactory;
import fi.om.initiative.sql.QFollowInitiative;

import javax.annotation.Resource;
import java.util.List;


public class FollowInitiativeDaoImpl implements  FollowInitiativeDao{
    @Resource
    PostgresQueryFactory queryFactory;

    @Override
    public void addFollow(Long initiativeId, String email, String hash) {

        if (queryFactory.from(QFollowInitiative.followInitiative)
                .where(QFollowInitiative.followInitiative.email.eq(email))
                .where(QFollowInitiative.followInitiative.initiativeId.eq(initiativeId))
                .count() != 0) {
            throw new DuplicateException("Already following initiative.");
        }

        queryFactory.insert(QFollowInitiative.followInitiative)
                .set(QFollowInitiative.followInitiative.email, email)
                .set(QFollowInitiative.followInitiative.unsubscribeHash, hash)
                .set(QFollowInitiative.followInitiative.initiativeId, initiativeId)
                .execute();
    }

    @Override
    public void removeFollow(String hash) {
        queryFactory.delete(QFollowInitiative.followInitiative)
                .where(QFollowInitiative.followInitiative.unsubscribeHash.eq(hash))
                .execute();

    }

    @Override
    public List<String> listFollowers(Long initiativeId) {
        return queryFactory.from(QFollowInitiative.followInitiative)
                .where(QFollowInitiative.followInitiative.initiativeId.eq(initiativeId))
                .list(QFollowInitiative.followInitiative.email);
    }
}
