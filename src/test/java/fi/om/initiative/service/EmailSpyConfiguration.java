package fi.om.initiative.service;

import com.google.common.collect.Lists;
import fi.om.initiative.conf.IntegrationTestConfiguration;
import fi.om.initiative.util.TaskExecutorAspect;
import mockit.Delegate;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.apache.commons.io.FileUtils;
import org.aspectj.util.FileUtil;
import org.joda.time.LocalDateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={IntegrationTestConfiguration.class})
public abstract class EmailSpyConfiguration {

    public static final int WAIT_FOR_EMAILS_TO_BE_SENT = 10000;

    @Resource
    private TaskExecutorAspect taskExecutorAspect;

    private List<EmailHelper> sentEmails = Lists.newArrayList();

    @Mocked EmailSender emailSender;

    private static final String EMAIL_TEMP_DIR = "target/test-emails/";

    @BeforeClass
    public static void removeSentEmailsFromFilesystem() throws IOException {
        FileUtils.forceMkdir(new File(EMAIL_TEMP_DIR));
        FileUtils.cleanDirectory(new File(EMAIL_TEMP_DIR));
    }

    @Before
    public void setupEmailSpy() throws InterruptedException, IOException, MessagingException {

        waitUntilQueueEmpty();

        new NonStrictExpectations() {{

            EmailSender.send((EmailHelper) withNotNull(), (JavaMailSenderImpl) withNotNull());
            result = new Delegate() {

                public void send(EmailHelper emailHelper, JavaMailSender javaMailSender) throws MessagingException {
                    sentEmails.add(emailHelper);
                }

            };

        }};

    }

    @After
    public void writeEmails() {
        clearAllSentEmails();
    }

    private void waitUntilQueueEmpty()  {
        int waitedForOldEmailsToBeSentInMillis = 0;
        while (taskExecutorAspect.getQueueLength() != 0) {

            if (waitedForOldEmailsToBeSentInMillis > WAIT_FOR_EMAILS_TO_BE_SENT) {
                throw new RuntimeException("There were still " + taskExecutorAspect.getQueueLength() + " emails to be sent after " + WAIT_FOR_EMAILS_TO_BE_SENT);
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            waitedForOldEmailsToBeSentInMillis += 10;
        }
    }

    protected List<EmailHelper> getAllSentEmails() {
        waitUntilQueueEmpty();
        return sentEmails;
    }

    protected void clearAllSentEmails() {
        waitUntilQueueEmpty();
        writeEmailsToHtml();
        sentEmails.clear();
    }

    protected  void assertSentEmail(String to, String subject, String ... content) {
        StringBuilder sentEmails = new StringBuilder();
        for (EmailHelper emailHelper : getAllSentEmails()) {
            if (emailHelper.subject.equals(subject)
                    && emailHelper.to.equals(to)) {
                for (String s : content) {
                    assertThat("Email PLAINTEXT content mismatch", emailHelper.text, containsString(s));
                    assertThat("Email HTML content mismatch", emailHelper.html, containsString(s));
                }

                return;
            }
            sentEmails
                    .append("\n")
                    .append(emailHelper.to)
                    .append(": ")
                    .append(emailHelper.subject);
        }

        fail("Email to " + to + " with subject '" + subject + "' not sent. Emails sent: " + sentEmails.toString());

    }

    protected void assertSentEmailCount(int i) {
        List<EmailHelper> allSentEmails = getAllSentEmails();
        assertThat(allSentEmails, hasSize(i));
    }

    private void writeEmailsToHtml() {
        for (EmailHelper sentEmail : sentEmails) {
            File file = new File(EMAIL_TEMP_DIR
                    + new LocalDateTime().toString("mmssSSS")
                    + "_"
                    + sentEmail.subject.replace("/", " - ")
                    + "_"
                    + sentEmail.to
                    + ".html");
            FileUtil.writeAsString(file, sentEmail.html);
        }


    }

}
