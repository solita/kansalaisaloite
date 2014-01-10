package fi.om.initiative.web;

import fi.om.initiative.dto.search.InitiativeSearch;
import fi.om.initiative.dto.search.OrderBy;
import fi.om.initiative.dto.search.SearchView;
import fi.om.initiative.dto.search.Show;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SearchParameterGeneratorTest {

    InitiativeSearch initiativeSearch;

    @Before
    public void setup() {
        initiativeSearch = new InitiativeSearch();
        initiativeSearch.setSearchView(SearchView.own);
        initiativeSearch.setOffset(5);
        initiativeSearch.setLimit(10);
        initiativeSearch.setOrderBy(OrderBy.id);
        initiativeSearch.setShow(Show.running);
    }

    @Test
    public void generatesAllFieldsAsRequired() {
        String parameters = SearchParameterGenerator.generateParameters(initiativeSearch);
        assertThat(parameters, is("?searchView=own&offset=5&limit=10&orderBy=id&show=running&minSupportCount=50"));
    }

    @Test
    public void changes_limit_and_clears_offset() {
        String parameters = new SearchParameterGenerator(initiativeSearch).getWithLimit(17);
        assertThat(parameters, is("?searchView=own&offset=0&limit=17&orderBy=id&show=running&minSupportCount=50"));
    }

    @Test
    public void remove_limit() {
        String parameters = new SearchParameterGenerator(initiativeSearch).getWithMaxLimit();
        assertThat(parameters, is("?searchView=own&offset=0&limit=500&orderBy=id&show=running&minSupportCount=50"));
    }

    @Test
    public void changes_offset() {
        String parameters = new SearchParameterGenerator(initiativeSearch).getWithOffset(12);
        assertThat(parameters, is("?searchView=own&offset=12&limit=10&orderBy=id&show=running&minSupportCount=50"));
    }

    @Test
    public void sets_minsupportcount() {
        String parameters = new SearchParameterGenerator(initiativeSearch.setMinSupportCount(10)).getWithOrderById();
        assertThat(parameters, is("?searchView=own&offset=5&limit=10&orderBy=id&show=running&minSupportCount=10"));
    }

    @Test
    public void orderBy_id() {
        initiativeSearch.setOrderBy(null);
        String parameters = new SearchParameterGenerator(initiativeSearch).getWithOrderById();
        assertThat(parameters, is("?searchView=own&offset=5&limit=10&orderBy=id&show=running&minSupportCount=50"));
    }

    @Test
    public void orderBy_mostTimeLeft() {
        String parameters = new SearchParameterGenerator(initiativeSearch).getWithOrderByMostTimeLeft();
        assertThat(parameters, is("?searchView=own&offset=5&limit=10&orderBy=mostTimeLeft&show=running&minSupportCount=50"));
    }

    @Test
    public void orderBy_leastTimeLeft() {
        String parameters = new SearchParameterGenerator(initiativeSearch).getWithOrderByLeastTimeLeft();
        assertThat(parameters, is("?searchView=own&offset=5&limit=10&orderBy=leastTimeLeft&show=running&minSupportCount=50"));
    }

    @Test
    public void orderBy_createdOldest() {
        String parameters = new SearchParameterGenerator(initiativeSearch).getWithOrderByCreatedOldest();
        assertThat(parameters, is("?searchView=own&offset=5&limit=10&orderBy=createdOldest&show=running&minSupportCount=50"));
    }

    @Test
    public void orderBy_createdNewest() {
        String parameters = new SearchParameterGenerator(initiativeSearch).getWithOrderByCreatedNewest();
        assertThat(parameters, is("?searchView=own&offset=5&limit=10&orderBy=createdNewest&show=running&minSupportCount=50"));
    }

    @Test
    public void orderBy_mostSupports() {
        String parameters = new SearchParameterGenerator(initiativeSearch).getWithOrderByMostSupports();
        assertThat(parameters, is("?searchView=own&offset=5&limit=10&orderBy=mostSupports&show=running&minSupportCount=50"));
    }

    @Test
    public void orderBy_leastSupports() {
        String parameters = new SearchParameterGenerator(initiativeSearch).getWithOrderByLeastSupports();
        assertThat(parameters, is("?searchView=own&offset=5&limit=10&orderBy=leastSupports&show=running&minSupportCount=50"));
    }

    @Test
    public void show_only_running() {
        initiativeSearch.setShow(Show.canceled);
        String parameters = new SearchParameterGenerator(initiativeSearch).getWithStateRunning();
        assertThat(parameters, is("?searchView=own&orderBy=mostTimeLeft&show=running&minSupportCount=50"));
    }

    @Test
    public void show_only_waiting() {
        initiativeSearch.setShow(Show.waiting);
        String parameters = new SearchParameterGenerator(initiativeSearch).getWithStateWaiting();
        assertThat(parameters, is("?searchView=own&orderBy=createdNewest&show=waiting&minSupportCount=0"));
    }

    @Test
    public void show_only_ended() {
        String parameters = new SearchParameterGenerator(initiativeSearch).getWithStateEnded();
        assertThat(parameters, is("?searchView=own&orderBy=createdNewest&show=ended&minSupportCount=50"));
    }

    @Test
    public void show_only_sentToParliament() {
        String parameters = new SearchParameterGenerator(initiativeSearch).getWithStateSentToParliament();
        assertThat(parameters, is("?searchView=own&orderBy=createdNewest&show=sentToParliament&minSupportCount=50"));
    }

    @Test
    public void show_only_canceled() {
        String parameters = new SearchParameterGenerator(initiativeSearch).getWithStateCanceled();
        assertThat(parameters, is("?searchView=own&orderBy=createdNewest&show=canceled&minSupportCount=50"));
    }

    @Test
    public void show_all() {
        String parameters = new SearchParameterGenerator(initiativeSearch).getWithStateAll();
        assertThat(parameters, is("?searchView=own&orderBy=createdNewest&show=all&minSupportCount=50"));
    }

    @Test
    public void show_only_on_preparations() {
        String parameters = new SearchParameterGenerator(initiativeSearch).getWithStatePreparation();
        assertThat(parameters, is("?searchView=own&orderBy=createdNewest&show=preparation&minSupportCount=0"));
    }

    @Test
    public void show_only_reviews() {
        String parameters = new SearchParameterGenerator(initiativeSearch).getWithStateReview();
        assertThat(parameters, is("?searchView=own&orderBy=createdNewest&show=review&minSupportCount=0"));
    }

    @Test
    public void show_only_omAll() {
        String parameters = new SearchParameterGenerator(initiativeSearch).getWithStateOmAll();
        assertThat(parameters, is("?searchView=own&orderBy=createdNewest&show=omAll&minSupportCount=0"));
    }

    @Test
    public void show_only_omCanceled() {
        String parameters = new SearchParameterGenerator(initiativeSearch).getWithStateOmCanceled();
        assertThat(parameters, is("?searchView=own&orderBy=createdNewest&show=omCanceled&minSupportCount=0"));
    }

    @Test
    public void show_only_closeToTermination() {
        String parameters = new SearchParameterGenerator(initiativeSearch).getWithStateCloseToTermination();
        assertThat(parameters, is("?searchView=own&orderBy=createdNewest&show=closeToTermination&minSupportCount=0"));
    }

    @Test
    public void show_looser_initiatives_sets_minSupportCount_to_zero_and_clears_offset() {
        String parameters = new SearchParameterGenerator(initiativeSearch).getWithShowLooserInitiatives();
        assertThat(parameters, is("?searchView=own&offset=0&limit=10&orderBy=id&show=running&minSupportCount=0"));
    }

    @Test
    public void hide_looser_initiatives_sets_minSupportCount_to_fifty_and_clears_offset() {
        String parameters = new SearchParameterGenerator(initiativeSearch).getWithHideLooserInitiatives();
        assertThat(parameters, is("?searchView=own&offset=0&limit=10&orderBy=id&show=running&minSupportCount=50"));
    }


}
