package fi.om.initiative.service;

public class AccessDeniedException extends RuntimeException {

    private static final long serialVersionUID = 4729051774126029713L;

    public AccessDeniedException() {
        super();
    }
    
    public AccessDeniedException(String msg) {
        super(msg);
    }
}
