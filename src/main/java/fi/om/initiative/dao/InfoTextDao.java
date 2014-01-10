package fi.om.initiative.dao;

import fi.om.initiative.dto.InfoPageText;
import fi.om.initiative.dto.InfoTextFooterLink;
import fi.om.initiative.dto.InfoTextSubject;
import fi.om.initiative.dto.LanguageCode;

import java.util.List;

public interface InfoTextDao {

    public void publishFromDraft(String uri, String modifierName);

    public void saveDraft(InfoPageText uri);

    public void draftFromPublished(String uri, String modifierName);

    public List<InfoTextSubject> getNotEmptySubjects(LanguageCode languageCode);

    public List<InfoTextSubject> getAllSubjects(LanguageCode languageCode);

    public InfoPageText getPublished(String uri);

    public InfoPageText getDraft(String uri);

    List<InfoTextFooterLink> getFooterLinks(LanguageCode language);
}
