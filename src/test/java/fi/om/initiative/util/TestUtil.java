package fi.om.initiative.util;

import org.hamcrest.Matcher;

import java.util.List;

import static org.junit.Assert.assertThat;

public class TestUtil {

    public static <T> void precondition(T actual, Matcher<? super T> matcher) {
        assertThat("Precondition failed", actual, matcher);
    }

    public static <T> String listValues(List<T> objects) {
        StringBuilder builder = new StringBuilder();
        for (T object : objects) {
            if (builder.length() != 0)
                builder.append(", ");
            builder.append(object.toString());
        }
        return builder.toString();

    }

}
