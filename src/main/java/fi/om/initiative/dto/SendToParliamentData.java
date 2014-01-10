package fi.om.initiative.dto;

import org.joda.time.LocalDate;

import javax.validation.constraints.NotNull;

import java.net.MalformedURLException;
import java.net.URL;

public class SendToParliamentData {

    @NotNull
    private LocalDate parliamentSentTime;

    @NotNull
    private String parliamentURL;

    @NotNull
    private String parliamentIdentifier;

    public LocalDate getParliamentSentTime() {
        return parliamentSentTime;
    }

    public void setParliamentSentTime(LocalDate parliamentSentTime) {
        this.parliamentSentTime = parliamentSentTime;
    }

    public String getParliamentURL() {
        return parliamentURL;
    }

    public void setParliamentURL(String parliamentURL) {
        if (hasNoProtocol(parliamentURL)) {
            this.parliamentURL = "http://" + parliamentURL;
        }
        else {
            this.parliamentURL = parliamentURL;
        }
    }

    private static boolean hasNoProtocol(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            return e.getMessage().contains("no protocol");
        }
        return false;
    }

    public String getParliamentIdentifier() {
        return parliamentIdentifier;
    }

    public void setParliamentIdentifier(String parliamentIdentifier) {
        this.parliamentIdentifier = parliamentIdentifier;
    }
}
