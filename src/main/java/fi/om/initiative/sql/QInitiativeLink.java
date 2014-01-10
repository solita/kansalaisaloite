package fi.om.initiative.sql;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;


/**
 * QInitiativeLink is a Querydsl query type for QInitiativeLink
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QInitiativeLink extends com.mysema.query.sql.RelationalPathBase<QInitiativeLink> {

    private static final long serialVersionUID = 1052366714;

    public static final QInitiativeLink initiativeLink = new QInitiativeLink("initiative_link");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> initiativeId = createNumber("initiativeId", Long.class);

    public final StringPath label = createString("label");

    public final StringPath uri = createString("uri");

    public final com.mysema.query.sql.PrimaryKey<QInitiativeLink> linkPk = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QInitiative> linkInitiativeIdFk = createForeignKey(initiativeId, "id");

    public QInitiativeLink(String variable) {
        super(QInitiativeLink.class,  forVariable(variable), "initiative", "initiative_link");
        addMetadata();
    }

    public QInitiativeLink(Path<? extends QInitiativeLink> path) {
        super(path.getType(), path.getMetadata(), "initiative", "initiative_link");
        addMetadata();
    }

    public QInitiativeLink(PathMetadata<?> metadata) {
        super(QInitiativeLink.class,  metadata, "initiative", "initiative_link");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("id").ofType(-5).withSize(19).notNull());
        addMetadata(initiativeId, ColumnMetadata.named("initiative_id").ofType(-5).withSize(19).notNull());
        addMetadata(label, ColumnMetadata.named("label").ofType(12).withSize(128).notNull());
        addMetadata(uri, ColumnMetadata.named("uri").ofType(12).withSize(4096).notNull());
    }

}

