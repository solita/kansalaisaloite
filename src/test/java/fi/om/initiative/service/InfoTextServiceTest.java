package fi.om.initiative.service;

import com.google.common.collect.Lists;
import fi.om.initiative.dao.InfoTextDao;
import fi.om.initiative.dto.InfoTextCategory;
import fi.om.initiative.dto.InfoTextSubject;
import fi.om.initiative.dto.LanguageCode;
import fi.om.initiative.util.Locales;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class InfoTextServiceTest {

    InfoTextService infoTextService;

    @Mocked
    InfoTextDao infoTextDaoMock;

    @Mocked
    UserService userService;

    @Before
    public void setup() {
        infoTextService = new InfoTextService(infoTextDaoMock, userService);
    }

    @Test
    public void groups_subjects_by_category() {

        final List<InfoTextSubject> list = Lists.newArrayList();
        list.add(new InfoTextSubject(InfoTextCategory.KANSALAISALOITE, "uri1", ""));
        list.add(new InfoTextSubject(InfoTextCategory.KANSALAISALOITE_FI, "uri2", ""));
        list.add(new InfoTextSubject(InfoTextCategory.KANSALAISALOITE, "uri3", ""));

        new Expectations() {{
            infoTextDaoMock.getAllSubjects(LanguageCode.FI); result = list;
        }};

        Map<String,List<InfoTextSubject>> map = infoTextService.getOmSubjectList(Locales.LOCALE_FI);

        assertThat(map.get(InfoTextCategory.KANSALAISALOITE.name()).size(), is(2));
        assertThat(map.get(InfoTextCategory.KANSALAISALOITE_FI.name()).size(), is(1));

    }

    @Test
    public void wont_fail_if_empty_list() {
        new Expectations() {{
            infoTextDaoMock.getAllSubjects(LanguageCode.FI); result = new ArrayList<InfoTextSubject>();
        }};

        Map<String, List<InfoTextSubject>> map = infoTextService.getOmSubjectList(Locales.LOCALE_FI);

        assertThat(map.size(), is(InfoTextCategory.values().length));
        for (List<InfoTextSubject> categorySubjects : map.values()) {
            assertThat(categorySubjects.size(), is(0));
        }
    }


}
