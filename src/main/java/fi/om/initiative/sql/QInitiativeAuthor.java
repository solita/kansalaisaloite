package fi.om.initiative.sql;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;


/**
 * QInitiativeAuthor is a Querydsl query type for QInitiativeAuthor
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QInitiativeAuthor extends com.mysema.query.sql.RelationalPathBase<QInitiativeAuthor> {

    private static final long serialVersionUID = 1703438603;

    public static final QInitiativeAuthor initiativeAuthor = new QInitiativeAuthor("initiative_author");

    public final StringPath address = createString("address");

    public final DateTimePath<org.joda.time.DateTime> confirmationrequestsent = createDateTime("confirmationrequestsent", org.joda.time.DateTime.class);

    public final DateTimePath<org.joda.time.DateTime> confirmed = createDateTime("confirmed", org.joda.time.DateTime.class);

    public final DateTimePath<org.joda.time.DateTime> created = createDateTime("created", org.joda.time.DateTime.class);

    public final StringPath email = createString("email");

    public final StringPath firstnames = createString("firstnames");

    public final StringPath homemunicipalityFi = createString("homemunicipalityFi");

    public final StringPath homemunicipalitySv = createString("homemunicipalitySv");

    public final NumberPath<Long> initiativeId = createNumber("initiativeId", Long.class);

    public final BooleanPath initiator = createBoolean("initiator");

    public final StringPath lastname = createString("lastname");

    public final StringPath phone = createString("phone");

    public final EnumPath<fi.om.initiative.dto.author.AuthorRole> role = createEnum("role", fi.om.initiative.dto.author.AuthorRole.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QInitiativeAuthor> authorPk = createPrimaryKey(initiativeId, userId);

    public final com.mysema.query.sql.ForeignKey<QInituser> authorUserIdFk = createForeignKey(userId, "id");

    public final com.mysema.query.sql.ForeignKey<QInitiative> authorInitiativeIdFk = createForeignKey(initiativeId, "id");

    public QInitiativeAuthor(String variable) {
        super(QInitiativeAuthor.class,  forVariable(variable), "initiative", "initiative_author");
        addMetadata();
    }

    public QInitiativeAuthor(Path<? extends QInitiativeAuthor> path) {
        super(path.getType(), path.getMetadata(), "initiative", "initiative_author");
        addMetadata();
    }

    public QInitiativeAuthor(PathMetadata<?> metadata) {
        super(QInitiativeAuthor.class,  metadata, "initiative", "initiative_author");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(address, ColumnMetadata.named("address").ofType(12).withSize(1024));
        addMetadata(confirmationrequestsent, ColumnMetadata.named("confirmationrequestsent").ofType(93).withSize(29).withDigits(6));
        addMetadata(confirmed, ColumnMetadata.named("confirmed").ofType(93).withSize(29).withDigits(6));
        addMetadata(created, ColumnMetadata.named("created").ofType(93).withSize(29).withDigits(6).notNull());
        addMetadata(email, ColumnMetadata.named("email").ofType(12).withSize(256));
        addMetadata(firstnames, ColumnMetadata.named("firstnames").ofType(12).withSize(256).notNull());
        addMetadata(homemunicipalityFi, ColumnMetadata.named("homemunicipality_fi").ofType(12).withSize(30).notNull());
        addMetadata(homemunicipalitySv, ColumnMetadata.named("homemunicipality_sv").ofType(12).withSize(30).notNull());
        addMetadata(initiativeId, ColumnMetadata.named("initiative_id").ofType(-5).withSize(19).notNull());
        addMetadata(initiator, ColumnMetadata.named("initiator").ofType(-7).withSize(1).notNull());
        addMetadata(lastname, ColumnMetadata.named("lastname").ofType(12).withSize(256).notNull());
        addMetadata(phone, ColumnMetadata.named("phone").ofType(12).withSize(128));
        addMetadata(role, ColumnMetadata.named("role").ofType(1111).withSize(2147483647).notNull());
        addMetadata(userId, ColumnMetadata.named("user_id").ofType(-5).withSize(19).notNull());
    }

}

