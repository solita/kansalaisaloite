package fi.om.initiative.dao;


import java.util.List;

public interface FollowInitiativeDao {

    void addFollow(Long initiativeId, String email, String hash);

    void removeFollow(String hash);

    List<String> listFollowers(Long initiativeId);
}
