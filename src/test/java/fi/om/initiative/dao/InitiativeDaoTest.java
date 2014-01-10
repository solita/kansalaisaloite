package fi.om.initiative.dao;

import com.google.common.collect.Lists;
import fi.om.initiative.conf.IntegrationTestConfiguration;
import fi.om.initiative.dto.*;
import fi.om.initiative.dto.author.Author;
import fi.om.initiative.dto.author.ContactInfo;
import fi.om.initiative.dto.initiative.*;
import fi.om.initiative.dto.search.InitiativeSearch;
import fi.om.initiative.dto.search.SearchView;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.net.MalformedURLException;
import java.util.List;

import static fi.om.initiative.util.Locales.asLocalizedString;
import static junit.framework.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={IntegrationTestConfiguration.class})
public class InitiativeDaoTest {

    @Resource
    private InitiativeDao initiativeDao;

    private Long userId;

    private static LocalDate DOB = new LocalDate().minusYears(20);

    private static DateTime testStartTime = new DateTime(); // XXX: Is re-initialized by init() - function.
                                                            // Tests are fragile if system localdate and database localdate are not in sync.
    private static LocalDate today = testStartTime.toLocalDate();

    private static Integer stringChangerIndex = 0;

    private static InitiativeSettings.MinSupportCountSettings impossibleStrictMinSupportCount = new InitiativeSettings.MinSupportCountSettings(1000000, Years.years(1000));
    
    @Resource
    private TestHelper testHelper;

    @Before
    public void init() {
        testHelper.dbCleanup();
        userId = testHelper.createTestUser();
        //NOTE: testStartTime should use db server time so that comparisons to trigger updated fields don't fail
        testStartTime = testHelper.getDbCurrentTime();
        today = testStartTime.toLocalDate();
    }

    @Test
    public void Intiative_CRUD_OK() {
        InitiativeManagement afterCreate = intiativeCreateTest();
        wait100(); // to ensure that timestamp would change
        intiativeUpdateTest(afterCreate);
    }

    private InitiativeManagement getTestInitiativeDraft() {
        Long id = create(createNotEndedInitiative(null), userId);
        return initiativeDao.getInitiativeForManagement(id, false);
    }
    
    @Test
    public void Intiative_Remove_Link_With_Empty_Label_And_URI_OK() {
        InitiativeManagement beforeUpdate = getTestInitiativeDraft();

        Link link = beforeUpdate.getLinks().get(0);
        link.setLabel("");
        link.setUri(null);

        initiativeDao.updateLinks(beforeUpdate.getId(), beforeUpdate.getLinks());
        
        InitiativeBase afterUpdate = initiativeDao.getInitiativeForPublic(beforeUpdate.getId());

        assertEquals(beforeUpdate.getLinks().size() - 1, afterUpdate.getLinks().size());
    }

    @Test
    public void Intiative_List_OK() {
        int initiativesCount = 3; // for larger counts optimize test to use sorted lists
        List<InitiativeBase> initiativesGet = Lists.newArrayList();

        Author author = createAuthor(userId);

        for (int i = 0; i < initiativesCount; i++) {
            Long id = create(createNotEndedInitiative(null), userId);
            // Author is required for own...
            initiativeDao.insertAuthor(id, userId, author);
            InitiativeBase initiative = initiativeDao.getInitiativeForPublic(id);
            initiativesGet.add(initiative);
        }

        List<InitiativeInfo> ownInitiativeList = initiativeDao.findInitiatives(new InitiativeSearch(SearchView.pub), userId, impossibleStrictMinSupportCount).list;
        assertEquals(0, ownInitiativeList.size()); // there were no public initiatives yet

        List<InitiativeInfo> publicInitiativeList = initiativeDao.findInitiatives(new InitiativeSearch(SearchView.own).setMinSupportCount(0), userId, impossibleStrictMinSupportCount).list;
        assertEquals(initiativesCount, publicInitiativeList.size());

    }

    private Long create(InitiativeManagement initiative, Long userId) {
        Long initiativeId = initiativeDao.create(initiative, userId);

        initiativeDao.updateLinks(initiativeId, initiative.getLinks());

        initiativeDao.updateInvitations(initiativeId, initiative.getInitiatorInvitations(),
                initiative.getRepresentativeInvitations(),
                initiative.getReserveInvitations());

        return initiativeId;
    }

    @Test
    public void Intiative_State_Update_OK() {
        InitiativeBase before = getTestInitiativeDraft();
        assertEquals(InitiativeState.DRAFT, before.getState());

        wait100(); // to ensure that timestamps would change

        InitiativeState newState = InitiativeState.REVIEW;
        initiativeDao.updateInitiativeState(before.getId(), userId, newState, null);
        InitiativeManagement after = initiativeDao.getInitiativeForManagement(before.getId(), false);

        assertEquals(newState, after.getState());
        assertAutoTimestamp(before.getStateDate(), after.getStateDate());

        assertInitiative(before, after); //normal fields
        //other auto fields:
        assertEquals(before.getId(), after.getId());
        assertEquals(userId, after.getModifierId());
        assertAutoTimestamp(before.getModified(), after.getModified());
        assertEquals(0, after.getSupportCount());
    }

