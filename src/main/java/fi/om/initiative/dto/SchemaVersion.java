package fi.om.initiative.dto;

import org.joda.time.DateTime;

public class SchemaVersion {

    private String script;
    
    private DateTime executed;
    
    public SchemaVersion() {}

    public SchemaVersion(String script, DateTime executed) {
        this.script = script;
        this.executed = executed;
    }

    public String getScript() {
        return script;
    }

    public DateTime getExecuted() {
        return executed;
    }
    
}
