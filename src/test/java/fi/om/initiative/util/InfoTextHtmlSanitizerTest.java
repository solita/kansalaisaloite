package fi.om.initiative.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

public class InfoTextHtmlSanitizerTest {

    @Test
    public void sanitizer_accepts_links() throws Exception {
        assertThat(InfoTextHtmlSanitizer.sanitize("<a href=\"link\">link</a>"), containsString("<a href"));
    }

    @Test
    public void sanitizer_accepts_images() throws Exception {
        assertThat(InfoTextHtmlSanitizer.sanitize("<img src=\"link\"/>"), containsString("<img src"));
    }

    @Test
    public void sanitizer_accepts_headers() throws Exception {
        assertThat(InfoTextHtmlSanitizer.sanitize("<h1>"), containsString("<h1>"));
        assertThat(InfoTextHtmlSanitizer.sanitize("<h2"), containsString("<h2>"));
        assertThat(InfoTextHtmlSanitizer.sanitize("<h3"), containsString("<h3>"));
        assertThat(InfoTextHtmlSanitizer.sanitize("<h4>"), containsString("<h4>"));
    }

    @Test
    public void sanitizer_does_not_accept_script_tag() throws Exception {
        assertThat(InfoTextHtmlSanitizer.sanitize("<script>"), not(containsString("script")));
    }

    @Test
    public void sanitizer_is_ok_with_custom_characters() {
        assertThat(InfoTextHtmlSanitizer.sanitize("<img src=\"1.jpg\" alt=\"äöå\" title=\"äöå\" />)"), is("<img src=\"1.jpg\" alt=\"äöå\" title=\"äöå\" />)"));
    }
}
