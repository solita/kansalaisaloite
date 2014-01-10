package fi.om.initiative.util;

import com.google.common.base.Predicate;

import fi.om.initiative.dto.Deletable;

public class NotDeleted<T extends Deletable> implements Predicate<T> {

    @Override
    public boolean apply(T input) {
        return !input.isDeleted();
    }
    
    public static <U extends Deletable> NotDeleted<U> create() {
        return new NotDeleted<U>();
    }

}
