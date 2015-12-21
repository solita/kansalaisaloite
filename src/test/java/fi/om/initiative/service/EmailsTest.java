package fi.om.initiative.service;


import fi.om.initiative.dao.TestHelper;
import fi.om.initiative.dto.User;
import fi.om.initiative.dto.initiative.InitiativeState;
import fi.om.initiative.web.HttpUserServiceImpl;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

public class EmailsTest extends EmailSpyConfiguration {

    @Resource
    private TestHelper testHelper;

    private Long userId;

    @Mocked
    HttpUserServiceImpl userService;

    @Resource
    private SupportVoteService supportVoteService;

    @Before
    public void init() {
        testHelper.dbCleanup();
        userId = testHelper.createTestUser();

        new Expectations() {{
            userService.getUserInRole((Role[]) withNotNull());
            result = new Delegate() {
                private AtomicInteger count = new AtomicInteger(0);
                @SuppressWarnings("unused")
                public User getUserInRole(Role... roles) {
                    return new User(userId, DateTime.now(), "Joku", "Käyttäjä", new LocalDate(1990, 1, 1), false, false);
                }
            };
            times = -1;
        }};

    }

    @Test
    public void send_initiative_to_vrk_sends_email_to_vrk() throws InterruptedException {

        final Long initiative = testHelper.create(
                new TestHelper.InitiativeDraft(userId)
                        .withState(InitiativeState.ACCEPTED)
                        .withSupportCount(50001)

        );

        testHelper.createSupport(initiative, LocalDate.now());

        supportVoteService.sendToVRK(initiative);

        assertSentEmailCount(1);
        assertSentEmail("kansalaisaloite.tarkastus@vrk.fi", "Kannatusilmoitusten määrän vahvistuspyyntö / Ansökan om bekräftelse av antalet stödförklaringar");
    }

}
