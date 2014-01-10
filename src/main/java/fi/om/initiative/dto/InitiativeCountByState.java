package fi.om.initiative.dto;

public class InitiativeCountByState{

    long running;
    long ended;
    long sentToParliament;
    long canceled;
    long waiting;

    public long getWaiting() {
        return waiting;
    }

    public InitiativeCountByState setWaiting(long waiting) {
        this.waiting = waiting;
        return this;
    }

    public long getRunning() {
        return running;
    }

    public InitiativeCountByState setRunning(long running) {
        this.running = running;
        return this;
    }

    public long getEnded() {
        return ended;
    }

    public InitiativeCountByState setEnded(long ended) {
        this.ended = ended;
        return this;
    }

    public long getSentToParliament() {
        return sentToParliament;
    }

    public InitiativeCountByState setSentToParliament(long sendToParliament) {
        this.sentToParliament = sendToParliament;
        return this;
    }

    public InitiativeCountByState setCanceled(long canceled) {
        this.canceled = canceled;
        return this;
    }

    public long getCanceled() {
        return canceled;
    }

    public long getAll() {
        return running+ended+sentToParliament+canceled+waiting;
    }

}
