package fi.om.initiative.util;

import org.owasp.html.Handler;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.HtmlSanitizer;
import org.owasp.html.HtmlSanitizer.Policy;
import org.owasp.html.HtmlStreamRenderer;

import java.io.IOException;

public class InfoTextHtmlSanitizer {

    private static Policy makePolicy(Appendable buffer) {
        final HtmlStreamRenderer renderer = HtmlStreamRenderer.create(
                buffer,
                new Handler<IOException>() {
                    public void handle(IOException ex) {
                        throw new RuntimeException(ex);
                    }
                },
                new Handler<String>() {
                    public void handle(String errorMessage) {
                        throw new RuntimeException(errorMessage);
                    }
                });

        return new HtmlPolicyBuilder()
                .allowElements( "h1", "h2", "h3", "h4", "p",
                        "ol", "li", "ul",
                        "i", "u", "b",
                        "blockquote",
                        "a", "br", "div", "img", "span")
                .allowAttributes("href").onElements("a")
                .allowAttributes("src").onElements("img")
                .allowAttributes("width").onElements("img")
                .allowAttributes("height").onElements("img")
                .allowAttributes("alt").onElements("img")
                .allowAttributes("class", "id", "title").globally()
                .allowStandardUrlProtocols()
//                .requireRelNofollowOnLinks()
//                .disallowElements("script")
                .build(renderer);
    }

    public static String sanitize(String html) {
        StringBuilder sb = new StringBuilder();

        HtmlSanitizer.sanitize(html, makePolicy(sb));

        return sb.toString();
    }
}
