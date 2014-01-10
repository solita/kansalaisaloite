package fi.om.initiative.dao;

import fi.om.initiative.conf.IntegrationTestConfiguration;
import fi.om.initiative.dto.*;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.StringContains.containsString;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={IntegrationTestConfiguration.class})
public class InfoTextDaoImplTest {

    public static final String URI_FI = "uri_fi";
    public static final String URI_SV = "uri_sv";
    public static final String TEXT_FI = "text_fi";
    public static final String TEXT_SV = "text_sv";
    public static final String SUBJECT_FI = "subject_fi";
    public static final String DRAFT_SUBJECT_FI = "draft_subject_fi";
    public static final String SUBJECT_SV = "subject_sv";
    public static final String DRAFT_SUBJECT_SV = "draft_subject_sv";
    public static final String DRAFT_FI = "draft_fi";
    public static final String DRAFT_SV = "draft_sv";
    private static final DateTime MODIFIED = new DateTime(2010, 1, 1, 0, 0);
    private static final String MODIFIER = "Modifier Name";
    public static final InfoTextCategory INFO_TEXT_CATEGORY = InfoTextCategory.KANSALAISALOITE;

    @Resource
    TestHelper testHelper;

    @Resource
    InfoTextDao infoTextDao;

    private Long testInfoText;
    private Long otherInfoText;

    @Before
    public void setup() {
        testHelper.dbCleanup();
        testInfoText = testHelper.createInfoText(
                LanguageCode.FI,
                INFO_TEXT_CATEGORY, 1,
                URI_FI,
                SUBJECT_FI,
                DRAFT_SUBJECT_FI,
                TEXT_FI,
                DRAFT_FI,
                MODIFIED, MODIFIER);
        otherInfoText = testHelper.createInfoText(
                LanguageCode.SV,
                InfoTextCategory.KANSALAISALOITE, 1,
                URI_SV,
                SUBJECT_SV,
                DRAFT_SUBJECT_SV,
                TEXT_SV,
                DRAFT_SV,
                MODIFIED, MODIFIER);
    }

    @Test
    public void get_published_by_uri_parses_all_information() {
        InfoPageText infoText = infoTextDao.getPublished(URI_FI);

        assertThat(infoText.getSubject(), is(SUBJECT_FI));
        assertThat(infoText.getUri(), is(URI_FI));
        assertThat(infoText.getContent(), is(TEXT_FI));
        assertThat(infoText.getModifyTime(), is(MODIFIED));
        assertThat(infoText.getModifierName(), is(MODIFIER));

    }

    @Test
    public void get_draft_by_uri_parses_all_information() {
        InfoPageText infoText = infoTextDao.getDraft(URI_FI);

        assertThat(infoText.getSubject(), is(DRAFT_SUBJECT_FI));
        assertThat(infoText.getUri(), is(URI_FI));
        assertThat(infoText.getContent(), is(DRAFT_FI));
        assertThat(infoText.getModifyTime(), is(MODIFIED));
        assertThat(infoText.getModifierName(), is(MODIFIER));
    }

    @Test
    public void publish_from_draft_sets_draft_as_text() {
        infoTextDao.publishFromDraft(URI_FI, "Modifier");

        InfoPageText published = infoTextDao.getPublished(URI_FI);
        assertThat(published.getModifyTime(), is(not(MODIFIED)));
        assertThat(published.getContent(), is(DRAFT_FI));
        assertThat(published.getSubject(), is(DRAFT_SUBJECT_FI));
        assertThat(published.getModifierName(), is("Modifier"));

    }

    @Test
    public void restore_from_public_to_draft() {
        infoTextDao.draftFromPublished(URI_FI, "Modifier");

        InfoPageText draft = infoTextDao.getDraft(URI_FI);
        assertThat(draft.getModifyTime(), is(not(MODIFIED)));
        assertThat(draft.getContent(), is(TEXT_FI));
        assertThat(draft.getSubject(), is(SUBJECT_FI));
        assertThat(draft.getModifierName(), is("Modifier"));

    }

    @Test
    public void get_om_list_assigns_all_values() {
        List<InfoTextSubject> omSubjectList = infoTextDao.getAllSubjects(LanguageCode.FI);

        assertThat(omSubjectList.get(0).getUri(), is(URI_FI));
        assertThat(omSubjectList.get(0).getSubject(), is(SUBJECT_FI));
        assertThat(omSubjectList.get(0).getInfoTextCategory(), is(INFO_TEXT_CATEGORY));
    }

