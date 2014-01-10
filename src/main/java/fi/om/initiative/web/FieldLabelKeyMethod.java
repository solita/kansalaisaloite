package fi.om.initiative.web;

import java.util.List;
import java.util.regex.Pattern;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

public class FieldLabelKeyMethod implements TemplateMethodModel {

    private static final Pattern REMOVE_PATTERN = Pattern.compile("\\[.+\\]");
    
    public static final FieldLabelKeyMethod INSTANCE = new FieldLabelKeyMethod();
    
    @Override
    @SuppressWarnings("rawtypes")
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments.size() != 1) {
            throw new TemplateModelException("Expected one argument: path");
        }
        return pathToLabelKey(arguments.get(0).toString());
    }
    
    public static String pathToLabelKey(String path) {
        return REMOVE_PATTERN.matcher(path).replaceAll("");
    }

}
