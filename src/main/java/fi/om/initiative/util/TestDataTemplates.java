package fi.om.initiative.util;

import com.google.common.collect.Lists;
import fi.om.initiative.dto.LanguageCode;
import fi.om.initiative.dto.ProposalType;
import fi.om.initiative.dto.User;
import fi.om.initiative.dto.author.Author;
import fi.om.initiative.dto.initiative.InitiativeManagement;
import fi.om.initiative.dto.initiative.InitiativeState;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.List;

import static fi.om.initiative.util.Locales.asLocalizedString;
import static fi.om.initiative.util.TestDataTemplateTexts.*;

public class TestDataTemplates {
    private static final User OM_USER = createUserTemplate("Oili", "Oikkonen", "010101-0001", false, true);
    private static final User VRK_USER = createUserTemplate("Veikko", "Verkkonen", "020202-0002", true, false);
    private static final List<User> users = Lists.newArrayList(OM_USER, VRK_USER);

    public static final User RESERVE_AUTHOR_USER = createUserTemplate("Vanja", "Varhonen", "050505-0005", false, false);

    public static final Author RESERVE_AUTHOR = createAuthorTemplate(RESERVE_AUTHOR_USER, "testi.varaedustaja@solita.fi", false, false, true);

    private static final List<InitiativeManagement> initiatives = createInitiativeTemplateList();

    
    public static List<User> getUserTemplates() {
        return users;
    }

    public static List<InitiativeManagement> getInitiativeTemplates() {
        return initiatives;
    }
    
    private static User createUserTemplate(String firstNames, String lastName, String ssn, boolean vrk, boolean om) {
        User user = new User(null, new DateTime(), firstNames, lastName, new LocalDate().minusYears(20), vrk, om);
        user.assignFirstNames(firstNames);
        user.assignLastName(lastName);
        user.assignSsn(ssn);
        user.assignFinnishCitizen(true);
        user.assignHomeMunicipality(asLocalizedString("Helsinki", "Helsingfors"));

        return user;
   }
    
    
    private static List<InitiativeManagement> createInitiativeTemplateList() {
        List<InitiativeManagement> initiatives = Lists.newArrayList();
        initiatives.add(createInitiativeTemplate(InitiativeState.DRAFT, INITIATIVE_1_NAME, INITIATIVE_1_PROPOSAL, INITIATIVE_1_RATIONALE));
        initiatives.add(createInitiativeTemplate(InitiativeState.DRAFT, INITIATIVE_2_NAME, INITIATIVE_2_PROPOSAL, INITIATIVE_2_RATIONALE));
        return initiatives;
    }

    private static Author createAuthorTemplate(User user, String email, boolean initiator, boolean representative, boolean reserve) {
        Author author = new Author(user);
        author.setInitiator(initiator);
        author.setRepresentative(representative);
        author.setReserve(reserve);

        author.assignEmail(email);
        return author;
    }

    private static InitiativeManagement createInitiativeTemplate(InitiativeState state, String name, String proposal, String rationale) {
        InitiativeManagement initiative = new InitiativeManagement();
        initiative.assignId(null);
        initiative.assignState(state);
        initiative.setName(asLocalizedString(name, null));
        initiative.setProposal(asLocalizedString(proposal, null));
        initiative.setProposalType(ProposalType.LAW);
        initiative.setRationale(asLocalizedString(rationale, null));
        initiative.setPrimaryLanguage(LanguageCode.FI);
        initiative.setStartDate(new LocalDate());
        initiative.setSupportStatementsInWeb(true);
        initiative.setSupportStatementsOnPaper(true);

        return initiative;
    }
}