    @Test
    public void get_om_list_returns_all_with_given_language_ordered_by_orderPosition() {
        testHelper.dbCleanup();
        testHelper.createInfoText(LanguageCode.FI, InfoTextCategory.KANSALAISALOITE, 10, "uri1", null, null, null, null, null, null);
        testHelper.createInfoText(LanguageCode.FI, InfoTextCategory.KANSALAISALOITE, 11, "uri2", "", "", "", "", null, "");
        testHelper.createInfoText(LanguageCode.FI, InfoTextCategory.KANSALAISALOITE, 9, "uri3", "t", "t", "t", "t", null, "");

        List<InfoTextSubject> omSubjectList = infoTextDao.getAllSubjects(LanguageCode.FI);
        assertThat(omSubjectList.size(), is(3));

        assertThat(omSubjectList.get(0).getUri(), is("uri3"));
        assertThat(omSubjectList.get(1).getUri(), is("uri1"));
        assertThat(omSubjectList.get(2).getUri(), is("uri2"));

    }

    @Test
    public void get_om_list_returns_only_links_with_given_language() {
        testHelper.dbCleanup();
        testHelper.createInfoText(LanguageCode.FI, InfoTextCategory.KANSALAISALOITE, 10, "finnish_uri", "text", "text", "text", "text", new DateTime(), "name");
        testHelper.createInfoText(LanguageCode.SV, InfoTextCategory.KANSALAISALOITE, 10, "swedish_uri", "text", "text", "text", "text", new DateTime(), "name");

        List<InfoTextSubject> finnish_links = infoTextDao.getAllSubjects(LanguageCode.FI);
        List<InfoTextSubject> swedish_links = infoTextDao.getAllSubjects(LanguageCode.SV);

        assertThat(finnish_links.size(), is(1));
        assertThat(swedish_links.size(), is(1));
        assertThat(finnish_links.get(0).getUri(), is("finnish_uri"));
        assertThat(swedish_links.get(0).getUri(), is("swedish_uri"));

    }

    @Test
    public void get_public_list_returns_only_links_ordered_by_orderPosition() {
        testHelper.dbCleanup();
        testHelper.createInfoText(LanguageCode.FI, InfoTextCategory.KANSALAISALOITE, 10, "uri1", "t", "t", "t", "t", null, null);
        testHelper.createInfoText(LanguageCode.FI, InfoTextCategory.KANSALAISALOITE, 11, "uri2", "t", "t", "t", "t", null, null);
        testHelper.createInfoText(LanguageCode.FI, InfoTextCategory.KANSALAISALOITE, 9, "uri3", "t", "t", "t", "t", null, "");

        List<InfoTextSubject> publicSubjectList = infoTextDao.getNotEmptySubjects(LanguageCode.FI);
        assertThat(publicSubjectList.size(), is(3));

        assertThat(publicSubjectList.get(0).getUri(), is("uri3"));
        assertThat(publicSubjectList.get(1).getUri(), is("uri1"));
        assertThat(publicSubjectList.get(2).getUri(), is("uri2"));
    }


    @Test
    public void get_public_list_returns_only_links_which_have_no_null_or_empty_text() {
        testHelper.dbCleanup();
        testHelper.createInfoText(LanguageCode.FI, InfoTextCategory.KANSALAISALOITE, 1, "1", "","",  "", "draftText", DateTime.now(), "name");
        testHelper.createInfoText(LanguageCode.FI, InfoTextCategory.KANSALAISALOITE, 2, "2", null,null, null, "draftText", DateTime.now(), "name");
        testHelper.createInfoText(LanguageCode.FI, InfoTextCategory.KANSALAISALOITE, 3, "3", "t", "t", null, "draftText", DateTime.now(), "name");
        testHelper.createInfoText(LanguageCode.FI, InfoTextCategory.KANSALAISALOITE, 5, "4", null, null, "t", "draftText", DateTime.now(), "n");

        testHelper.createInfoText(LanguageCode.FI, InfoTextCategory.KANSALAISALOITE, 4, "5", "t", "t", "t", "draftText", DateTime.now(), "n");

        List<InfoTextSubject> publicSubjectList = infoTextDao.getNotEmptySubjects(LanguageCode.FI);

        assertThat(publicSubjectList.size(), is(1));
        assertThat(publicSubjectList.get(0).getUri(), is("5"));

    }

