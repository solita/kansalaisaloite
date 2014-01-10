package fi.om.initiative.web;

import fi.om.initiative.service.AccessDeniedException;

public class CSRFException extends AccessDeniedException {

    private static final long serialVersionUID = 2151739773568509601L;

    public CSRFException() {
        super();
    }

    public CSRFException(String message) {
        super(message);
    }

}
