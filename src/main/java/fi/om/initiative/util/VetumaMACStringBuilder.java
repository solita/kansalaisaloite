package fi.om.initiative.util;

public class VetumaMACStringBuilder {
    
    public static final String DELIM = "&";
    
    private final StringBuilder sb = new StringBuilder(128);
    
    public VetumaMACStringBuilder append(Object value) {
        if (value != null) {
            sb.append(value.toString());
        }
        sb.append(DELIM);
        return this;
    }
    
    public VetumaMACStringBuilder appendOptional(Object value) {
        if (value != null) {
            sb.append(value.toString());
            sb.append(DELIM);
        }
        return this;
    }
    
    public String toString() {
        return sb.toString();
    }

}
