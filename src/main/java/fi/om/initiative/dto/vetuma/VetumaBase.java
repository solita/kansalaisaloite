package fi.om.initiative.dto.vetuma;

import org.hibernate.validator.constraints.URL;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public abstract class VetumaBase {

    private static final DateTimeFormatter DTF = DateTimeFormat.forPattern("yyyyMMddHHmmssSSS");
    
    public static final String DELIM = "&";
    
    /**
     * 
     */
    protected String RCVID;
    
    /**
     * yyyyMMddHHmmssSSS
     */
    protected String TIMESTMP;
    
    /**
     * 
     */
    protected String SO;
    
    /**
     * Language (fi, sv, en)
     */
    protected String LG;
    
    /**
     * Return URL for authentication success
     */
    @URL(protocol="https")
    protected String RETURL;
    
    /**
     * Return URL for authentication failure
     */
    @URL(protocol="https")
    protected String CANURL;
    
    /**
     * Return URL for authentication error
     */
    @URL(protocol="https")
    protected String ERRURL;
    
    /**
     * 
     */
    protected String MAC;
    
    /**
     * 
     */
    protected String TRID;

    public String getRCVID() {
        return RCVID;
    }

    public void setRCVID(String RCVID) {
        this.RCVID = RCVID;
    }

    public String getTIMESTMP() {
        return TIMESTMP;
    }

    public void setTIMESTMP(String TIMESTMP) {
        this.TIMESTMP = TIMESTMP;
    }

    public String getSO() {
        return SO;
    }

    public void setSO(String SO) {
        this.SO = SO;
    }

    public String getLG() {
        return LG;
    }

    public void setLG(String LG) {
        this.LG = LG;
    }

    public String getRETURL() {
        return RETURL;
    }

    public void setRETURL(String RETURL) {
        this.RETURL = RETURL;
    }

    public String getCANURL() {
        return CANURL;
    }

    public void setCANURL(String CANURL) {
        this.CANURL = CANURL;
    }

    public String getERRURL() {
        return ERRURL;
    }

    public void setERRURL(String ERRURL) {
        this.ERRURL = ERRURL;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public String getTRID() {
        return TRID;
    }

    public void setTRID(String TRID) {
        this.TRID = TRID;
    }

    public void setTimestamp(DateTime timestamp) {
        setTIMESTMP(DTF.print(timestamp));
    }
    
    public DateTime getTimestamp() {
        return DTF.parseDateTime(TIMESTMP);
    }
    
    public abstract String toMACString();
    
    public String toString() {
        return toMACString();
    }
    
}
