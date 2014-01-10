package fi.om.initiative.dto.vetuma;

import static com.google.common.base.Strings.emptyToNull;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

import fi.om.initiative.util.VetumaMACStringBuilder;

public class VetumaLoginResponse extends VetumaResponse{

    private static final String NAME_REGEXP = "ETUNIMI=([^,]*), SUKUNIMI=(.*)";
    
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEXP);

    private static final String SSN_REGEXP = 
            "HETU=([0-9]{6}" + // Six digits
    		"[-Aa]" + // - or A
    		"[0-9]{3}" + // Three digits
    		"[A-Za-z0-9])"; // One letter or digit
    
    private static final Pattern SSN_PATTERN = Pattern.compile(SSN_REGEXP);
    
    protected String USERID;
    
    @javax.validation.constraints.Pattern(regexp=NAME_REGEXP)
    protected String SUBJECTDATA;
    
    protected String EXTRADATA;
    
    protected String VTJDATA;

    public String getUSERID() {
        return USERID;
    }

    public void setUSERID(String USERID) {
        this.USERID = USERID;
    }

    public String getSUBJECTDATA() {
        return SUBJECTDATA;
    }

    public void setSUBJECTDATA(String SUBJECTDATA) {
        this.SUBJECTDATA = SUBJECTDATA;
    }

    public String getEXTRADATA() {
        return EXTRADATA;
    }

    public void setEXTRADATA(String EXTRADATA) {
        this.EXTRADATA = EXTRADATA;
    }

    public String getVTJDATA() {
        return VTJDATA;
    }

    public void setVTJDATA(String VTJDATA) {
        this.VTJDATA = VTJDATA;
    }
    
    public String getVTJDataXML() {
        if (Strings.isNullOrEmpty(VTJDATA)) {
            return null;
        } else {
            try {
                return URLDecoder.decode(VTJDATA, "ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public String[] getNames() {
        String[] names = {null, null};
        if (!Strings.isNullOrEmpty(SUBJECTDATA)) {
            Matcher matcher = NAME_PATTERN.matcher(SUBJECTDATA);
            if (matcher.matches()) {
                names[0] = emptyToNull(matcher.group(1));
                names[1] = emptyToNull(matcher.group(2));
            }
        }
        return names;
    }
    
    public String getSsn() {
        String ssn = null;
        if (!Strings.isNullOrEmpty(EXTRADATA)) {
            Matcher matcher = SSN_PATTERN.matcher(EXTRADATA);
            if (matcher.matches()) {
                ssn = matcher.group(1).toUpperCase();
            }
        }
        return ssn;
    }

    @Override
    public String toMACString() {
        return new VetumaMACStringBuilder()
                .append(RCVID)
                .append(TIMESTMP)
                .append(SO)
                .append(USERID)
                .append(LG)
                .append(RETURL)
                .append(CANURL)
                .append(ERRURL)
                .append(SUBJECTDATA)
                .append(EXTRADATA)
                .append(STATUS)
                .appendOptional(TRID)
                .appendOptional(VTJDATA)
                .toString();
    }
    
}
