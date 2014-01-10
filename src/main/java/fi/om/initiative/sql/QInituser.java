package fi.om.initiative.sql;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;


/**
 * QInituser is a Querydsl query type for QInituser
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QInituser extends com.mysema.query.sql.RelationalPathBase<QInituser> {

    private static final long serialVersionUID = 1467712303;

    public static final QInituser inituser = new QInituser("inituser");

    public final DatePath<org.joda.time.LocalDate> dateofbirth = createDate("dateofbirth", org.joda.time.LocalDate.class);

    public final StringPath firstnames = createString("firstnames");

    public final StringPath hash = createString("hash");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<org.joda.time.DateTime> lastlogin = createDateTime("lastlogin", org.joda.time.DateTime.class);

    public final StringPath lastname = createString("lastname");

    public final BooleanPath om = createBoolean("om");

    public final BooleanPath vrk = createBoolean("vrk");

    public final com.mysema.query.sql.PrimaryKey<QInituser> inituserPk = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QInitiative> _initiativeModifierIdFk = createInvForeignKey(id, "modifier_id");

    public final com.mysema.query.sql.ForeignKey<QInitiativeAuthor> _authorUserIdFk = createInvForeignKey(id, "user_id");

    public QInituser(String variable) {
        super(QInituser.class,  forVariable(variable), "initiative", "inituser");
        addMetadata();
    }

    public QInituser(Path<? extends QInituser> path) {
        super(path.getType(), path.getMetadata(), "initiative", "inituser");
        addMetadata();
    }

    public QInituser(PathMetadata<?> metadata) {
        super(QInituser.class,  metadata, "initiative", "inituser");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(dateofbirth, ColumnMetadata.named("dateofbirth").ofType(91).withSize(13).notNull());
        addMetadata(firstnames, ColumnMetadata.named("firstnames").ofType(12).withSize(256).notNull());
        addMetadata(hash, ColumnMetadata.named("hash").ofType(12).withSize(64).notNull());
        addMetadata(id, ColumnMetadata.named("id").ofType(-5).withSize(19).notNull());
        addMetadata(lastlogin, ColumnMetadata.named("lastlogin").ofType(93).withSize(29).withDigits(6));
        addMetadata(lastname, ColumnMetadata.named("lastname").ofType(12).withSize(256).notNull());
        addMetadata(om, ColumnMetadata.named("om").ofType(-7).withSize(1).notNull());
        addMetadata(vrk, ColumnMetadata.named("vrk").ofType(-7).withSize(1).notNull());
    }

}

