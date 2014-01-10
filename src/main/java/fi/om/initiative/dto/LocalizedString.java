package fi.om.initiative.dto;

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static fi.om.initiative.util.Identities.addToHash;
import static fi.om.initiative.util.Identities.initialHash;
import static fi.om.initiative.util.Identities.propertyEquals;
import static fi.om.initiative.util.Identities.typeEquals;

import java.util.Locale;

import fi.om.initiative.util.Locales;

public class LocalizedString implements Cloneable {

    private String fi;
    
    private String sv;

    public LocalizedString() {}
    
    public LocalizedString(String fi, String sv) {
        this.fi = fi;
        this.sv = sv;
    }

    public String getFi() {
        return fi;
    }

    public void setFi(String fi) {
        this.fi = emptyToNull(fi);
    }

    public String getSv() {
        return sv;
    }

    public void setSv(String sv) {
        this.sv = emptyToNull(sv);
    }
    
    public boolean hasTranslation(Locale locale) {
        return hasTranslation(locale.getLanguage());
    }
    
    public boolean hasTranslation(String lang) {
        if (Locales.FI.equalsIgnoreCase(lang)) {
            return fi != null;
        } else if (Locales.SV.equalsIgnoreCase(lang)) {
            return sv != null;
        } else {
            throw new IllegalArgumentException("Unknown lang: " + lang);
        }
    }

    public String getTranslation(Locale locale) {
        return getTranslation(locale.getLanguage());
    }

    public String getTranslation(String lang) {
        if (Locales.FI.equals(lang)) {
            return fi;
        } else if (Locales.SV.equals(lang)) {
            return sv;
        } else {
            return null;
        }
    }

    public boolean hasAnyTranslation() {
        return fi != null || sv != null;
    }

    @Override
    public boolean equals(Object obj) {
        LocalizedString other = typeEquals(this, obj);
        return other != null 
                && propertyEquals(this.fi, other.fi) 
                && propertyEquals(this.sv, other.sv);
    }
    
    @Override
    public int hashCode() {
        int hashCode = initialHash(fi);
        return addToHash(hashCode, sv);
    }

    @Override
    public String toString() {
        return (isNullOrEmpty(fi) ? "-" : fi) + " / " + (isNullOrEmpty(sv) ? "-" : sv);
    }
    
    @Override
    public LocalizedString clone() {
        try {
            return (LocalizedString) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
