package fi.om.initiative.dto;

public class InfoTextSubject {
    private String uri;
    private String subject;
    private InfoTextCategory infoTextCategory;

    public InfoTextSubject(InfoTextCategory infoTextCategory, String uri, String subject) {
        this.infoTextCategory = infoTextCategory;
        this.uri = uri;
        this.subject = subject;
    }

    public String getUri() {
        return uri;
    }

    public String getSubject() {
        return subject;
    }

    public InfoTextCategory getInfoTextCategory() {
        return infoTextCategory;
    }
}
