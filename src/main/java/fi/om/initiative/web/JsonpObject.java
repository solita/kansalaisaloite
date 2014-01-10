package fi.om.initiative.web;

public final class JsonpObject<T> {

    private final String callback;
    
    private final T object;
    
    public JsonpObject(String callback, T object) {
        this.callback = callback;
        this.object = object;
    }
    
    public String getCallback() {
        return callback;
    }
    
    public T getObject() {
        return object;
    }

}
