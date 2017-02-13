package fi.om.initiative.service;


import fi.om.initiative.dao.TestHelper;
import fi.om.initiative.dto.FollowInitiativeDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import javax.annotation.Resource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FollowServiceTest extends EmailSpyConfiguration {

    @Resource
    private TestHelper testHelper;

    @Resource
    private FollowService followService;

    @Before
    public void setup() {
        testHelper.dbCleanup();
    }

    @Test
    public void follow_initiative_email_is_validated() {
        Long initiativeId = testHelper.createRunningPublicInitiative(testHelper.createTestUser(), "test");

        FollowInitiativeDto followInitiativeDto = new FollowInitiativeDto();
        followInitiativeDto.setEmail("INVALID EMAIL");

        assertFalse(followService.followInitiative(initiativeId, followInitiativeDto, emailValidationErrors(followInitiativeDto), null));

        assertSentEmailCount(0);
    }

    @Test
    public void follow_initiative_sends_confirmation_email() {

        Long initiativeId = testHelper.createRunningPublicInitiative(testHelper.createTestUser(), "test");

        FollowInitiativeDto followInitiativeDto = new FollowInitiativeDto();
        followInitiativeDto.setEmail("follower@example.com");

        assertTrue(followService.followInitiative(initiativeId, followInitiativeDto, emailValidationErrors(followInitiativeDto), null));

        assertSentEmailCount(1);
        assertSentEmail("follower@example.com", "Olet tilannut aloitteen sähköpostitiedotteet / Du har beställt e-postmeddelanden med information om initiativet");

    }

    // This tries to be the BindingResult created by spring while validating.
    private static DirectFieldBindingResult emailValidationErrors(FollowInitiativeDto followInitiativeDto) {
        return new DirectFieldBindingResult(followInitiativeDto, "email");
    }

}
