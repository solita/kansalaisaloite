package fi.om.initiative.validation;

import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

public class JSONPCallbackValidator {

    public static final String NAME = "([_$\\p{IsAlphabetic}][_$0-9\\p{IsAlphabetic}]*)";

    public static final String INDEX = "(?:(?:\\[[0-9]+\\])+)"; // numeric index
    
    public static final String ID = NAME + INDEX + "?"; // name + optional index

    public static final String PATH = "(?U)" + ID + "(?:\\." + ID + ")*";

    public static final Pattern PATH_PATTERN = Pattern.compile(PATH);
    
    public static final Set<String> RESERVED_WORDS = Collections.unmodifiableSet(Sets.newHashSet(
            "abstract", "boolean", "break", "byte", "case", "catch", "char", "class",
            "const", "continue", "debugger", "default", "delete", "do", "double",
            "else", "enum", "export", "extends", "false", "final", "finally", "float",
            "for", "function", "goto", "if", "implements", "import", "in", "instanceof",
            "int", "interface", "long", "native", "new", "null", "package", "private",
            "protected", "public", "return", "short", "static", "super", "switch",
            "synchronized", "this", "throw", "throws", "transient", "true", "try",
            "typeof", "var", "void", "volatile", "while", "with"
            ));

    public static boolean isValidJSONPCallback(String callback) {
        Matcher matcher = PATH_PATTERN.matcher(callback);
        if (matcher.matches()) {
            for (int i=1; i <= matcher.groupCount(); i++) {
                String name = matcher.group(i);
                if (!Strings.isNullOrEmpty(name) && RESERVED_WORDS.contains(name)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

}
