package fi.om.initiative.dto;

public class Follower {

    public final String email;
    public final String unsubscribeHash;

    public Follower(String email, String unsubscribeHash) {
        this.email = email;
        this.unsubscribeHash = unsubscribeHash;
    }
}
