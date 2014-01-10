package fi.om.initiative.service;

import fi.om.initiative.dto.User;
import fi.om.initiative.dto.initiative.InitiativeManagement;

import java.util.List;

public interface TestDataService {

    void createTestUsersFromTemplates(List<User> userTemplates);

    void createTestInitiativesFromTemplates(List<InitiativeManagement> initiatives, User currentUser, String initiatorEmail, String reserveEmail);
}