package fi.om.initiative.sql;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;


/**
 * QSchemaVersion is a Querydsl query type for QSchemaVersion
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QSchemaVersion extends com.mysema.query.sql.RelationalPathBase<QSchemaVersion> {

    private static final long serialVersionUID = -158125533;

    public static final QSchemaVersion schemaVersion = new QSchemaVersion("schema_version");

    public final DateTimePath<org.joda.time.DateTime> executed = createDateTime("executed", org.joda.time.DateTime.class);

    public final StringPath script = createString("script");

    public final com.mysema.query.sql.PrimaryKey<QSchemaVersion> schemaVersionScriptPk = createPrimaryKey(script);

    public QSchemaVersion(String variable) {
        super(QSchemaVersion.class,  forVariable(variable), "initiative", "schema_version");
        addMetadata();
    }

    public QSchemaVersion(Path<? extends QSchemaVersion> path) {
        super(path.getType(), path.getMetadata(), "initiative", "schema_version");
        addMetadata();
    }

    public QSchemaVersion(PathMetadata<?> metadata) {
        super(QSchemaVersion.class,  metadata, "initiative", "schema_version");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(executed, ColumnMetadata.named("executed").ofType(93).withSize(29).withDigits(6).notNull());
        addMetadata(script, ColumnMetadata.named("script").ofType(12).withSize(64).notNull());
    }

}

