package fi.om.initiative.service;

import com.google.common.collect.Lists;
import fi.om.initiative.conf.IntegrationTestConfiguration;
import fi.om.initiative.util.TaskExecutorAspect;
import jdk.nashorn.internal.ir.annotations.Ignore;
import mockit.Delegate;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={IntegrationTestConfiguration.class})
@Ignore
public abstract class EmailSpyConfiguration {

    public static final int WAIT_FOR_EMAILS_TO_BE_SENT = 10000;

    @Resource
    private TaskExecutorAspect taskExecutorAspect;

    @Mocked
    JavaMailSenderImpl javaMailSender;

    private List<MimeMessage> sentEmails = Lists.newArrayList();

    @Before
    public void setupEmailSpy() throws InterruptedException {

        waitUntilQueueEmpty();
        new NonStrictExpectations() {{

            javaMailSender.createMimeMessage();
            result = new Delegate() {

                public MimeMessage createMimeMessage() throws MessagingException {
                    return new MimeMessage((Session) null);
                }

            };

        }};

        new NonStrictExpectations() {{

            javaMailSender.send((javax.mail.internet.MimeMessage) withNotNull());
            result = new Delegate() {

                @SuppressWarnings("unused")
                public void send(MimeMessage message) throws MessagingException {

                    System.out.println(message.getSubject());
                    sentEmails.add(message);
                }

            };

        }};
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

    public List<MimeMessage> getAllSentEmails() {
        waitUntilQueueEmpty();
        return sentEmails;
    }

}
