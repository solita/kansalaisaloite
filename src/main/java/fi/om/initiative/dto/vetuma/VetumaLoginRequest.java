package fi.om.initiative.dto.vetuma;

import fi.om.initiative.util.VetumaMACStringBuilder;

public class VetumaLoginRequest extends VetumaRequest implements Cloneable {

    protected String EXTRADATA;
    
    public String getEXTRADATA() {
        return EXTRADATA;
    }

    public void setEXTRADATA(String EXTRADATA) {
        this.EXTRADATA = EXTRADATA;
    }

    @Override
    public String toMACString() {
        VetumaMACStringBuilder sb = new VetumaMACStringBuilder()
                .append(RCVID)
                .append(APPID)
                .append(TIMESTMP)
                .append(SO)
                .append(SOLIST)
                .append(TYPE)
                .append(AU)
                .append(LG)
                .append(RETURL)
                .append(CANURL)
                .append(ERRURL)
                .append(AP)
                // These are optional, but exist always in template, so they are needed also in MAC
                .append(EXTRADATA)
                .append(APPNAME)
                .append(TRID)
                ;

        return sb.toString();
    }
    
    @Override
    public VetumaLoginRequest clone() {
        try {
            return (VetumaLoginRequest) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

}