    @Test
    public void Author_CRUD_OK() {
        InitiativeBase initiative = getTestInitiativeDraft();

        Author afterCreate = authorCreateTest(initiative.getId());
        wait100(); // to ensure that timestamp would change
        Author afterUpdate = authorUpdateTest(initiative.getId(), afterCreate);
        authorFromInitiativeTest(initiative.getId(), afterUpdate);
        //TODO: delete test
    }

    @Test
    public void update_parliament_data() throws MalformedURLException {
        Long id = create(createNotEndedInitiative(null), userId);

        SendToParliamentData sendToParliamentData = new SendToParliamentData();
        sendToParliamentData.setParliamentIdentifier("identifier");
        sendToParliamentData.setParliamentSentTime(new LocalDate(2010, 5, 5));
        sendToParliamentData.setParliamentURL("http://www.example.com");

        initiativeDao.updateSendToParliament(id, sendToParliamentData);

        InitiativeManagement updated = initiativeDao.get(id);
        assertEquals(sendToParliamentData.getParliamentIdentifier(), updated.getParliamentIdentifier());
        assertEquals(sendToParliamentData.getParliamentURL(), updated.getParliamentURL());
        assertEquals(sendToParliamentData.getParliamentSentTime(), updated.getParliamentSentTime());
        assertEquals(InitiativeState.DONE, updated.getState());
    }
    
    private void authorFromInitiativeTest(Long initiativeId, Author expected) {
        InitiativeManagement initiative = initiativeDao.getInitiativeForManagement(initiativeId, false);
        Author actual;
        if (expected.isInitiator()) {
            actual = initiative.getInitiators().get(0);
        } else if (expected.isRepresentative()) {
            actual = initiative.getRepresentatives().get(0);
        } else if (expected.isReserve()) {
            actual = initiative.getReserves().get(0);
        } else {
            throw new IllegalStateException("Author was not what expected");
        }

        assertEquals(expected.getFirstNames(), actual.getFirstNames());
        assertEquals(expected.getLastName()  , actual.   getLastName());   
        assertEquals(expected.getHomeMunicipality(), actual.getHomeMunicipality());

        assertContacInfoEquals(expected.getContactInfo(), actual.getContactInfo());
    }
    
