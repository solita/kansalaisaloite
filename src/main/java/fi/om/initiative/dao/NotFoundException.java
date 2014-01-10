package fi.om.initiative.dao;

public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 7842883381594745137L;

    public NotFoundException(String type, Object id) {
        super("" + type + "#" + id);
    }

}
