package fi.om.initiative.dto;

import java.beans.PropertyEditorSupport;

import com.google.common.base.Strings;


public class InitURIEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (Strings.isNullOrEmpty(text)) {
            setValue(null);
        } else {
            setValue(new InitURI(text));
        }
    }

    
}