    private void assertContacInfoEquals(ContactInfo expected, ContactInfo actual) {
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getPhone(), actual.getPhone());
    }
    
    private Author authorCreateTest(Long initiativeId) {
        Author before = createAuthor(userId);

        // These are not handled in regular updates -> these should be overriden by defaults
        before.assignConfirmed(testStartTime.plusDays(7));
        before.assignCreated(testStartTime.plusDays(7));

        initiativeDao.insertAuthor(initiativeId, userId, before);
        
        Author after = initiativeDao.getAuthor(initiativeId, userId);

        assertAuthor(before, after);
        assertAuthorAutoFieldsCreated(after);
        return after;
    }

    private Author authorUpdateTest(Long initiativeId, Author before) {
        authorUpdateValues(before);
        initiativeDao.updateAuthor(initiativeId, userId, before);
        Author after = initiativeDao.getAuthor(initiativeId, userId);

        assertAuthor(before, after);
        assertAuthorAutoFieldsEqual(before, after); // doesn't include automatic update fields

        return after;
    }
    
    private InitiativeManagement intiativeCreateTest() {
        InitiativeManagement before = createNotEndedInitiative(-123l);

        // These are not handled in regular updates -> these should be overriden by defaults
        before.assignModifierId(-123l); // Non-existing
        before.assignModified(testStartTime.plusDays(7));
        before.assignState(InitiativeState.ACCEPTED);
        before.assignStateDate(testStartTime.plusDays(7));
        before.assignSupportCount(50);

        Long id = create(before, userId);
        
        assertNotNull(id);
        
        InitiativeManagement after = initiativeDao.getInitiativeForManagement(id, false);

        assertInitiative(before, after);
        assertInitiativeAutoFieldsModified(null, after, userId, id);
        return after;
    }
    
    private InitiativeBase intiativeUpdateTest(InitiativeManagement before) {
        Long id = before.getId();
        intiativeUpdateValues(before);
        initiativeDao.updateInitiative(before, userId, true, true);
        initiativeDao.updateLinks(before.getId(), before.getLinks());
        
        InitiativeBase after = initiativeDao.getInitiativeForPublic(before.getId());

        assertInitiative(before, after);
        assertInitiativeAutoFieldsModified(before, after, userId, id);

        //updateInitiativeValues updated link #0, removed #1 and created a new, so first id should be same and second different
        assertEquals(before.getLinks().get(0).getId(), after.getLinks().get(0).getId());
        assertNotEquals(before.getLinks().get(1).getId(), after.getLinks().get(1).getId());
        return after;
    }

    
    private void assertInitiative(InitiativeInfo before, InitiativeInfo after) {
        assertEquals(before.isFinancialSupport(), after.isFinancialSupport());
        assertEquals(before.getFinancialSupportURL(), after.getFinancialSupportURL());
        assertEquals(before.getName(), after.getName());
        assertEquals(before.getProposalType(), after.getProposalType());
        assertEquals(before.getPrimaryLanguage(), after.getPrimaryLanguage());
        assertEquals(before.getStartDate(), after.getStartDate());
        assertEquals(before.getEndDate(), after.getEndDate());
        assertEquals(before.isSupportStatementsInWeb(), after.isSupportStatementsInWeb());
        assertEquals(before.isSupportStatementsOnPaper(), after.isSupportStatementsOnPaper());
        assertEquals(before.isSupportStatementPdf(), after.isSupportStatementPdf());
        assertEquals(before.getSupportStatementAddress(), after.getSupportStatementAddress());
    }

    
    private void assertInitiative(InitiativeBase before, InitiativeBase after) {
        assertInitiative((InitiativeInfo) before, (InitiativeInfo) after);
        assertEquals(before.getProposal(), after.getProposal());
        assertEquals(before.getRationale(), after.getRationale());

        // Compare links
        List<Link> beforeLinks = before.getLinks();
        List<Link> afterLinks = after.getLinks();
        assertEquals(beforeLinks.size(), afterLinks.size());
        
        for (int i=0; i < beforeLinks.size(); i++) {
            Link expectedLink = beforeLinks.get(i);
            Link actualLink = afterLinks.get(i);
            
            assertEquals(expectedLink.getUri(), actualLink.getUri());
            assertEquals(expectedLink.getLabel(), actualLink.getLabel());
        }
    }

    private void assertInitiativeAutoFieldsModified(InitiativeManagement old, InitiativeManagement i, Long userId, Long id) {
        assertInitiativeAutoFieldsModified((InitiativeBase) old, (InitiativeBase) i, userId, id);
        assertEquals(userId, i.getModifierId());
    }
    
    private void assertInitiativeAutoFieldsModified(InitiativeBase old, InitiativeBase i, Long userId, Long id) {
        // These are not inserted or updated manually -> assert defaults
        assertEquals(id, i.getId());
        
        if (old == null) {
            assertAutoTimestamp(i.getModified());
            assertEquals(InitiativeState.DRAFT, i.getState());
            assertAutoTimestamp(i.getStateDate());
        }
        else {
            assertAutoTimestamp(old.getModified(), i.getModified());
            assertEquals(old.getState(), i.getState());
            assertEquals(old.getStateDate(), i.getStateDate());
        }
        assertEquals(0, i.getSupportCount());
    }

    public static InitiativeManagement createNotEndedInitiative(Long id) {
        String chg = getChanger();
        InitiativeManagement initiative = new InitiativeManagement();
        initiative.assignId(id);
        initiative.setFinancialSupport(true);
        initiative.setFinancialSupportURL(new InitURI("http://www.solita.fi" + chg));
        initiative.setName(asLocalizedString("Nimi" + chg, null));
        initiative.setProposal(asLocalizedString("Ehdotus" + chg, null));
        initiative.setAcceptanceIdentifier("some acceptance identifier");
        initiative.setProposalType(ProposalType.LAW);
        initiative.setRationale(asLocalizedString("Perustelut"+chg, null));
        initiative.setPrimaryLanguage(LanguageCode.FI);
        initiative.setStartDate(today);
        initiative.assignEndDate(today.plusMonths(6));
        initiative.setSupportStatementsInWeb(true);
        initiative.setSupportStatementsOnPaper(true);

        initiative.setLinks(Lists.newArrayList(intiativeLinkCreateValues(), intiativeLinkCreateValues()));
        
        return initiative;
    }

    private void intiativeUpdateValues(InitiativeBase i) {
        String chg = getChanger();
        i.setFinancialSupport(!i.isFinancialSupport());
        i.setFinancialSupportURL(new InitURI(i.getFinancialSupportURL()+chg));
        i.setName(updateFiSvMap(i.getName(), chg));
        i.setProposal(updateFiSvMap(i.getProposal(), chg));
        i.setProposalType(ProposalType.PREPARATION);
        i.setRationale(updateFiSvMap(i.getRationale(), chg));
        i.setPrimaryLanguage(LanguageCode.FI);
        i.setStartDate(i.getStartDate().plusDays(1));
        i.assignEndDate(i.getEndDate().plusDays(1));
        i.setSupportStatementsInWeb(!i.isSupportStatementsInWeb());
        i.setSupportStatementsOnPaper(!i.isSupportStatementsOnPaper());
        i.setSupportStatementPdf(!i.isSupportStatementPdf());
        i.setSupportStatementAddress(chg);

        List<Link> oldLinks = i.getLinks(); //there should be two links inserted in a previous create
        //update link #0, remove #1 and create a new
        List<Link> newLinks = Lists.newArrayList(intiativeLinkUpdateValues(oldLinks.get(0)), intiativeLinkCreateValues());
        i.setLinks(newLinks);
    }
    
    private Link intiativeLinkUpdateValues(Link l) {
        String chg = getChanger();
        l.setLabel(l.getLabel() + chg);
        l.setUri(new InitURI(l.getUri()+chg));
        return l;
    }
    
    public static Link intiativeLinkCreateValues() {
        String chg = getChanger();
        Link link = new Link();
        link.setLabel("Solita"+chg);
        link.setUri(new InitURI("http://www.solita.fi"+chg));
        return link;
    }

    
    
    private void assertAuthor(Author before, Author after) {
        assertEquals(before.getUserId(), after.getUserId());
        assertEquals(before.getFirstNames(), after.getFirstNames());
        assertEquals(before.getLastName(), after.getLastName());
        assertEquals(before.getHomeMunicipality(), after.getHomeMunicipality());
        
        assertEquals(before.isInitiator(), after.isInitiator());
        assertEquals(before.isRepresentative(), after.isRepresentative());
        assertEquals(before.isReserve(), after.isReserve());

        assertContacInfoEquals(before.getContactInfo(), after.getContactInfo());
    }

    private void assertAuthorAutoFieldsEqual(Author before, Author after) {
        // Automatic fields should remain same when not updated
        assertEquals(before.getConfirmed(), after.getConfirmed());
        assertEquals(before.getCreated(), after.getCreated());
    }
    
    private void assertAuthorAutoFieldsCreated(Author after) {
        assertAutoTimestamp(after.getConfirmed());
        assertAutoTimestamp(after.getCreated());
    }

    public static Author createAuthor(Long userId, boolean initiator, boolean representative, boolean reserve) {
        String chg = getChanger();
        Author author = new Author(userId, "Etunimi"+chg, "Sukunimi"+chg, DOB, TestHelper.createDefaultMunicipality());
        author.setInitiator(initiator);
        author.setRepresentative(representative);
        author.setReserve(reserve);
        
        author.assignAddress("Kotikatu 5"+chg);
        author.assignEmail("email"+chg+"@domain.fi");
        author.assignPhone("123456"+chg);
        return author;
    }
    
    public static Author createAuthor(Long userId) {
        return createAuthor(userId, true, true, false);
    }
    

    private void authorUpdateValues(Author a) {
        String chg = getChanger();
        //Author author = new Author(userId, "Etunimi"+chg, "Sukunimi"+chg, "Kotikunta"+chg);
        a.setInitiator(!a.isInitiator());
        a.setRepresentative(!a.isRepresentative());
        a.setReserve(!a.isReserve());

        ContactInfo c = a.getContactInfo();
        c.setAddress(c.getAddress()+chg);
        c.setEmail(c.getEmail()+chg);
        c.setPhone(c.getPhone()+chg);
    }
    
    private static String getChanger() {
        //increments changer string to ensure that each version of test data is different
        stringChangerIndex++;
        return stringChangerIndex.toString();
    }
    
    private LocalizedString updateFiSvMap(LocalizedString original, String changer) {
        return asLocalizedString(original.getFi()+changer, original.getSv()+changer);
    }

    private void assertAutoTimestamp(DateTime beforeModification, DateTime afterModification) {
        assertAutoTimestamp(afterModification);
        assertBefore(beforeModification, afterModification);
    }
    
    private void assertAutoTimestamp(DateTime afterModification) {
        assertBeforeOrEqual(testStartTime, afterModification);
        assertBeforeOrEqualNow(afterModification);
    }
    
    private void assertBeforeOrEqual(DateTime before, DateTime after) {
        assertTrue(before.isEqual(after) || before.isBefore(after));
    }
    private void assertBefore(DateTime before, DateTime after) {
        assertTrue(before.isBefore(after));
    }
    private void assertBeforeOrEqualNow(DateTime value) {
        assertTrue("Not true: "+value+"<="+new DateTime(), value.isBeforeNow() || value.isEqualNow());
    }
    private void assertNotEquals(Long before, Long after) {
        assertTrue(!after.equals(before));
    }

    private synchronized void wait100() {
        try {
            wait(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
