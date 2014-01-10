package fi.om.initiative.dto;

import java.beans.PropertyEditorSupport;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class LocalDateEditor extends PropertyEditorSupport {

    final String datePattern;
    
    public LocalDateEditor(String datePattern) {
        this.datePattern = datePattern;
    }

    @Override
    public void setAsText(String text) {
        if (text == null) {
            setValue(null);
        } else {
            String value = text.trim();
            if ("".equals(value)) {
                setValue(null);
            } else {
                DateTimeFormatter jodaDateFormatter = getJodaDateFormatter();
                LocalDate ret = jodaDateFormatter.parseLocalDate(text);
                setValue(ret);
            }
        }
    }

    @Override
    public String getAsText() {
         Object value = getValue();
         if (value == null) {
             return "";
         }
         else {
             DateTimeFormatter jodaDateFormatter = getJodaDateFormatter();
             LocalDate ld = (LocalDate)value;
             return jodaDateFormatter.print(ld.toDate().getTime());
         }
    }

    private DateTimeFormatter getJodaDateFormatter() {
        return DateTimeFormat.forPattern(datePattern);
    }
}
