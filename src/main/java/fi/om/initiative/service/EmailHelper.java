package fi.om.initiative.service;


public class EmailHelper {
    public String to;
    public String from;
    public String replyTo;
    public String subject;
    public String text;
    public String html;

    public void setTo(String to) {
        this.to = to;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }


    public void setText(String text, String html) {
        this.text = text;
        this.html = html;
    }



}
