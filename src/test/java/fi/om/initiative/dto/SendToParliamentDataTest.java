package fi.om.initiative.dto;

import org.junit.Test;

import java.net.MalformedURLException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SendToParliamentDataTest {

    @Test
    public void set_url_adds_protocol() throws MalformedURLException {
        assertThat(parliamentDataWithURL("www.google.fi").getParliamentURL(), is("http://www.google.fi"));
    }

    @Test
    public void set_url_does_not_add_protocol_if_already_has() {
        assertThat(parliamentDataWithURL("http://www.google.fi").getParliamentURL(), is("http://www.google.fi"));
        assertThat(parliamentDataWithURL("https://www.google.fi").getParliamentURL(), is("https://www.google.fi"));
    }

    @Test
    public void invalid_urls_are_still_set() {
        String invalidUrl = "234234.234.234,5∞∞∞∞∞$$$%%ääää";
        assertThat(parliamentDataWithURL(invalidUrl).getParliamentURL(), is("http://" + invalidUrl));

    }

    private static SendToParliamentData parliamentDataWithURL(String parliamentURL) {
        SendToParliamentData data = new SendToParliamentData();
        data.setParliamentURL(parliamentURL);
        return data;
    }
}
