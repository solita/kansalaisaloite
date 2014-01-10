package fi.om.initiative.dto.vetuma;


public abstract class VetumaRequest extends VetumaBase {
    
    public static enum Type {
        LOGIN
    }
    
    public static enum Action {
        EXTAUTH
        , CONFIRM
        , SIGNATURE
    }

    protected String APPID;

    protected String SOLIST;

    protected Type TYPE;
    
    protected Action AU;
    
    protected String AP;
    
    protected String APPNAME;
    
    public String getAPPID() {
        return APPID;
    }
    public void setAPPID(String APPID) {
        this.APPID = APPID;
    }
    public String getSOLIST() {
        return SOLIST;
    }
    public void setSOLIST(String SOLIST) {
        this.SOLIST = SOLIST;
    }
    public Type getTYPE() {
        return TYPE;
    }
    public void setTYPE(Type TYPE) {
        this.TYPE = TYPE;
    }
    public Action getAU() {
        return AU;
    }
    public void setAU(Action AU) {
        this.AU = AU;
    }
    public String getAP() {
        return AP;
    }
    public void setAP(String AP) {
        this.AP = AP;
    }
    public String getAPPNAME() {
        return APPNAME;
    }
    public void setAPPNAME(String APPNAME) {
        this.APPNAME = APPNAME;
    }
}
