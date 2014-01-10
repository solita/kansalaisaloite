package fi.om.initiative.web;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.om.initiative.conf.WebTestConfiguration;
import fi.om.initiative.util.Locales;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={WebTestConfiguration.class})
public class MessageSourceTest {
    
    @Resource 
    protected MessageSource messageSource;

    @Test
    public void Load_Message() {
        assertEquals("Tallenna", messageSource.getMessage("action.save", null, Locales.LOCALE_FI));
    }
    
}
