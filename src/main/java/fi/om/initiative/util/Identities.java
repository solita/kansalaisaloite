package fi.om.initiative.util;

import java.util.Objects;


/**
 * Utility for easy efficient hashCode and equals. There are hashCode-utilities such as
 * Objects (in java.util and Google Guava) but those rely on varargs which is ineffective.
 * These methods do not allocate memory (e.g. construct an array for varargs).
 * 
 * HashCode algorithm is essentially the same as with Objects that relies on Arrays.hashCode(Object[]).
 * 
 * Usage: 
 * <pre>
 * import static fi.om.initiative.util.Identities.*;
 * 
 * public class MyBean {
 * 
 *     ...
 *     
 *     {@literal @}Override
 *     public int hashCode() {
 *         int hashCode = initialHash(firstProperty);
 *         hashCode = addToHash(secondProperty);
 *         ...
 *         return addToHash(nthProperty);
 *     }
 *     
 *     {@literal @}Override
 *     public boolean equals(Object obj) {
 *         MyBean other = typeEquals(this, obj);
 *         return other != null
 *             && propertyEquals(this.firstProperty, other.firstProperty)
 *             && propertyEquals(this.secondProperty, other.secondProperty)
 *             ...
 *             && propertyEquals(this.nthProperty, other.nthProperty);
 *     }
 * }
 * </pre>
 * 
 * @author samppasa
 */
public class Identities {

    public static int initialHash(Object value) {
        return addToHash(1, value);
    }

    public static int addToHash(int hashCode, Object value) {
        return 31*hashCode + (value==null ? 0 : value.hashCode());
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T typeEquals(T thiz, Object obj) {
        return typeEquals(thiz, obj, (Class<T>) thiz.getClass());
    }
    
    @SuppressWarnings("unchecked")
    public static <T, U extends T> T typeEquals(U thiz, Object obj, Class<T> clazz) {
        if (thiz == obj || clazz.isInstance(obj)) {
            return (T) obj;
        } else {
            return null;
        }
    }
    
    public static boolean propertyEquals(Object a, Object b) {
        return Objects.equals(a, b);
    }

}
