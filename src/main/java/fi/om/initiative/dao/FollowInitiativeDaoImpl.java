package fi.om.initiative.dao;

import com.mysema.query.Tuple;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.MappingProjection;
import fi.om.initiative.dto.Follower;
import fi.om.initiative.sql.QFollowInitiative;

import javax.annotation.Resource;
import java.util.List;


public class FollowInitiativeDaoImpl implements  FollowInitiativeDao{

    @Resource
    PostgresQueryFactory queryFactory;

    @Override
    public void addFollow(Long initiativeId, Follower follower) {

        if (queryFactory.from(QFollowInitiative.followInitiative)
                .where(QFollowInitiative.followInitiative.email.eq(follower.email))
                .where(QFollowInitiative.followInitiative.initiativeId.eq(initiativeId))
                .count() != 0) {
            throw new DuplicateException("Already following initiative.");
        }

        queryFactory.insert(QFollowInitiative.followInitiative)
                .set(QFollowInitiative.followInitiative.email, follower.email)
                .set(QFollowInitiative.followInitiative.unsubscribeHash, follower.unsubscribeHash)
                .set(QFollowInitiative.followInitiative.initiativeId, initiativeId)
                .execute();
    }

    @Override
    public void removeFollow(Long initiativeId, String hash) {
        queryFactory.delete(QFollowInitiative.followInitiative)
                .where(QFollowInitiative.followInitiative.unsubscribeHash.eq(hash))
                .where(QFollowInitiative.followInitiative.initiativeId.eq(initiativeId))
                .execute();

    }

    @Override
    public List<Follower> listFollowers(Long initiativeId) {
        return queryFactory.from(QFollowInitiative.followInitiative)
                .where(QFollowInitiative.followInitiative.initiativeId.eq(initiativeId))
                .list(followerMapping);
    }

    private static final MappingProjection<Follower> followerMapping =
            new MappingProjection<Follower>(Follower.class, QFollowInitiative.followInitiative.all()) {
                @Override
                protected Follower map(Tuple row) {
                    return new Follower(row.get(QFollowInitiative.followInitiative.email),
                            row.get(QFollowInitiative.followInitiative.unsubscribeHash));
                }
            };
}
