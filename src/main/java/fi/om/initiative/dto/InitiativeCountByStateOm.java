package fi.om.initiative.dto;

public class InitiativeCountByStateOm {


    private long running;
    private long ended;
    private long sentToParliament;
    private long omCanceled;
    private long waiting;

    private long preparation;
    private long review;
    private long closeToTermination; // Do not count at getAll() because has duplicates

    public long getOmAll() {
        return running
                +ended
                +sentToParliament
                +omCanceled
                +waiting
                +preparation
                +review;
    }

    public long getRunning() {
        return running;
    }

    public InitiativeCountByStateOm setRunning(long running) {
        this.running = running;
        return this;
    }

    public long getEnded() {
        return ended;
    }

    public InitiativeCountByStateOm setEnded(long ended) {
        this.ended = ended;
        return this;
    }

    public long getSentToParliament() {
        return sentToParliament;
    }

    public InitiativeCountByStateOm setSentToParliament(long sentToParliament) {
        this.sentToParliament = sentToParliament;
        return this;
    }

    public long getOmCanceled() {
        return omCanceled;
    }

    public InitiativeCountByStateOm setOmCanceled(long omCanceled) {
        this.omCanceled = omCanceled;
        return this;
    }

    public long getWaiting() {
        return waiting;
    }

    public InitiativeCountByStateOm setWaiting(long waiting) {
        this.waiting = waiting;
        return this;
    }

    public long getPreparation() {
        return preparation;
    }

    public InitiativeCountByStateOm setPreparation(long preparation) {
        this.preparation = preparation;
        return this;
    }

    public long getReview() {
        return review;
    }

    public InitiativeCountByStateOm setReview(long review) {
        this.review = review;
        return this;
    }

    public long getCloseToTermination() {
        return closeToTermination;
    }

    public InitiativeCountByStateOm setCloseToTermination(long closeToTermination) {
        this.closeToTermination = closeToTermination;
        return this;
    }
}
