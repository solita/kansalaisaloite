package fi.om.initiative.service;

import fi.om.initiative.dao.SupportVoteDao;
import fi.om.initiative.dto.SupportVote;
import fi.om.initiative.dto.initiative.InitiativeState;
import fi.om.initiative.util.Locales;
import fi.om.initiative.util.MutableObject;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;


public class SupportVoteServiceTest extends ServiceTestBase {
    
    @Mocked SupportVoteDao supportVoteDao;
    @Mocked InitiativeService initiativeService;
    @Mocked UserService userService; 
    @Mocked EmailService emailService; 
    @Mocked Errors errors;

    @Autowired EncryptionService encryptionService;
    @Autowired SmartValidator validator;

    
    private SupportVoteService supportVoteService;
    
    @Before
    public void init() {
        supportVoteService = new SupportVoteServiceImpl(supportVoteDao, userService, emailService, encryptionService, initiativeService, INITIATIVE_SETTINGS);
    }   


    @SuppressWarnings("rawtypes")
    @Test
    public void Add_Suppport_Vote_Properties() {
        INITIATIVE_PUBLIC.assignState(InitiativeState.ACCEPTED);
        
        final MutableObject<SupportVote> vote1Holder = MutableObject.create();
        final MutableObject<SupportVote> vote2Holder = MutableObject.create();
        
        // 2 times!
        new Expectations(2) {{
            userService.getUserInRole(Role.AUTHENTICATED); result = REGISTERED_USER; 
            initiativeService.getInitiativeForPublic(INITIATIVE_PUBLIC.getId()); result = INITIATIVE_PUBLIC; 

            userService.getUserInRole(Role.AUTHENTICATED); result = REGISTERED_USER;
            supportVoteDao.getVote(INITIATIVE_PUBLIC.getId(), (String) withNotNull()); result = null;
            
            supportVoteDao.incrementSupportCount(INITIATIVE_PUBLIC.getId());
            supportVoteDao.insertSupportVote((SupportVote) withNotNull());

            result = new Delegate() {
                @SuppressWarnings("unused")
                void insertSupportVote(SupportVote vote) {
                    if (vote1Holder.get() == null) {
                        vote1Holder.set(vote);
                    } else {
                        vote2Holder.set(vote);
                    }
                }
            };
        }};
        
        supportVoteService.vote(INITIATIVE_PUBLIC.getId(), Locales.LOCALE_FI);
        supportVoteService.vote(INITIATIVE_PUBLIC.getId(), Locales.LOCALE_FI);
        
        SupportVote vote1 = vote1Holder.get();
        SupportVote vote2 = vote2Holder.get();
        
        // Same supportId for duplicate check
        assertEquals(vote1.getSupportId(), vote2.getSupportId()); 
        
        // Strong encryption uses different salt for each encryption operation
        assertFalse(vote1.getEncryptedDetails().equals(vote2.getEncryptedDetails()));

        // Actual details are same however
        String vote1Details = encryptionService.decrypt(vote1.getEncryptedDetails());
        String vote2Details = encryptionService.decrypt(vote2.getEncryptedDetails());
        assertEquals(vote1Details, vote2Details);

        String today = SupportVoteServiceImpl.VRK_DTF.print(vote1.getCreated());
        String dateOfBirth = SupportVoteServiceImpl.VRK_DTF.print(REGISTERED_USER.getDateOfBirth());
        assertEquals("User|Registered|"+dateOfBirth+"|Helsinki|"+today, vote1Details);
    }

}
