package fi.om.initiative.sql;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;


/**
 * QSupportVote is a Querydsl query type for QSupportVote
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QSupportVote extends com.mysema.query.sql.RelationalPathBase<QSupportVote> {

    private static final long serialVersionUID = 490765445;

    public static final QSupportVote supportVote = new QSupportVote("support_vote");

    public final NumberPath<Long> batchId = createNumber("batchId", Long.class);

    public final DateTimePath<org.joda.time.DateTime> created = createDateTime("created", org.joda.time.DateTime.class);

    public final StringPath details = createString("details");

    public final NumberPath<Long> initiativeId = createNumber("initiativeId", Long.class);

    public final StringPath supportid = createString("supportid");

    public final com.mysema.query.sql.PrimaryKey<QSupportVote> supportVotePk = createPrimaryKey(initiativeId, supportid);

    public final com.mysema.query.sql.ForeignKey<QInitiative> supportVoteInitiativeIdFk = createForeignKey(initiativeId, "id");

    public final com.mysema.query.sql.ForeignKey<QSupportVoteBatch> supportVoteBatchIdFk = createForeignKey(batchId, "id");

    public QSupportVote(String variable) {
        super(QSupportVote.class,  forVariable(variable), "initiative", "support_vote");
        addMetadata();
    }

    public QSupportVote(Path<? extends QSupportVote> path) {
        super(path.getType(), path.getMetadata(), "initiative", "support_vote");
        addMetadata();
    }

    public QSupportVote(PathMetadata<?> metadata) {
        super(QSupportVote.class,  metadata, "initiative", "support_vote");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(batchId, ColumnMetadata.named("batch_id").ofType(-5).withSize(19));
        addMetadata(created, ColumnMetadata.named("created").ofType(93).withSize(29).withDigits(6).notNull());
        addMetadata(details, ColumnMetadata.named("details").ofType(12).withSize(4096).notNull());
        addMetadata(initiativeId, ColumnMetadata.named("initiative_id").ofType(-5).withSize(19).notNull());
        addMetadata(supportid, ColumnMetadata.named("supportid").ofType(12).withSize(64).notNull());
    }

}

