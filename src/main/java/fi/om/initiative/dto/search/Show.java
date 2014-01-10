package fi.om.initiative.dto.search;

public enum Show {

    waiting(false),
    running(false),
    ended(false),
    sentToParliament(false),
    canceled(false),
    all(false),

    // om view:
    preparation(true),
    review(true),
    omAll(true),
    omCanceled(true),
    closeToTermination(true);

    private boolean omOnly;

    Show(boolean omOnly) {
        this.omOnly = omOnly;
    }

    public boolean isOmOnly() {
        return omOnly;
    }
}
