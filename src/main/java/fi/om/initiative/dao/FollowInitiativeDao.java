package fi.om.initiative.dao;


import fi.om.initiative.dto.Follower;

import java.util.List;

public interface FollowInitiativeDao {

    void addFollow(Long initiativeId, Follower follower);

    void removeFollow(String hash);

    List<Follower> listFollowers(Long initiativeId);
}
