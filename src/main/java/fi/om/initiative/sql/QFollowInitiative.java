package fi.om.initiative.sql;

import com.mysema.query.sql.ColumnMetadata;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;

import javax.annotation.Generated;

import static com.mysema.query.types.PathMetadataFactory.forVariable;


/**
 * QFollowInitiative is a Querydsl query type for QFollowInitiative
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QFollowInitiative extends com.mysema.query.sql.RelationalPathBase<QFollowInitiative> {

    private static final long serialVersionUID = 959906545;

    public static final QFollowInitiative followInitiative = new QFollowInitiative("follow_initiative");

    public final StringPath email = createString("email");

    public final NumberPath<Long> initiativeId = createNumber("initiativeId", Long.class);

    public final StringPath unsubscribeHash = createString("unsubscribeHash");

    public final com.mysema.query.sql.PrimaryKey<QFollowInitiative> followInitiativePkey = createPrimaryKey(initiativeId, email);

    public final com.mysema.query.sql.ForeignKey<QInitiative> followInitiativeInitiativeId = createForeignKey(initiativeId, "id");

    public QFollowInitiative(String variable) {
        super(QFollowInitiative.class,  forVariable(variable), "initiative", "follow_initiative");
        addMetadata();
    }

    public QFollowInitiative(Path<? extends QFollowInitiative> path) {
        super(path.getType(), path.getMetadata(), "initiative", "follow_initiative");
        addMetadata();
    }

    public QFollowInitiative(PathMetadata<?> metadata) {
        super(QFollowInitiative.class,  metadata, "initiative", "follow_initiative");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(email, ColumnMetadata.named("email").ofType(12).withSize(100).notNull());
        addMetadata(initiativeId, ColumnMetadata.named("initiative_id").ofType(-5).withSize(19).notNull());
        addMetadata(unsubscribeHash, ColumnMetadata.named("unsubscribe_hash").ofType(12).withSize(40).notNull());
    }

}

