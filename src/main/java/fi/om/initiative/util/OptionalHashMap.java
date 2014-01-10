package fi.om.initiative.util;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

/**
 * Optional hashmap. Use in cases where it's highly possible that the key is not found from the
 * map and the user of the map was probably not prepared for that.
 *
 * @param <T>
 * @param <E>
 */
public class OptionalHashMap<T, E>{

    Map<T, E> hashMap;

    public OptionalHashMap() {
        hashMap = Maps.newHashMap();
    }
    public OptionalHashMap(Map<T, E> originalMap) {
        hashMap = originalMap;
    }

    public int size() {
        return hashMap.size();
    }

    public boolean isEmpty() {
        return hashMap.isEmpty();
    }

    public Optional<E> get(T key) {
        E maybeKey = hashMap.get(key);
        return Optional.fromNullable(maybeKey);
    }

    public E put(T key, E value) {
        if (value == null)
            return value;
        hashMap.put(key, value);
        return value;
    }

    public void clear() {
        hashMap.clear();
    }

    public Collection<E> values() {
        return hashMap.values();
    }
}
