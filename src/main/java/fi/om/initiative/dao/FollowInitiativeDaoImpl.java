package fi.om.initiative.dao;

import com.mysema.query.sql.postgres.PostgresQueryFactory;
import fi.om.initiative.sql.QFollowInitiative;

import javax.annotation.Resource;
import java.util.Map;



public class FollowInitiativeDaoImpl implements  FollowInitiativeDao{
    @Resource
    PostgresQueryFactory queryFactory;

    @Override
    public void addFollow(Long initiativeId, String email, String hash) {
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
    public Map<String, String> listFollowers(Long initiativeId) {
        return queryFactory.from(QFollowInitiative.followInitiative)
                .where(QFollowInitiative.followInitiative.initiativeId.eq(initiativeId))
                .map(QFollowInitiative.followInitiative.email, QFollowInitiative.followInitiative.unsubscribeHash);
    }
}
