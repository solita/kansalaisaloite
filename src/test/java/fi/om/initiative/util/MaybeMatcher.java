package fi.om.initiative.util;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public abstract class MaybeMatcher extends TypeSafeMatcher<Maybe<?>> {

    public static Matcher<? super Maybe<?>> isPresent() {
            return new MaybeMatcher() {

                @Override
                protected boolean matchesSafely(Maybe<?> item) {
                    return isMaybeObject(item) && item.isPresent();
                }

                @Override
                protected void describeMismatchSafely(Maybe<?> item, Description mismatchDescription) {
                    mismatchDescription.appendText("was isNotPresent()");
                }

                @Override
                public void describeTo(Description description) {
                    description.appendText("isPresent()");
                }
            };
        }

    public static Matcher<? super Maybe<?>> isNotPresent() {
        return new MaybeMatcher() {
            @Override
            protected boolean matchesSafely(Maybe<?> item) {
                return isMaybeObject(item) && item.isNotPresent();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("isNotPresent()");
            }

            @Override
            protected void describeMismatchSafely(Maybe<?> item, Description mismatchDescription) {
                mismatchDescription.appendText("was isPresent()");
            }
        };
    }

    private static boolean isMaybeObject(Maybe<?> item) {
        return item != null;
    }
}
