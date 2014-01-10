package fi.om.initiative.sql;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;


/**
 * QInitiativeInvitation is a Querydsl query type for QInitiativeInvitation
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QInitiativeInvitation extends com.mysema.query.sql.RelationalPathBase<QInitiativeInvitation> {

    private static final long serialVersionUID = -894763239;

    public static final QInitiativeInvitation initiativeInvitation = new QInitiativeInvitation("initiative_invitation");

    public final DateTimePath<org.joda.time.DateTime> created = createDateTime("created", org.joda.time.DateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> initiativeId = createNumber("initiativeId", Long.class);

    public final StringPath invitationcode = createString("invitationcode");

    public final EnumPath<fi.om.initiative.dto.author.AuthorRole> role = createEnum("role", fi.om.initiative.dto.author.AuthorRole.class);

    public final DateTimePath<org.joda.time.DateTime> sent = createDateTime("sent", org.joda.time.DateTime.class);

    public final com.mysema.query.sql.PrimaryKey<QInitiativeInvitation> invitationPk = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QInitiative> invitationInitiativeIdFk = createForeignKey(initiativeId, "id");

    public QInitiativeInvitation(String variable) {
        super(QInitiativeInvitation.class,  forVariable(variable), "initiative", "initiative_invitation");
        addMetadata();
    }

    public QInitiativeInvitation(Path<? extends QInitiativeInvitation> path) {
        super(path.getType(), path.getMetadata(), "initiative", "initiative_invitation");
        addMetadata();
    }

    public QInitiativeInvitation(PathMetadata<?> metadata) {
        super(QInitiativeInvitation.class,  metadata, "initiative", "initiative_invitation");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(created, ColumnMetadata.named("created").ofType(93).withSize(29).withDigits(6).notNull());
        addMetadata(email, ColumnMetadata.named("email").ofType(12).withSize(256).notNull());
        addMetadata(id, ColumnMetadata.named("id").ofType(-5).withSize(19).notNull());
        addMetadata(initiativeId, ColumnMetadata.named("initiative_id").ofType(-5).withSize(19).notNull());
        addMetadata(invitationcode, ColumnMetadata.named("invitationcode").ofType(12).withSize(64));
        addMetadata(role, ColumnMetadata.named("role").ofType(1111).withSize(2147483647).notNull());
        addMetadata(sent, ColumnMetadata.named("sent").ofType(93).withSize(29).withDigits(6));
    }

}