    @Test
    public void get_public_list_returns_only_links_with_given_language() {
        testHelper.dbCleanup();
        testHelper.createInfoText(LanguageCode.FI, InfoTextCategory.KANSALAISALOITE, 10, "finnish_uri", "text", "text", "text", "text", new DateTime(), "name");
        testHelper.createInfoText(LanguageCode.SV, InfoTextCategory.KANSALAISALOITE, 10, "swedish_uri", "text", "text", "text", "text", new DateTime(), "name");

        List<InfoTextSubject> finnish_links = infoTextDao.getNotEmptySubjects(LanguageCode.FI);
        List<InfoTextSubject> swedish_links = infoTextDao.getNotEmptySubjects(LanguageCode.SV);

        assertThat(finnish_links.size(), is(1));
        assertThat(swedish_links.size(), is(1));
        assertThat(finnish_links.get(0).getUri(), is("finnish_uri"));
        assertThat(swedish_links.get(0).getUri(), is("swedish_uri"));

    }

    @Test
    public void saves_draft() {
        InfoPageText infoPageText = InfoPageText.builder(URI_FI)
                .withText("New subject", "New content")
                .withModifier("New modifier", new DateTime(2011, 5, 5, 0, 0))
                .build();
        infoTextDao.saveDraft(infoPageText);

        InfoPageText draft = infoTextDao.getDraft(URI_FI);

        assertThat(draft.getContent(), is("New content"));
        assertThat(draft.getSubject(), is("New subject"));
        assertThat(draft.getModifierName(), is("New modifier"));
        assertThat(draft.getModifyTime(), is(new DateTime(2011, 5, 5, 0, 0)));
    }

    @Test
    public void gets_footer_links_with_given_language(){
        Long dontShowAtFooter = testHelper.createInfoText(
                LanguageCode.FI,
                INFO_TEXT_CATEGORY, 2,
                "uri2",
                SUBJECT_FI,
                DRAFT_SUBJECT_FI,
                TEXT_FI,
                DRAFT_FI,
                MODIFIED, MODIFIER,
                false);
        Long showAtFooterButWrongLanguage = testHelper.createInfoText(
                LanguageCode.FI,
                INFO_TEXT_CATEGORY, 3,
                "uri3",
                SUBJECT_FI,
                DRAFT_SUBJECT_FI,
                TEXT_FI,
                DRAFT_FI,
                MODIFIED, MODIFIER,
                false);

        String wantedUri = "wanted uri";
        String wantedSubject = "wanted subject";
        Long showAtFooterCorrectLanguage = testHelper.createInfoText(
                LanguageCode.FI,
                INFO_TEXT_CATEGORY, 4,
                wantedUri,
                wantedSubject,
                DRAFT_SUBJECT_FI,
                TEXT_FI,
                DRAFT_FI,
                MODIFIED, MODIFIER,
                true);

        List<InfoTextFooterLink> footerLinks = infoTextDao.getFooterLinks(LanguageCode.FI);
        assertThat(footerLinks.size(), is(1));
        assertThat(footerLinks.get(0).getSubject(), is(wantedSubject));
        assertThat(footerLinks.get(0).getUri(), is(wantedUri));
    }

    @Test
    public void unwanted_html_tags_are_not_loaded_as_content_when_getting_published_or_draft() {
        testHelper.createInfoText(LanguageCode.FI,
                INFO_TEXT_CATEGORY, 2,
                "uri2",
                SUBJECT_FI,
                DRAFT_SUBJECT_FI,
                "published <script> tag",
                "draft <script/> tag",
                MODIFIED, MODIFIER);

        InfoPageText draft = infoTextDao.getDraft("uri2");
        assertThat(draft.getContent(), containsString("draft"));
        assertThat(draft.getContent(), not(containsString("script")));

        InfoPageText published = infoTextDao.getPublished("uri2");
        assertThat(published.getContent(), containsString("published"));
        assertThat(published.getContent(), not(containsString("script")));
    }
}
