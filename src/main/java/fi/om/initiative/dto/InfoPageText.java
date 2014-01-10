package fi.om.initiative.dto;

import fi.om.initiative.util.InfoTextHtmlSanitizer;
import org.joda.time.DateTime;

public class InfoPageText {
    private final String uri;
    private final String subject;
    private final String content;
    private final DateTime modifyTime;
    private final String modifierName;


    private InfoPageText(String uri, String modifierName, DateTime modifyTime, String text, String subject) {
        this.uri = uri;
        this.modifierName = modifierName;
        this.modifyTime = modifyTime;
        this.content = text;
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public DateTime getModifyTime() {
        return modifyTime;
    }

    public String getModifierName() {
        return modifierName;
    }

    public static Builder builder(String uri) {
        return new Builder(uri);
    }

    public String getUri() {
        return uri;
    }

    public static class Builder {

        private String uri;
        private String subject;
        private String content;
        private DateTime modifyTime;
        private String creatorName;

        public Builder(String uri) {
            this.uri = uri;
        }

        public Builder withText(String subject, String content) {
            this.content = InfoTextHtmlSanitizer.sanitize(content);
            this.subject = subject;
            return this;
        }

        public Builder withModifier(String creatorName, DateTime modifyTime) {
            this.creatorName = creatorName;
            this.modifyTime = modifyTime;
            return this;
        }

        public InfoPageText build() {
            return new InfoPageText(uri, creatorName, modifyTime, content, subject);
        }

    }
}
