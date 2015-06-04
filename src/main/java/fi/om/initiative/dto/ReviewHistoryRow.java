package fi.om.initiative.dto;


import fi.om.initiative.util.Maybe;
import fi.om.initiative.util.ReviewHistoryType;
import org.joda.time.DateTime;

public class ReviewHistoryRow {

    private Long id;
    private ReviewHistoryType type;
    private DateTime created;
    private Maybe<String> message;
    private Maybe<String> snapshot;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReviewHistoryType getType() {
        return type;
    }

    public void setType(ReviewHistoryType type) {
        this.type = type;
    }

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }

    public Maybe<String> getMessage() {
        return message;
    }

    public void setMessage(Maybe<String> message) {
        this.message = message;
    }

    public Maybe<String> getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(Maybe<String> snapshot) {
        this.snapshot = snapshot;
    }
}
