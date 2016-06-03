package fi.om.initiative.sql;

import com.mysema.query.sql.ColumnMetadata;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.BooleanPath;
import com.mysema.query.types.path.DateTimePath;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;

import javax.annotation.Generated;

import static com.mysema.query.types.PathMetadataFactory.forVariable;


/**
 * QFlywaySchema is a Querydsl query type for QFlywaySchema
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QFlywaySchema extends com.mysema.query.sql.RelationalPathBase<QFlywaySchema> {

    private static final long serialVersionUID = 462582449;

    public static final QFlywaySchema flywaySchema = new QFlywaySchema("flyway_schema");

    public final NumberPath<Integer> checksum = createNumber("checksum", Integer.class);

    public final StringPath description = createString("description");

    public final NumberPath<Integer> executionTime = createNumber("executionTime", Integer.class);

    public final StringPath installedBy = createString("installedBy");

    public final DateTimePath<org.joda.time.DateTime> installedOn = createDateTime("installedOn", org.joda.time.DateTime.class);

    public final NumberPath<Integer> installedRank = createNumber("installedRank", Integer.class);

    public final StringPath script = createString("script");

    public final BooleanPath success = createBoolean("success");

    public final StringPath type = createString("type");

    public final StringPath version = createString("version");

    public final NumberPath<Integer> versionRank = createNumber("versionRank", Integer.class);

    public final com.mysema.query.sql.PrimaryKey<QFlywaySchema> flywaySchemaPk = createPrimaryKey(version);

    public QFlywaySchema(String variable) {
        super(QFlywaySchema.class,  forVariable(variable), "initiative", "flyway_schema");
        addMetadata();
    }

    public QFlywaySchema(Path<? extends QFlywaySchema> path) {
        super(path.getType(), path.getMetadata(), "initiative", "flyway_schema");
        addMetadata();
    }

    public QFlywaySchema(PathMetadata<?> metadata) {
        super(QFlywaySchema.class,  metadata, "initiative", "flyway_schema");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(checksum, ColumnMetadata.named("checksum").ofType(4).withSize(10));
        addMetadata(description, ColumnMetadata.named("description").ofType(12).withSize(200).notNull());
        addMetadata(executionTime, ColumnMetadata.named("execution_time").ofType(4).withSize(10).notNull());
        addMetadata(installedBy, ColumnMetadata.named("installed_by").ofType(12).withSize(100).notNull());
        addMetadata(installedOn, ColumnMetadata.named("installed_on").ofType(93).withSize(29).withDigits(6).notNull());
        addMetadata(installedRank, ColumnMetadata.named("installed_rank").ofType(4).withSize(10).notNull());
        addMetadata(script, ColumnMetadata.named("script").ofType(12).withSize(1000).notNull());
        addMetadata(success, ColumnMetadata.named("success").ofType(-7).withSize(1).notNull());
        addMetadata(type, ColumnMetadata.named("type").ofType(12).withSize(20).notNull());
        addMetadata(version, ColumnMetadata.named("version").ofType(12).withSize(50).notNull());
        addMetadata(versionRank, ColumnMetadata.named("version_rank").ofType(4).withSize(10).notNull());
    }

}

