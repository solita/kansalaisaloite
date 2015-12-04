package fi.om.initiative.web;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import fi.om.initiative.StartJetty;
import fi.om.initiative.conf.PropertyNames;
import fi.om.initiative.conf.WebTestConfiguration;
import fi.om.initiative.dao.TestHelper;
import fi.om.initiative.util.Locales;
import org.eclipse.jetty.server.Server;
import org.joda.time.ReadablePeriod;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={WebTestConfiguration.class})
public abstract class WebTestBase {
    
    protected static final int PORT = 8095; // NOTE: must match port in test.properties/baseUrl
    
    @Resource 
    protected TestHelper testHelper;
    @Resource 
    protected MessageSource messageSource;

    protected Urls urls;
    
    protected WebDriver driver;
    
    @Inject 
    protected Environment env;
    
    private static Server jettyServer;
    
    protected static final String OM_USER_SSN = "010101-0001";
    protected static final String VRK_USER_SSN = "020202-0002";
    protected static final String INITIATOR_USER_SSN = "081181-9984";

    private PeriodFormatter periodFormatter = ISOPeriodFormat.standard();

    @BeforeClass
    public static synchronized void initialize() {
        if (jettyServer == null) {
            jettyServer = StartJetty.startService(PORT, "test,disableSecureCookie");
            try {
                while (!jettyServer.isStarted()) {
                    Thread.sleep(250);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    @Before
    public void init() {
        if (urls == null) {
            Urls.initUrls(env.getRequiredProperty(PropertyNames.baseURL)); 
            urls = Urls.FI;
        }

        String driverType = env.getProperty("test.web-driver", "hu");
        System.out.println("*** driverType = " + driverType);
        if ("ie".equals(driverType)) {
            driver = new InternetExplorerDriver();
            driver.get(urls.frontpage());
            driver.navigate().to("javascript:document.getElementById('overridelink').click()"); // to skip security certificate problem page
        }
        else if ("ff".equals(driverType)) {
            driver = new FirefoxDriver();
        }
//        else if ("safari".equals(driverType)) {
//            driver = new SafariDriver();
//        }
        else {
        	HtmlUnitDriver htmlUnitDriver =	new HtmlUnitDriver(BrowserVersion.FIREFOX_3_6);
            htmlUnitDriver.setJavascriptEnabled(true);
        	driver = htmlUnitDriver;
        }

        driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS); // default is 0!!!
        driver.manage().timeouts().setScriptTimeout(50, TimeUnit.SECONDS); // default is 0!!!
        //driver.manage().timeouts().pageLoadTimeout(50, TimeUnit.SECONDS); // default is 0!!!
        
        if (urls == null) {
            Urls.initUrls("https://localhost:" + PORT); 
            urls = Urls.FI;
        }
        testHelper.dbCleanup();
        Long userIdOM = testHelper.createOMTestUserWithHash(OM_USER_SSN);
        System.out.println("*** OM user " + OM_USER_SSN + " created with id " + userIdOM);
        Long userIdVRK = testHelper.createVRKTestUserWithHash(VRK_USER_SSN);
        System.out.println("*** VRK user " + VRK_USER_SSN + " created with id " + userIdVRK);
    }

    @After
    public void endTest() {
        if (driver != null) {
            driver.quit(); // Quits this driver, closing every associated window.
            driver = null;
        }
    }
    
    
    @AfterClass
    public static void destroy() {
        try {
            jettyServer.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void timeMachine(Long initiativeId, ReadablePeriod elapsedTimePeriod) {
        testHelper.timeMachine(initiativeId, elapsedTimePeriod);
        System.out.println("*** timeMachine " + initiativeId + ": " + elapsedTimePeriod);
    }

    public void timeMachine(Long initiativeId, String elapsedTimePeriod) {
        timeMachine(initiativeId, periodFormatter.parsePeriod(elapsedTimePeriod));
    }

    public void timeMachine(String viewUrl, String elapsedTimePeriod) {
        timeMachine(getInitiativeIdFromViewUrl(viewUrl), elapsedTimePeriod);
    }
    
    public Long getInitiativeIdFromViewUrl(String viewUrl) {
        String[] arr = viewUrl.split("/");
        String idStr = arr[arr.length -1];
        Long id = Long.parseLong(idStr);
        return id;
    }
    
    protected String getMessage(String code) {
        return getMessage(code, null);
    }

    protected String getMessage(String code, Object arg) {
        Object[] args = {arg};
        String text = messageSource.getMessage(code, args, Locales.LOCALE_FI);
        text = text.replace('\u00A0', ' '); //replace non breaking space with normal space, because it would be rendered to it
        text = text.trim();
        return text;
    }
    
    protected void open(String href) {
        driver.get(href);
    }

    protected void assertMsgContainedById(String className, String messageKey) {
        String text = getMessage(messageKey);
        assertTextContainedById(className, text);
    }
    
    protected void assertTextContainedById(String id, String text) {
        List<WebElement> elements = driver.findElements(By.id(id));
        for (WebElement element : elements) {
            assertNotNull(element); 
            String elementText = element.getText().trim();
            if (elementText.contains(text)) {
                return;
            }
        }
        System.out.println("--- assertTextContainedById --------------- " + id + ": " + text);
        for (WebElement element : elements) {
            System.out.println("*** '" + element.getText().trim() + "'");
        }
        fail(id + " id with text " + text + " not found");
    }

    protected void assertMsgContainedByClass(String className, String messageKey) {
        String text = getMessage(messageKey);
        assertTextContainedByClass(className, text);
    }
    
    protected void assertTextContainedByClass(String className, String text) {
        System.out.println("--- assertTextContainedByClass --------------- " + className + ": " + text);
        List<WebElement> elements = driver.findElements(By.className(className));
        for (WebElement element : elements) {
            assertNotNull(element); 
            String elementText = element.getText().trim();
            if (elementText.contains(text)) {
                return;
            }
        }
        System.out.println("--- assertTextContainedByClass --------------- " + className + ": " + text);
        for (WebElement element : elements) {
            System.out.println("*** '" + element.getText().trim() + "'");
        }
        fail(className + " class with text " + text + " not found");
    }

    protected void assertTextContainedByXPath(String xpathExpression, String text) {
        List<WebElement> elements = driver.findElements(By.xpath(xpathExpression));
        for (WebElement element : elements) {
            assertNotNull(element); 
            String elementText = element.getText().trim();
            if (elementText.contains(text)) {
                return;
            }
        }
        System.out.println("--- assertTextContainedByXPath --------------- " + xpathExpression + ": " + text);
        for (WebElement element : elements) {
            System.out.println("*** '" + element.getText().trim() + "'");
        }
        fail(xpathExpression + " xpath with text " + text + " not found");
    }
    
    protected void assertTextByTag(String tag, String text) {
        List<WebElement> elements = driver.findElements(By.tagName(tag));
        for (WebElement element : elements) {
            assertNotNull(element); 
            if (text.equals(element.getText().trim())) {
                return;
            }
        }
        System.out.println("--- assertTextByTag --------------- " + tag + ": " + text);
        for (WebElement element : elements) {
            System.out.println("*** '" + element.getText().trim() + "'");
        }
        fail(tag + " tag with text " + text + " not found");
    }

    protected void assertTitle(String text) {
        String title = driver.getTitle();
        if (text.equals(title.trim())) {
            return;
        }
        System.out.println("--- assertTitle --------------- : " + text);
        System.out.println("*** '" + title.trim() + "'");
        fail("title with text " + text + " not found");
    }
    
    protected void inputText(String fieldName, String text) {
        driver.findElement(By.name(fieldName)).sendKeys(text);
    }
    
    protected void inputTextByCSS(String css, String text) {
        driver.findElement(By.cssSelector(css)).sendKeys(text);
    }
    
    protected void clickByName(String name) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(By.name(name)));
        link.click();
    }
    
    protected void clickById(String id) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
        link.click();
    }

    protected void clickLinkContaining(String text) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(By.partialLinkText(text)));
        link.click();
    }
    
    protected WebElement getElemContaining(String text, String tagName) {
        List<WebElement> htmlElements = driver.findElements(By.tagName(tagName));
       
        for (WebElement e : htmlElements) {
          if (e.getText().contains(text)) {
            return e;
          }
        }
        throw new NullPointerException("Element not found with text: " + text);
    }


    protected String getPageUrl() {
        return driver.getCurrentUrl();
    }
    
    protected void assertValue(String fieldName, String value) {
//        WebDriverWait wait = new WebDriverWait(driver,10);
//        wait.until(pageContainsElement(By.name(fieldName)));

        assertEquals(value, driver.findElement(By.name(fieldName)).getAttribute("value"));
    }

    protected void wait100() {
        waitms(100);
    }
    protected synchronized void waitms(int timeout) {
        try {
            wait(timeout);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
