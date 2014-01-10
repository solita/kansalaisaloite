package fi.om.initiative.dto;

public class InfoTextFooterLink {

    String uri;
    String subject;

    public InfoTextFooterLink(String uri, String subject) {
        this.uri = uri;
        this.subject = subject;
    }

    public String getUri() {
        return uri;
    }

    public String getSubject() {
        return subject;
    }
}


