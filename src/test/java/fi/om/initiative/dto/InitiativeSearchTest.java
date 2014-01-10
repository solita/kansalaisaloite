package fi.om.initiative.dto;

import fi.om.initiative.dto.search.InitiativeSearch;
import fi.om.initiative.dto.search.OrderBy;
import fi.om.initiative.dto.search.SearchView;
import fi.om.initiative.dto.search.Show;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class InitiativeSearchTest {

    @Test
    public void copy_all_fields() throws CloneNotSupportedException, IllegalAccessException {

        InitiativeSearch original = new InitiativeSearch();
        original.setOrderBy(OrderBy.mostSupports);
        original.setOffset(25);
        original.setLimit(50);
        original.setSearchView(SearchView.om);
        original.setShow(Show.sentToParliament);
        original.setMinSupportCount(30);

        InitiativeSearch clone = original.copy();

        for (Field field : original.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            assertThat(field, is(not(nullValue())));
            assertThat(field.get(original), is(field.get(clone)));
        }

    }
}
