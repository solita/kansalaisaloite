package fi.om.initiative.sql;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;


/**
 * QInitiativeSupportVoteDay is a Querydsl query type for QInitiativeSupportVoteDay
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QInitiativeSupportVoteDay extends com.mysema.query.sql.RelationalPathBase<QInitiativeSupportVoteDay> {

    private static final long serialVersionUID = -749526781;

    public static final QInitiativeSupportVoteDay initiativeSupportVoteDay = new QInitiativeSupportVoteDay("initiative_support_vote_day");

    public final NumberPath<Long> initiativeId = createNumber("initiativeId", Long.class);

    public final NumberPath<Integer> supportCount = createNumber("supportCount", Integer.class);

    public final DatePath<org.joda.time.LocalDate> supportDate = createDate("supportDate", org.joda.time.LocalDate.class);

    public final com.mysema.query.sql.PrimaryKey<QInitiativeSupportVoteDay> supportVoteDayPk = createPrimaryKey(initiativeId, supportDate);

    public final com.mysema.query.sql.ForeignKey<QInitiative> supportVoteDayInitiativeIdFk = createForeignKey(initiativeId, "id");

    public QInitiativeSupportVoteDay(String variable) {
        super(QInitiativeSupportVoteDay.class,  forVariable(variable), "initiative", "initiative_support_vote_day");
        addMetadata();
    }

    public QInitiativeSupportVoteDay(Path<? extends QInitiativeSupportVoteDay> path) {
        super(path.getType(), path.getMetadata(), "initiative", "initiative_support_vote_day");
        addMetadata();
    }

    public QInitiativeSupportVoteDay(PathMetadata<?> metadata) {
        super(QInitiativeSupportVoteDay.class,  metadata, "initiative", "initiative_support_vote_day");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(initiativeId, ColumnMetadata.named("initiative_id").ofType(-5).withSize(19).notNull());
        addMetadata(supportCount, ColumnMetadata.named("support_count").ofType(4).withSize(10).notNull());
        addMetadata(supportDate, ColumnMetadata.named("support_date").ofType(91).withSize(13).notNull());
    }

}

