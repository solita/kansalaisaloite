package fi.om.initiative.dao;

public class DuplicateException extends RuntimeException {

    public DuplicateException(String s) {
        super(s);
    }
}
