package fi.om.initiative.sql;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;


/**
 * QSupportVoteBatch is a Querydsl query type for QSupportVoteBatch
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QSupportVoteBatch extends com.mysema.query.sql.RelationalPathBase<QSupportVoteBatch> {

    private static final long serialVersionUID = -1435404875;

    public static final QSupportVoteBatch supportVoteBatch = new QSupportVoteBatch("support_vote_batch");

    public final DateTimePath<org.joda.time.DateTime> created = createDateTime("created", org.joda.time.DateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> initiativeId = createNumber("initiativeId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QSupportVoteBatch> supportVoteBatchPk = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QInitiative> supportVoteBatchInitiativeIdFk = createForeignKey(initiativeId, "id");

    public final com.mysema.query.sql.ForeignKey<QSupportVote> _supportVoteBatchIdFk = createInvForeignKey(id, "batch_id");

    public QSupportVoteBatch(String variable) {
        super(QSupportVoteBatch.class,  forVariable(variable), "initiative", "support_vote_batch");
        addMetadata();
    }

    public QSupportVoteBatch(Path<? extends QSupportVoteBatch> path) {
        super(path.getType(), path.getMetadata(), "initiative", "support_vote_batch");
        addMetadata();
    }

    public QSupportVoteBatch(PathMetadata<?> metadata) {
        super(QSupportVoteBatch.class,  metadata, "initiative", "support_vote_batch");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(created, ColumnMetadata.named("created").ofType(93).withSize(29).withDigits(6).notNull());
        addMetadata(id, ColumnMetadata.named("id").ofType(-5).withSize(19).notNull());
        addMetadata(initiativeId, ColumnMetadata.named("initiative_id").ofType(-5).withSize(19).notNull());
    }

}

