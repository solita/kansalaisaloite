package fi.om.initiative.web;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SummaryMethod implements TemplateMethodModel {

    private final int minLength = 500;
    
    private final int maxLength = 750;
    
    private static String ellipsis = "...";
    
    // NewLine + optional whitespace + NewLine
    private static final Pattern PARAGRAPH = Pattern.compile("\n[ \t\\x0B\f\r]*\n");
    
    private static final Pattern SENTENCE = Pattern.compile("[\\.\\?!]");
    
    private static final Pattern CLAUSE = Pattern.compile("[,;:\n]");
    
    private static final Pattern BOUNDARY = Pattern.compile("\\b");

    public static final TemplateMethodModel INSTANCE = new SummaryMethod();
    
    @Override
    @SuppressWarnings("rawtypes")
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments.size() != 1) {
            throw new TemplateModelException("Expected one argument: path");
        }
        return summary(Objects.toString(arguments.get(0), ""), minLength, maxLength); 
    }

    public static String summary(final String str, final int minLength, final int maxLength) {
        if (maxLength < minLength) {
            throw new IllegalArgumentException("maxLength cannot be less than maxLength");
        }
        else if (str == null) {
            return null;
        } 
        else if (str.length() <= maxLength) {
            return str;
        } 
        else {
            // For now we ignore code points and rely on plain chars
            final String summaryPrefix = str.substring(0, minLength);
            final String summarySuffix = str.substring(minLength, maxLength);

            String summary = getFirst(summarySuffix, PARAGRAPH, false); 
            if (summary != null) {
                return summaryPrefix + summary;
            } 

            summary = getFirst(summarySuffix, SENTENCE, true);
            if (summary != null) {
                return summaryPrefix + summary;
            } 

            summary = getFirst(summarySuffix, CLAUSE, true);
            if (summary != null) {
                return summaryPrefix + summary + ellipsis;
            } 

            summary = getFirst(summarySuffix, BOUNDARY, false);
            if (summary != null) {
                return summaryPrefix + summary + ellipsis;
            }

            return str.substring(0, maxLength)  + ellipsis ;
        }
    }

    private static String getFirst(String str, Pattern pattern, boolean includeMatch) {
        Matcher m = pattern.matcher(str);
        if (m.find()) {
            if (includeMatch) {
                return str.substring(0, m.end());
            } else {
                return str.substring(0, m.start());             
            }
        } else {
            return null;
        }
    }
    
}
