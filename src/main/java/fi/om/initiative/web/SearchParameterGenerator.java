package fi.om.initiative.web;

import fi.om.initiative.dto.search.InitiativeSearch;
import fi.om.initiative.dto.search.OrderBy;
import fi.om.initiative.dto.search.Show;

import java.lang.reflect.Field;

/**
 * Freemarker uses this class for generating links with get-parameters at search page.
 */
public class SearchParameterGenerator {


    private final InitiativeSearch original;

    public SearchParameterGenerator(InitiativeSearch search) {
        this.original = search;
    }

    static String generateParameters(InitiativeSearch search) {

        StringBuilder builder = new StringBuilder();

        for (Field field : search.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            addFieldValue(builder, field, search);
        }

        return builder.toString();

    }

    private static void addFieldValue(StringBuilder builder, Field field, InitiativeSearch search){
        try {
            Object fieldValue = field.get(search);
            if (fieldValue != null) {
                appendParameter(builder, field.getName(), fieldValue);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private static void appendParameter(StringBuilder builder, String name, Object fieldValue) {
        if (builder.length() > 0) {
            builder.append("&");
        }
        else {
            builder.append("?");
        }
        builder.append(name)
                .append("=")
                .append(fieldValue.toString());
    }

    public String getWithLimit(int limit) {
        return generateParameters(original.copy().setOffset(0).setLimit(limit));
    }

    public String getWithOffset(int offset) {
        return generateParameters(original.copy().setOffset(offset));
    }

    public String getWithOrderById() {
        return generateParameters(original.copy().setOrderBy(OrderBy.id));
    }

    public String getWithOrderByMostTimeLeft() {
        return generateParameters(original.copy().setOrderBy(OrderBy.mostTimeLeft));
    }

    public String getWithOrderByLeastTimeLeft() {
        return generateParameters(original.copy().setOrderBy(OrderBy.leastTimeLeft));
    }

    public String getWithOrderByMostSupports() {
        return generateParameters(original.copy().setOrderBy(OrderBy.mostSupports));
    }

    public String getWithOrderByLeastSupports() {
        return generateParameters(original.copy().setOrderBy(OrderBy.leastSupports));
    }

    public String getWithOrderByCreatedNewest() {
        return generateParameters(original.copy().setOrderBy(OrderBy.createdNewest));
    }

    public String getWithOrderByCreatedOldest() {
        return generateParameters(original.copy().setOrderBy(OrderBy.createdOldest));
    }

    public String getWithMaxLimit() {
        return generateParameters(original.copy().setOffset(0).setLimit(Urls.MAX_INITIATIVE_SEARCH_LIMIT));
    }

    public String getWithStateRunning() {
        return generateParameters(new InitiativeSearch()
                .setOrderBy(OrderBy.mostTimeLeft)
                .setSearchView(original.getSearchView())
                .setShow(Show.running));
    }

    public String getWithStateEnded() {
        return generateParameters(new InitiativeSearch()
                .setOrderBy(OrderBy.createdNewest)
                .setSearchView(original.getSearchView())
                .setShow(Show.ended));
    }

    public String getWithStateSentToParliament() {
        return generateParameters(new InitiativeSearch()
                .setOrderBy(OrderBy.createdNewest)
                .setSearchView(original.getSearchView())
                .setShow(Show.sentToParliament));
    }

    public String getWithStateCanceled() {
        return generateParameters(new InitiativeSearch()
                .setOrderBy(OrderBy.createdNewest)
                .setSearchView(original.getSearchView())
                .setShow(Show.canceled));
    }

    public String getWithStateAll() {
        return generateParameters(new InitiativeSearch()
                .setOrderBy(OrderBy.createdNewest)
                .setSearchView(original.getSearchView())
                .setShow(Show.all));
    }

    public String getWithStateWaiting() {
        return generateParameters(new InitiativeSearch()
                .setOrderBy(OrderBy.createdNewest)
                .setSearchView(original.getSearchView())
                .setShow(Show.waiting)
                .setMinSupportCount(0));
    }

    public String getWithStateReview() {
        return generateParameters(new InitiativeSearch()
                .setOrderBy(OrderBy.createdNewest)
                .setSearchView(original.getSearchView())
                .setShow(Show.review)
                .setMinSupportCount(0));
    }

    public String getWithStatePreparation() {
        return generateParameters(new InitiativeSearch()
                .setOrderBy(OrderBy.createdNewest)
                .setSearchView(original.getSearchView())
                .setShow(Show.preparation)
                .setMinSupportCount(0));
    }

    public String getWithStateOmAll() {
        return generateParameters(new InitiativeSearch()
                .setOrderBy(OrderBy.createdNewest)
                .setSearchView(original.getSearchView())
                .setShow(Show.omAll)
                .setMinSupportCount(0));
    }

    public String getWithStateOmCanceled() {
        return generateParameters(new InitiativeSearch()
                .setOrderBy(OrderBy.createdNewest)
                .setSearchView(original.getSearchView())
                .setShow(Show.omCanceled)
                .setMinSupportCount(0));
    }

    public String getWithStateCloseToTermination() {
        return generateParameters(new InitiativeSearch()
                .setOrderBy(OrderBy.createdNewest)
                .setSearchView(original.getSearchView())
                .setShow(Show.closeToTermination)
                .setMinSupportCount(0));
    }

    public String getWithShowLooserInitiatives() {
        return generateParameters(original.copy().setOffset(0).setMinSupportCount(0));
    }

    public String getWithHideLooserInitiatives() {
        return generateParameters(original.copy().setOffset(0).setMinSupportCount(Urls.DEFAULT_INITIATIVE_MINSUPPORTCOUNT));
    }
}
