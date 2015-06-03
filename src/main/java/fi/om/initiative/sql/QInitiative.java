package fi.om.initiative.sql;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import java.util.*;

import com.mysema.query.sql.ColumnMetadata;


/**
 * QInitiative is a Querydsl query type for QInitiative
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QInitiative extends com.mysema.query.sql.RelationalPathBase<QInitiative> {

    private static final long serialVersionUID = 1362518880;

    public static final QInitiative initiative = new QInitiative("initiative");

    public final StringPath acceptanceidentifier = createString("acceptanceidentifier");

    public final DatePath<org.joda.time.LocalDate> enddate = createDate("enddate", org.joda.time.LocalDate.class);

    public final NumberPath<Integer> externalsupportcount = createNumber("externalsupportcount", Integer.class);

    public final BooleanPath financialsupport = createBoolean("financialsupport");

    public final StringPath financialsupporturl = createString("financialsupporturl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<org.joda.time.DateTime> modified = createDateTime("modified", org.joda.time.DateTime.class);

    public final NumberPath<Long> modifierId = createNumber("modifierId", Long.class);

    public final StringPath nameFi = createString("nameFi");

    public final StringPath nameSv = createString("nameSv");

    public final StringPath parliamentidentifier = createString("parliamentidentifier");

    public final DatePath<org.joda.time.LocalDate> parliamentsenttime = createDate("parliamentsenttime", org.joda.time.LocalDate.class);

    public final StringPath parliamenturl = createString("parliamenturl");

    public final EnumPath<fi.om.initiative.dto.LanguageCode> primarylanguage = createEnum("primarylanguage", fi.om.initiative.dto.LanguageCode.class);

    public final StringPath proposalFi = createString("proposalFi");

    public final StringPath proposalSv = createString("proposalSv");

    public final EnumPath<fi.om.initiative.dto.ProposalType> proposaltype = createEnum("proposaltype", fi.om.initiative.dto.ProposalType.class);

    public final StringPath rationaleFi = createString("rationaleFi");

    public final StringPath rationaleSv = createString("rationaleSv");

    public final NumberPath<Integer> sentsupportcount = createNumber("sentsupportcount", Integer.class);

    public final DatePath<org.joda.time.LocalDate> startdate = createDate("startdate", org.joda.time.LocalDate.class);

    public final EnumPath<fi.om.initiative.dto.initiative.InitiativeState> state = createEnum("state", fi.om.initiative.dto.initiative.InitiativeState.class);

    public final StringPath statecomment = createString("statecomment");

    public final DateTimePath<org.joda.time.DateTime> statedate = createDateTime("statedate", org.joda.time.DateTime.class);

    public final NumberPath<Integer> supportcount = createNumber("supportcount", Integer.class);

    public final StringPath supportCountData = createString("supportCountData");

    public final StringPath supportstatementaddress = createString("supportstatementaddress");

    public final BooleanPath supportstatementpdf = createBoolean("supportstatementpdf");

    public final BooleanPath supportstatementsinweb = createBoolean("supportstatementsinweb");

    public final BooleanPath supportstatementsonpaper = createBoolean("supportstatementsonpaper");

    public final DateTimePath<org.joda.time.DateTime> supportstatementsremoved = createDateTime("supportstatementsremoved", org.joda.time.DateTime.class);

    public final StringPath verificationidentifier = createString("verificationidentifier");

    public final DatePath<org.joda.time.LocalDate> verified = createDate("verified", org.joda.time.LocalDate.class);

    public final NumberPath<Integer> verifiedsupportcount = createNumber("verifiedsupportcount", Integer.class);

    public final com.mysema.query.sql.PrimaryKey<QInitiative> initiativePk = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QInituser> initiativeModifierIdFk = createForeignKey(modifierId, "id");

    public final com.mysema.query.sql.ForeignKey<QInitiativeAuthor> _authorInitiativeIdFk = createInvForeignKey(id, "initiative_id");

    public final com.mysema.query.sql.ForeignKey<QInitiativeSupportVoteDay> _supportVoteDayInitiativeIdFk = createInvForeignKey(id, "initiative_id");

    public final com.mysema.query.sql.ForeignKey<QInitiativeInvitation> _invitationInitiativeIdFk = createInvForeignKey(id, "initiative_id");

    public final com.mysema.query.sql.ForeignKey<QReviewHistory> _reviewHistoryInitiativeId = createInvForeignKey(Arrays.asList(id, id), Arrays.asList("initiative_id", "initiative_id"));

    public final com.mysema.query.sql.ForeignKey<QSupportVoteBatch> _supportVoteBatchInitiativeIdFk = createInvForeignKey(id, "initiative_id");

    public final com.mysema.query.sql.ForeignKey<QSupportVote> _supportVoteInitiativeIdFk = createInvForeignKey(id, "initiative_id");

    public final com.mysema.query.sql.ForeignKey<QInitiativeLink> _linkInitiativeIdFk = createInvForeignKey(id, "initiative_id");

    public QInitiative(String variable) {
        super(QInitiative.class,  forVariable(variable), "initiative", "initiative");
        addMetadata();
    }

    public QInitiative(Path<? extends QInitiative> path) {
        super(path.getType(), path.getMetadata(), "initiative", "initiative");
        addMetadata();
    }

    public QInitiative(PathMetadata<?> metadata) {
        super(QInitiative.class,  metadata, "initiative", "initiative");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(acceptanceidentifier, ColumnMetadata.named("acceptanceidentifier").ofType(12).withSize(128));
        addMetadata(enddate, ColumnMetadata.named("enddate").ofType(91).withSize(13));
        addMetadata(externalsupportcount, ColumnMetadata.named("externalsupportcount").ofType(4).withSize(10).notNull());
        addMetadata(financialsupport, ColumnMetadata.named("financialsupport").ofType(-7).withSize(1).notNull());
        addMetadata(financialsupporturl, ColumnMetadata.named("financialsupporturl").ofType(12).withSize(4096));
        addMetadata(id, ColumnMetadata.named("id").ofType(-5).withSize(19).notNull());
        addMetadata(modified, ColumnMetadata.named("modified").ofType(93).withSize(29).withDigits(6).notNull());
        addMetadata(modifierId, ColumnMetadata.named("modifier_id").ofType(-5).withSize(19).notNull());
        addMetadata(nameFi, ColumnMetadata.named("name_fi").ofType(12).withSize(512));
        addMetadata(nameSv, ColumnMetadata.named("name_sv").ofType(12).withSize(512));
        addMetadata(parliamentidentifier, ColumnMetadata.named("parliamentidentifier").ofType(12).withSize(50));
        addMetadata(parliamentsenttime, ColumnMetadata.named("parliamentsenttime").ofType(91).withSize(13));
        addMetadata(parliamenturl, ColumnMetadata.named("parliamenturl").ofType(12).withSize(512));
        addMetadata(primarylanguage, ColumnMetadata.named("primarylanguage").ofType(1111).withSize(2147483647).notNull());
        addMetadata(proposalFi, ColumnMetadata.named("proposal_fi").ofType(12).withSize(2147483647));
        addMetadata(proposalSv, ColumnMetadata.named("proposal_sv").ofType(12).withSize(2147483647));
        addMetadata(proposaltype, ColumnMetadata.named("proposaltype").ofType(1111).withSize(2147483647).notNull());
        addMetadata(rationaleFi, ColumnMetadata.named("rationale_fi").ofType(12).withSize(2147483647));
        addMetadata(rationaleSv, ColumnMetadata.named("rationale_sv").ofType(12).withSize(2147483647));
        addMetadata(sentsupportcount, ColumnMetadata.named("sentsupportcount").ofType(4).withSize(10));
        addMetadata(startdate, ColumnMetadata.named("startdate").ofType(91).withSize(13));
        addMetadata(state, ColumnMetadata.named("state").ofType(1111).withSize(2147483647).notNull());
        addMetadata(statecomment, ColumnMetadata.named("statecomment").ofType(12).withSize(4096));
        addMetadata(statedate, ColumnMetadata.named("statedate").ofType(93).withSize(29).withDigits(6).notNull());
        addMetadata(supportcount, ColumnMetadata.named("supportcount").ofType(4).withSize(10).notNull());
        addMetadata(supportCountData, ColumnMetadata.named("support_count_data").ofType(12).withSize(2147483647));
        addMetadata(supportstatementaddress, ColumnMetadata.named("supportstatementaddress").ofType(12).withSize(1024));
        addMetadata(supportstatementpdf, ColumnMetadata.named("supportstatementpdf").ofType(-7).withSize(1));
        addMetadata(supportstatementsinweb, ColumnMetadata.named("supportstatementsinweb").ofType(-7).withSize(1).notNull());
        addMetadata(supportstatementsonpaper, ColumnMetadata.named("supportstatementsonpaper").ofType(-7).withSize(1).notNull());
        addMetadata(supportstatementsremoved, ColumnMetadata.named("supportstatementsremoved").ofType(93).withSize(29).withDigits(6));
        addMetadata(verificationidentifier, ColumnMetadata.named("verificationidentifier").ofType(12).withSize(128));
        addMetadata(verified, ColumnMetadata.named("verified").ofType(91).withSize(13));
        addMetadata(verifiedsupportcount, ColumnMetadata.named("verifiedsupportcount").ofType(4).withSize(10));
    }

}

