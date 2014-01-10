package fi.om.initiative.service;

import com.google.common.collect.Lists;
import fi.om.initiative.conf.IntegrationTestConfiguration;
import fi.om.initiative.dao.InitiativeDao;
import fi.om.initiative.dao.TestHelper;
import fi.om.initiative.dto.*;
import fi.om.initiative.dto.initiative.InitiativeManagement;
import fi.om.initiative.dto.initiative.InitiativeState;
import fi.om.initiative.web.HttpUserServiceImpl;
import mockit.Delegate;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static fi.om.initiative.util.Locales.LOCALE_FI;
import static fi.om.initiative.util.Locales.asLocalizedString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={IntegrationTestConfiguration.class})
public class SupportVoteServiceIntegrationTest {

    @Resource
    InitiativeDao initiativeDao;

    @Resource
    SupportVoteService supportVoteService;
    
    @Resource
    TestHelper testHelper;
    
    @Mocked HttpUserServiceImpl userService;
    
    private static DateTime testStartTime = new DateTime();

    private static LocalDate today = testStartTime.toLocalDate();

    private static Integer stringChangerIndex = 0;

    private Long userId;
    
    private final LocalizedString municipality = TestHelper.createDefaultMunicipality(); 

    @Before
    public void init() {
        testHelper.dbCleanup();
        userId = testHelper.createTestUser();
        //NOTE: testStartTime should use db server time so that comparisons to trigger updated fields don't fail
        testStartTime = testHelper.getDbCurrentTime();
        today = testStartTime.toLocalDate();
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testMultipleSupports() throws InterruptedException, ExecutionException {
        // For load testing or profiling, increase these
        final int CONCURRENT_THREADS = 10; // E.g. 20
        final int RUNS_PER_THREAD = 20; // E.g. 100
        final int RUNS = CONCURRENT_THREADS * RUNS_PER_THREAD;

        final DateTime startTime = new DateTime();
        final DateTime dt = new DateTime(1920, 1, 1, 0, 0);
        final DateTimeFormatter dtf = DateTimeFormat.forPattern("ddMMyy-hhmm");
        final Set<String> ssns = new HashSet<String>();
        new NonStrictExpectations() {{
            userService.getUserInRole((Role[]) withNotNull()); 
            result = new Delegate() {
                private AtomicInteger count = new AtomicInteger(0);
                @SuppressWarnings("unused")
                public User getUserInRole(Role... roles) {
                    String ssn = dtf.print(dt.plusMinutes(count.incrementAndGet() * 31));
                    if (!ssns.add(ssn)) {
                        throw new RuntimeException("Duplicate SSN: " + ssn);
                    }
                    return new User(ssn, null, "Teppo", "Testi", true, municipality);
                }
            };
            times = -1;
        }};
        
        final Long initietiveId = create(createInitiative(null), userId);
        final List<Boolean> supportVotes = Collections.synchronizedList(new ArrayList<Boolean>());

        List<Callable<Boolean>> threads = Lists.newArrayList();

        for (int i = 0; i < CONCURRENT_THREADS; ++i) {
            threads.add(new Callable<Boolean>(){
                @Override
                public Boolean call() throws Exception {
                    for (int i=0; i < RUNS_PER_THREAD; i++) {
                        try {
                            supportVoteService.vote(initietiveId, LOCALE_FI);
                            supportVotes.add(Boolean.TRUE);
                        } catch (Exception e) {
                            supportVotes.add(Boolean.FALSE);
                            throw e;
                        }
                    }
                    return Boolean.TRUE;
                }
            });
        }

        ExecutorService executor = Executors.newCachedThreadPool();
        for (Future<Boolean> booleanFuture : executor.invokeAll(threads)) {
            assertThat(booleanFuture.get(), is(Boolean.TRUE));
        }

        assertThat(supportVotes.size(), is(RUNS));

        System.out.println("testMultipleSupports(" + RUNS + ") finished in " + new Period(startTime, new DateTime()));
        
        for (Boolean status : supportVotes) {    // Double check
            assertThat(status, is(Boolean.TRUE));
        }

        assertThat(initiativeDao.get(initietiveId).getTotalSupportCount(), is(RUNS));
    }

    // Utils copied temporarily from InitiativeDaoTest

    private Long create(InitiativeManagement initiative, Long userId) {
        Long initiativeId = initiativeDao.create(initiative, userId);

        initiativeDao.updateLinks(initiativeId, initiative.getLinks());
        initiativeDao.updateInvitations(initiativeId, initiative.getInitiatorInvitations(),
                initiative.getRepresentativeInvitations(),
                initiative.getReserveInvitations());

        initiativeDao.updateInitiativeState(initiativeId, userId, InitiativeState.ACCEPTED, "Accepted by default for tests");
        return initiativeId;
    }

    public static InitiativeManagement createInitiative(Long id) {
        String chg = getChanger();
        InitiativeManagement initiative = new InitiativeManagement();
        initiative.assignId(id);
        initiative.setFinancialSupport(true);
        initiative.setFinancialSupportURL(new InitURI("http://www.solita.fi"+chg));
        initiative.setName(asLocalizedString("Nimi"+chg, null));
        initiative.setProposal(asLocalizedString("Ehdotus"+chg, null));
        initiative.setProposalType(ProposalType.LAW);
        initiative.setRationale(asLocalizedString("Perustelut"+chg, null));
        initiative.setPrimaryLanguage(LanguageCode.FI);
        initiative.setStartDate(today);
        initiative.assignEndDate(today.plusMonths(6));
        initiative.setSupportStatementsInWeb(true);
        initiative.setSupportStatementsOnPaper(true);

        return initiative;
    }

    private static String getChanger() {
        //increments changer string to ensure that each version of test data is different
        stringChangerIndex++;
        return stringChangerIndex.toString();
    }
}
