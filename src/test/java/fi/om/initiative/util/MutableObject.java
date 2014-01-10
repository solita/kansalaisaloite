package fi.om.initiative.util;

public class MutableObject<T> {
    
    private T value;
    
    public static <D> MutableObject<D> create() {
        return new MutableObject<D>();
    }
    
    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return this.value;
    }

}
