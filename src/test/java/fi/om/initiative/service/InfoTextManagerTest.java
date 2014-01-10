package fi.om.initiative.service;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class InfoTextManagerTest {

    private InfoTextManager infoTextManager;

    @Before
    public void setup() {
        infoTextManager = new InfoTextManager();
    }

    @Test
    public void write_and_get() {

        String text = "this is some long text that should be saved and loaded";

        infoTextManager.setNewsText(text);

        assertThat(infoTextManager.getNewsText(), is(text));

    }
}
