package fi.om.initiative.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.om.initiative.dao.InfoTextDao;
import fi.om.initiative.dto.*;
import fi.om.initiative.util.Locales;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InfoTextService implements FooterLinkProvider {

    private InfoTextDao infoTextDao;

    private UserService userService;

    public InfoTextService(InfoTextDao infoTextDao, UserService userService) {
        this.infoTextDao = infoTextDao;
        this.userService = userService;
    }

    public InfoPageText getPublished(String uri) {
        InfoPageText published = infoTextDao.getPublished(uri);
        return published;
    }

    @Override
    public List<InfoTextFooterLink> getFooterLinks(Locale locale) {
        return infoTextDao.getFooterLinks(languageCode(locale));
    }

    public InfoPageText getDraft(String uri) {
        userService.getUserInRole(Role.OM);
        return infoTextDao.getDraft(uri);
    }

    public Map<String, List<InfoTextSubject>> getPublicSubjectList(Locale locale) {
        return mapByCategory(infoTextDao.getNotEmptySubjects(languageCode(locale)));
    }

    public Map<String, List<InfoTextSubject>> getOmSubjectList(Locale locale) {
        userService.getUserInRole(Role.OM);
        return mapByCategory(infoTextDao.getAllSubjects(languageCode(locale)));
    }

    public void updateDraft(String localizedPageName, String content, String subject) {
        User omUser = userService.getUserInRole(Role.OM);

        InfoPageText infoPageText = InfoPageText.builder(localizedPageName)
                .withText(subject, content)
                .withModifier(omUser.getLastName(), DateTime.now())
                .build();
        infoTextDao.saveDraft(infoPageText);
    }

    public void publishDraft(String uri) {
        userService.getUserInRole(Role.OM);
        infoTextDao.publishFromDraft(uri, userService.getUserInRole(Role.OM).getLastName());
    }

    public void restoreDraftFromPublished(String uri) {
        userService.getUserInRole(Role.OM);
        infoTextDao.draftFromPublished(uri, userService.getUserInRole(Role.OM).getLastName());
    }

    private static Map<String, List<InfoTextSubject>> mapByCategory(List<InfoTextSubject> subjectList) {
        Map<String, List<InfoTextSubject>> map = Maps.newHashMap();
        for (InfoTextCategory infoTextCategory : InfoTextCategory.values()) {
            map.put(infoTextCategory.name(), Lists.<InfoTextSubject>newArrayList());
        }

        for (InfoTextSubject infoTextSubject : subjectList) {
            map.get(infoTextSubject.getInfoTextCategory().name()).add(infoTextSubject);
        }

        return map;
    }


    private static LanguageCode languageCode(Locale locale) {
        return locale.equals(Locales.LOCALE_FI) ? LanguageCode.FI : LanguageCode.SV;
    }

}
