package fi.om.initiative.dto;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

public class InitURI {
    
    private static final Set<String> ALLOWED_SCHEMES = Sets.newHashSet(
                "http", 
                "https", 
                "ftp",
                "ftps"
            );

    private final URI uri;
    
    public InitURI(String uriString) {
        this.uri = validateAndNormalize(uriString);
    }

    public static URI validateAndNormalize(String uriString) {
        if (Strings.isNullOrEmpty(uriString)) {
            throw new IllegalArgumentException("Null or empty URI is not allowed");
        } else {
            try {
                URI uri = new URI(uriString);
                String scheme = uri.getScheme();
                if (scheme == null) {
                    return new URI("http://" + uriString.toString());
                } else {
                    if (!ALLOWED_SCHEMES.contains(scheme.toLowerCase())) {
                        throw new IllegalArgumentException("Illegal URI: " + uriString + ". Allowed schemes are http(s) and ftp(s).");
                    } else {
                        return uri;
                    }
                }
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
    
    public String toString() {
        return uri.toString();
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof InitURI) {
            return uri.equals(((InitURI)obj).uri);
        } else {
            return false;
        }
    }

}
