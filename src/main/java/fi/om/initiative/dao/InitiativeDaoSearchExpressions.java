package fi.om.initiative.dao;

import com.mysema.query.types.Expression;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.expr.DateTimeExpression;
import fi.om.initiative.dto.InitiativeSettings;
import fi.om.initiative.dto.initiative.InitiativeState;
import fi.om.initiative.sql.QInitiative;
import org.joda.time.LocalDate;

public class InitiativeDaoSearchExpressions {

    private static final QInitiative qInitiative = QInitiative.initiative;
    private static final Expression<LocalDate> CURRENT_DATE = DateTimeExpression.currentDate(LocalDate.class);

    public static BooleanExpression INITIATIVE_IS_RUNNING(InitiativeSettings.MinSupportCountSettings minSupportCountSettings) {
        return qInitiative.state.in(InitiativeState.ACCEPTED)
                .and(qInitiative.enddate.goe(CURRENT_DATE))
                .and(qInitiative.startdate.loe(CURRENT_DATE))
                .and(qInitiative.supportcount.add(qInitiative.externalsupportcount)
                        .goe(minSupportCountSettings.requiredMinSupportCountForSearch)
                        .or(qInitiative.startdate.goe(minSupportCountSettings.startDateNotBefore))
                );
    }

    public static BooleanExpression INITIATIVE_IS_ENDED(InitiativeSettings.MinSupportCountSettings minSupportCountSettings) {
        return qInitiative.state.eq(InitiativeState.ACCEPTED)
                .and(qInitiative.enddate.lt(CURRENT_DATE)
                        .or(qInitiative.supportcount.add(qInitiative.externalsupportcount)
                                .lt(minSupportCountSettings.requiredMinSupportCountForSearch)
                                .and(qInitiative.startdate.lt(minSupportCountSettings.startDateNotBefore))));
    }

    public static final BooleanExpression INITIATIVE_IS_CANCELED
            = qInitiative.state.eq(InitiativeState.CANCELED)
            .and(qInitiative.acceptanceidentifier.isNotNull());

    public static final BooleanExpression INITIATIVE_IS_SENT_TO_PARLIAMENT
            = qInitiative.state.eq(InitiativeState.DONE);

    public static final BooleanExpression INITIATIVE_IS_WAITING
            = qInitiative.state.eq(InitiativeState.ACCEPTED).and(qInitiative.startdate.gt(CURRENT_DATE));

    public static BooleanExpression INITIATIVE_PUBLIC_ALL() {
            return qInitiative.state.eq(InitiativeState.ACCEPTED) // RUNNING or ENDED or STARTING
            .or(INITIATIVE_IS_CANCELED)
            .or(INITIATIVE_IS_SENT_TO_PARLIAMENT);
    }

    public static BooleanExpression INITIATIVE_STATES(InitiativeState... states) {
        return qInitiative.state.in(states);
    }

    public static BooleanExpression INITIATIVE_IS_REVIEW
            = INITIATIVE_STATES(InitiativeState.REVIEW);

    public static BooleanExpression INITIATIVE_IS_OM_CANCELED
            = INITIATIVE_STATES(InitiativeState.CANCELED);

    public static BooleanExpression INITIATIVE_IS_PREPARATION
            = INITIATIVE_STATES(InitiativeState.DRAFT, InitiativeState.PROPOSAL);
}
