package fi.om.initiative.dao;

import com.google.common.collect.Lists;
import com.mysema.commons.lang.Assert;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.Tuple;
import com.mysema.query.dml.StoreClause;
import com.mysema.query.sql.SQLSubQuery;
import com.mysema.query.sql.dml.SQLDeleteClause;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.sql.postgres.PostgresQuery;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.support.Expressions;
import com.mysema.query.types.*;
import com.mysema.query.types.expr.CaseBuilder;
import com.mysema.query.types.expr.DateTimeExpression;
import com.mysema.query.types.expr.SimpleExpression;
import com.mysema.query.types.expr.Wildcard;
import fi.om.initiative.dto.InitURI;
import fi.om.initiative.dto.Invitation;
import fi.om.initiative.dto.SchemaVersion;
import fi.om.initiative.dto.SendToParliamentData;
import fi.om.initiative.dto.author.Author;
import fi.om.initiative.dto.author.AuthorRole;
import fi.om.initiative.dto.author.ContactInfo;
import fi.om.initiative.dto.initiative.*;
import fi.om.initiative.dto.search.*;
import fi.om.initiative.json.SupportCount;
import fi.om.initiative.sql.*;
import fi.om.initiative.util.OptionalHashMap;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static fi.om.initiative.dao.InitiativeDaoSearchExpressions.*;
import static fi.om.initiative.dto.InitiativeSettings.MinSupportCountSettings;
import static fi.om.initiative.util.Locales.asLocalizedString;

//@SuppressWarnings("serial")
@SQLExceptionTranslated
public class InitiativeDaoImpl implements InitiativeDao {
    
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(InitiativeDaoImpl.class);
   
    private static final Expression<DateTime> CURRENT_TIME = DateTimeExpression.currentTimestamp(DateTime.class);

    private static final Expression<LocalDate> CURRENT_DATE = DateTimeExpression.currentDate(LocalDate.class);
    
    private static final Expression<DateTime> NULL_TIME = new NullExpression<DateTime>(DateTime.class);
    
    private static final QInitiative qInitiative = QInitiative.initiative;

    private static final QInitiativeLink qInitiativeLink = QInitiativeLink.initiativeLink;

    private static final QInitiativeAuthor qAuthor = QInitiativeAuthor.initiativeAuthor;

    private static final QInituser qUser = QInituser.inituser;
    
    private static final QInitiativeInvitation qInvitation = QInitiativeInvitation.initiativeInvitation;
    
    private static final QSchemaVersion qSchemaVersion = QSchemaVersion.schemaVersion;

    private static void populate(InitiativeInfo initiative, Tuple tuple) {
        initiative.assignModified(tuple.get(qInitiative.modified));
        initiative.assignState(tuple.get(qInitiative.state));
        initiative.assignStateDate(tuple.get(qInitiative.statedate));
        initiative.setAcceptanceIdentifier(tuple.get(qInitiative.acceptanceidentifier));
        initiative.assignSupportCount(tuple.get(qInitiative.supportcount));
        initiative.assignSentSupportCount(tuple.get(qInitiative.sentsupportcount));
        initiative.setExternalSupportCount(tuple.get(qInitiative.externalsupportcount));
        initiative.setVerifiedSupportCount(tuple.get(qInitiative.verifiedsupportcount));
        initiative.setVerified(tuple.get(qInitiative.verified));
        initiative.assignSupportStatementsRemoved(tuple.get(qInitiative.supportstatementsremoved));
        
        initiative.setName(asLocalizedString(tuple.get(qInitiative.nameFi), tuple.get(qInitiative.nameSv)));
        initiative.setStartDate(tuple.get(qInitiative.startdate));
        initiative.assignEndDate(tuple.get(qInitiative.enddate));
        initiative.setProposalType(tuple.get(qInitiative.proposaltype));
        initiative.setPrimaryLanguage(tuple.get(qInitiative.primarylanguage));
        initiative.setFinancialSupport(tuple.get(qInitiative.financialsupport));
        initiative.setFinancialSupportURL(convertURI(tuple.get(qInitiative.financialsupporturl), "Initiative", initiative.getId()));
        initiative.setSupportStatementsOnPaper(tuple.get(qInitiative.supportstatementsonpaper));
        initiative.setSupportStatementsInWeb(tuple.get(qInitiative.supportstatementsinweb));
        initiative.setSupportStatementPdf(Boolean.TRUE.equals(tuple.get(qInitiative.supportstatementpdf)));
        initiative.setSupportStatementAddress(tuple.get(qInitiative.supportstatementaddress));

        initiative.setParliamentIdentifier(tuple.get(qInitiative.parliamentidentifier));
        initiative.setParliamentURL(tuple.get(qInitiative.parliamenturl));
        initiative.setParliamentSentTime(tuple.get(qInitiative.parliamentsenttime));
    }
    
    private static void populate(InitiativeBase initiative, Tuple tuple) {
        populate((InitiativeInfo) initiative, tuple); 
        initiative.setProposal(asLocalizedString(tuple.get(qInitiative.proposalFi), tuple.get(qInitiative.proposalSv)));
        initiative.setRationale(asLocalizedString(tuple.get(qInitiative.rationaleFi), tuple.get(qInitiative.rationaleSv)));
    }

    private static final MappingProjection<InitiativeInfo> initiativeInfoMapping = 
            new MappingProjection<InitiativeInfo>(InitiativeInfo.class,
                    qInitiative.id,
                    qInitiative.modified,
                    qInitiative.state,
                    qInitiative.statedate,
                    qInitiative.acceptanceidentifier,
                    qInitiative.supportcount,
                    qInitiative.sentsupportcount,
                    qInitiative.externalsupportcount,
                    qInitiative.verifiedsupportcount,
                    qInitiative.verified,
                    qInitiative.supportstatementsremoved,
                    qInitiative.nameFi,
                    qInitiative.nameSv,
                    qInitiative.startdate,
                    qInitiative.enddate,
                    qInitiative.proposaltype,
                    qInitiative.primarylanguage,
                    qInitiative.financialsupport,
                    qInitiative.financialsupporturl,
                    qInitiative.supportstatementsonpaper,
                    qInitiative.supportstatementsinweb,
                    qInitiative.parliamentsenttime,
                    qInitiative.parliamenturl,
                    qInitiative.parliamentidentifier
            ) {

                private static final long serialVersionUID = -4881570381807049732L;

                @Override
                public InitiativeInfo map(Tuple tuple) {
                    if (tuple == null) {
                        return null;
                    }

                    InitiativeInfo initiative = new InitiativeInfo(tuple.get(qInitiative.id));
                    populate(initiative, tuple);
                    return initiative;
                }

            };

    private static final MappingProjection<InitiativePublic> initiativePublicMapping = 
            new MappingProjection<InitiativePublic>(InitiativePublic.class, qInitiative.all()) {
        
                private static final long serialVersionUID = 8944540576249724221L;

                @Override
                public InitiativePublic map(Tuple tuple) {
                    if (tuple == null) {
                        return null;
                    }
                    
                    InitiativePublic initiative = new InitiativePublic(tuple.get(qInitiative.id));
                    populate(initiative, tuple);
                    return initiative;
                }
                
            };
            
    private static final MappingProjection<InitiativeManagement> initiativeManagementMapping = 
            new MappingProjection<InitiativeManagement>(InitiativeManagement.class, qInitiative.all()) {

                private static final long serialVersionUID = 2838260766600285859L;

                @Override
                public InitiativeManagement map(Tuple tuple) {
                    if (tuple == null) {
                        return null;
                    }
                    
                    InitiativeManagement initiative = new InitiativeManagement(tuple.get(qInitiative.id));
                    populate(initiative, tuple);
                    initiative.assignModifierId(tuple.get(qInitiative.modifierId));
                    initiative.assignStateComment(tuple.get(qInitiative.statecomment));
                    
                    initiative.setVerificationIdentifier(tuple.get(qInitiative.verificationidentifier));

                    return initiative;
                }
                
            };
            
    private static final MappingProjection<SchemaVersion> schemaVersionMapping = 
            new MappingProjection<SchemaVersion>(SchemaVersion.class, qSchemaVersion.all()) {

                private static final long serialVersionUID = -1940230714453573464L;

                @Override
                protected SchemaVersion map(Tuple tuple) {
                    if (tuple == null) {
                        return null;
                    }
                return new SchemaVersion(
                              tuple.get(qSchemaVersion.script), 
                              tuple.get(qSchemaVersion.executed)
                              );
                }
                
            };

    static Expression<?>[] projection(Expression<?> first, Expression<?>... rest) {
        Expression<?>[] fields = new Expression<?>[rest.length + 1];
        System.arraycopy(rest, 0, fields, 1, rest.length);
        fields[0] = first;
        return fields;
    }
    
    static Expression<?>[] projection(Expression<?> first, Expression<?> second, Expression<?>... rest) {
        Expression<?>[] fields = new Expression<?>[rest.length + 2];
        System.arraycopy(rest, 0, fields, 2, rest.length);
        fields[0] = first;
        fields[1] = second;
        return fields;
    }
    
    private static Author mapAuthor(Tuple tuple) {
        if (tuple == null || tuple.get(qAuthor.userId) == null) {
            return null;
        }
        Author author = new Author(
                tuple.get(qAuthor.userId), 
                tuple.get(qAuthor.firstnames), 
                tuple.get(qAuthor.lastname),
                tuple.get(qUser.dateofbirth),
                asLocalizedString(tuple.get(qAuthor.homemunicipalityFi), tuple.get(qAuthor.homemunicipalitySv))
            );

        author.assignCreated(tuple.get(qAuthor.created));
        author.assignFirstNames(tuple.get(qAuthor.firstnames));
        author.setInitiator(tuple.get(qAuthor.initiator));
        author.assignLastName(tuple.get(qAuthor.lastname));
        author.assignConfirmed(tuple.get(qAuthor.confirmed));
        author.assignConfirmationRequestSent(tuple.get(qAuthor.confirmationrequestsent));

        AuthorRole role = tuple.get(qAuthor.role);
        if (AuthorRole.REPRESENTATIVE.equals(role)) {
            author.setRepresentative(true);
        } else if (AuthorRole.RESERVE.equals(role)) {
            author.setReserve(true);
        }
        
        author.setContactInfo(new ContactInfo(
                tuple.get(qAuthor.email), 
                tuple.get(qAuthor.phone), 
                tuple.get(qAuthor.address)));

        return author;
    }

    private static final MappingProjection<Author> authorPublicMapping = 
            new MappingProjection<Author>(Author.class, qAuthor.all()) {

                private static final long serialVersionUID = 8195056321917573389L;

                @Override
                protected Author map(Tuple tuple) {
                    return mapAuthor(tuple);
                }
            };

    private static final MappingProjection<Author> authorManagementMapping = 
            new MappingProjection<Author>(Author.class, projection(qUser.dateofbirth, qAuthor.all())) {

                private static final long serialVersionUID = -5290160604454532213L;

                @Override
                protected Author map(Tuple tuple) {
                    return mapAuthor(tuple);
                }
            };

    public static final MappingProjection<Link> linkMapping = 
            new MappingProjection<Link>(Link.class, qInitiativeLink.all()) {

                private static final long serialVersionUID = 4307652170307391794L;

                @Override
                public Link map(Tuple tuple) {
                    if (tuple == null) {
                        return null;
                    }
                    Link link = new Link();
                    link.setId(tuple.get(qInitiativeLink.id));
                    link.setLabel(tuple.get(qInitiativeLink.label));
                    link.setUri(convertURI(tuple.get(qInitiativeLink.uri), "Link", link.getId()));
                    return link;
                }
            };
            
    private static InitURI convertURI(String uri, String entity, Long id) {
        if (uri == null) {
            return null;
        } else {
            try {
                return new InitURI(uri);
            } catch (IllegalArgumentException e) {
                log.error("Error in " + entity + "(" + id + ") URI", e);
                return null;
            }
        }
    }

    private static final MappingProjection<Invitation> invitationMapping = 
            new MappingProjection<Invitation>(Invitation.class, qInvitation.all()) {

                private static final long serialVersionUID = 2242724524883021896L;

                @Override
                protected Invitation map(Tuple tuple) {
                    if (tuple == null) {
                        return null;
                    }
                    Invitation invitation = new Invitation(tuple.get(qInvitation.id));
                    invitation.assignCreated(tuple.get(qInvitation.created));
                    invitation.assignSent(tuple.get(qInvitation.sent));
                    invitation.assignInvitationCode(tuple.get(qInvitation.invitationcode));
                    invitation.setEmail(tuple.get(qInvitation.email));
                    invitation.assignRole(tuple.get(qInvitation.role));
                    return invitation;
                }
                
            };
            
    @Resource PostgresQueryFactory queryFactory;

    public InitiativeDaoImpl() {}
    
    public InitiativeDaoImpl(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @Transactional(readOnly=true)
    public InitiativePublic getInitiativeForPublic(Long id) {

        PostgresQuery qry = queryFactory
                .from(qInitiative)
                .leftJoin(qInitiative._authorInitiativeIdFk, qAuthor)
                .where(qInitiative.id.eq(id));


        InitiativePublic initiative = mapInitiativeWithAuthors(qry, initiativePublicMapping, authorPublicMapping);
        if (initiative != null) {
            fetchLinks(initiative);
        }
        return initiative;
    }

    @Override
    @Transactional(readOnly=true)
    public long getInitiativeCount() {
        PostgresQuery qry = queryFactory
                .from(qInitiative);
        return qry.count();
    }
    
    private <T extends InitiativeBase> T mapInitiativeWithAuthors(PostgresQuery qry, MappingProjection<T> initiativeMapping, MappingProjection<Author> authorMapping) {
        List<Tuple> rows = qry.list(new QTuple(initiativeMapping, authorMapping));
        T initiative = null;
        List<Author> authors = Lists.newArrayList();
        for (Tuple tuple : rows) {
            if (initiative == null) {
                initiative = tuple.get(initiativeMapping);
            }
            Author author = tuple.get(authorMapping);
            if (author != null) {
                authors.add(author);
            }
        }
        if (initiative != null) {
            initiative.assignAuthors(authors);
        }
        return initiative;
    }
    
    @Override
    @Transactional(readOnly=true)
    public InitiativeManagement getInitiativeForManagement(Long id, boolean forUpdate) {
        PostgresQuery qry = queryFactory
                .from(qInitiative)
                .leftJoin(qInitiative._authorInitiativeIdFk, qAuthor)
                .leftJoin(qAuthor.authorUserIdFk, qUser)
                .where(qInitiative.id.eq(id));

        if (forUpdate) {
            qry.forUpdate().of(qInitiative);
        }
        
        InitiativeManagement initiative = mapInitiativeWithAuthors(qry, initiativeManagementMapping, authorManagementMapping);
        if (initiative != null) {
            fetchLinks(initiative);
            fetchInvitations(initiative);
        }
        return initiative;
    }

    private void fetchLinks(InitiativeBase initiative) {
        PostgresQuery qry = queryFactory
                .from(qInitiativeLink)
                .where(qInitiativeLink.initiativeId.eq(initiative.getId()))
                .orderBy(qInitiativeLink.id.asc());
        
        List<Link> links = qry.list(linkMapping);
        initiative.setLinks(links);
    }

    private void fetchInvitations(InitiativeManagement initiative) {
        PostgresQuery qry = queryFactory
                .from(qInvitation)
                .where(qInvitation.initiativeId.eq(initiative.getId()))
                .orderBy(qInvitation.id.asc());

        List<Invitation> invitations = qry.list(invitationMapping);
        initiative.assignInvitations(invitations);
    }
    
    @Override
    @Transactional(readOnly=false)
    public Long create(InitiativeManagement initiative, Long userId) {
        SQLInsertClause insert = queryFactory.insert(qInitiative);
        populateInitiative(insert, initiative, userId, true, true);

        return insert.executeWithKey(qInitiative.id);
    }
    
    private Long insertLink(Link link, Long initiativeId) {
        SQLInsertClause insert = queryFactory.insert(qInitiativeLink);
        insert.set(qInitiativeLink.initiativeId, initiativeId);
        populateLink(insert, link);

        return insert.executeWithKey(qInitiativeLink.id);
    }
    
    private Long insertInvitation(Long initiativeId, Invitation invitation) {
        SQLInsertClause insert = queryFactory.insert(qInvitation);

        insert.set(qInvitation.initiativeId, initiativeId);
        insert.set(qInvitation.email, invitation.getEmail());
        insert.set(qInvitation.role, invitation.getRole());
        
        return insert.executeWithKey(qInvitation.id);
    }
    
    private void populateLink(StoreClause<?> store, Link link) {
        // NOTE: Cannot not change initiative_id foreign key!
        store
        .set(qInitiativeLink.uri, link.getUri().toString())
        .set(qInitiativeLink.label, link.getLabel())
        ;
    }
    
    private void populateInitiative(StoreClause<?> store, InitiativeBase initiative, Long userId, boolean basic, boolean extra) {
        store
        .set(qInitiative.modifierId, userId)
        .set(qInitiative.modified, CURRENT_TIME)
        ;
        
        if (basic) {
            // Basic fields
            store
            .set(qInitiative.nameFi, initiative.getName().getFi())
            .set(qInitiative.nameSv, initiative.getName().getSv())
            .set(qInitiative.startdate, initiative.getStartDate())
            .set(qInitiative.enddate, initiative.getEndDate())
            .set(qInitiative.proposaltype, initiative.getProposalType())
            .set(qInitiative.proposalFi, initiative.getProposal().getFi())
            .set(qInitiative.proposalSv, initiative.getProposal().getSv())
            .set(qInitiative.rationaleFi, initiative.getRationale().getFi())
            .set(qInitiative.rationaleSv, initiative.getRationale().getSv())
            .set(qInitiative.primarylanguage, initiative.getPrimaryLanguage())
            ;
        } 
        if (extra) {
            // Extra fields
            store
            .set(qInitiative.financialsupport, initiative.isFinancialSupport())
            .set(qInitiative.supportstatementsonpaper, initiative.isSupportStatementsOnPaper())
            .set(qInitiative.supportstatementsinweb, initiative.isSupportStatementsInWeb())
            .set(qInitiative.externalsupportcount, initiative.getExternalSupportCount())
            .set(qInitiative.supportstatementpdf, initiative.isSupportStatementPdf())
            .set(qInitiative.supportstatementaddress, initiative.getSupportStatementAddress())
            ;

            InitURI uri = initiative.getFinancialSupportURL();
            if (uri != null) {
                store.set(qInitiative.financialsupporturl, uri.toString());
            } else {
                store.set(qInitiative.financialsupporturl, (String) null);
            }
        }
    }

    @Override
    @Transactional(readOnly=false)
    public void updateInitiativeState(Long initiativeId, Long userId, InitiativeState state, String comment) {
        SQLUpdateClause update = getUpdateClauseForInitiativeState(initiativeId, userId, state, comment);

        if (update.execute() != 1) {
            throw new NotFoundException(qInitiative.getTableName(), initiativeId);
        } 
    }

    @Override
    @Transactional(readOnly=false)
    public void updateInitiativeStateAndAcceptanceIdentifier(Long initiativeId, Long userId, InitiativeState state, String comment, String acceptanceIdentifier) {
        SQLUpdateClause update = getUpdateClauseForInitiativeState(initiativeId, userId, state, comment)
                .set(qInitiative.acceptanceidentifier, acceptanceIdentifier);

        if (update.execute() != 1) {
            throw new NotFoundException(qInitiative.getTableName(), initiativeId);
        } 
    }
    
    private SQLUpdateClause getUpdateClauseForInitiativeState(Long initiativeId, Long userId, InitiativeState state, String comment) {
        Assert.notNull(initiativeId, "initiative.id");
        return queryFactory.update(qInitiative)
                .set(qInitiative.modifierId, userId)
                .set(qInitiative.modified, CURRENT_TIME)
                .set(qInitiative.state, state)
                .set(qInitiative.statecomment, comment)
                .set(qInitiative.statedate, CURRENT_TIME) 
                .where(qInitiative.id.eq(initiativeId));
    }

    @Override
    @Transactional(readOnly = true)
    public InitiativeManagement get(Long id) {
        return queryFactory.from(qInitiative)
                .where(qInitiative.id.eq(id)).uniqueResult(initiativeManagementMapping);
    }

    @Override
    @Transactional(readOnly = true)
    public OptionalHashMap<String, Long> getOmCounts(MinSupportCountSettings minSupportCountSettings) {

        Expression<String> caseBuilder = new CaseBuilder()
                .when(InitiativeDaoSearchExpressions.INITIATIVE_IS_WAITING).then(new ConstantImpl<>(Show.waiting.name()))
                .when(InitiativeDaoSearchExpressions.INITIATIVE_IS_RUNNING(minSupportCountSettings)).then(new ConstantImpl<>(Show.running.name()))
                .when(InitiativeDaoSearchExpressions.INITIATIVE_IS_PREPARATION).then(new ConstantImpl<>(Show.preparation.name()))
                .when(InitiativeDaoSearchExpressions.INITIATIVE_IS_REVIEW).then(new ConstantImpl<>(Show.review.name()))
                .when(InitiativeDaoSearchExpressions.INITIATIVE_IS_OM_CANCELED).then(new ConstantImpl<>(Show.canceled.name()))
                .when(InitiativeDaoSearchExpressions.INITIATIVE_IS_SENT_TO_PARLIAMENT).then(new ConstantImpl<>(Show.sentToParliament.name()))
                .otherwise(Show.ended.name());
        SimpleExpression<String> simpleExpression = Expressions.as(caseBuilder, "showCategory");
        Map<String, Long> map = queryFactory
                .from(qInitiative)
                .groupBy(simpleExpression)
                .map(simpleExpression, qInitiative.count());

        return new OptionalHashMap<>(map);

    }

    @Override
    @Transactional(readOnly = true)
    public OptionalHashMap<String, Long> getPublicCounts(MinSupportCountSettings minSupportCountSettings) {

        Expression<String> caseBuilder = new CaseBuilder()
                .when(InitiativeDaoSearchExpressions.INITIATIVE_IS_WAITING).then(new ConstantImpl<>(Show.waiting.name()))
                .when(InitiativeDaoSearchExpressions.INITIATIVE_IS_RUNNING(minSupportCountSettings)).then(new ConstantImpl<>(Show.running.name()))
                .when(InitiativeDaoSearchExpressions.INITIATIVE_IS_CANCELED).then(new ConstantImpl<>(Show.canceled.name()))
                .when(InitiativeDaoSearchExpressions.INITIATIVE_IS_SENT_TO_PARLIAMENT).then(new ConstantImpl<>(Show.sentToParliament.name()))
                .otherwise(Show.ended.name());
        SimpleExpression<String> simpleExpression = Expressions.as(caseBuilder, "showCategory");
        Map<String, Long> map = queryFactory
                .from(qInitiative)
                .where(InitiativeDaoSearchExpressions.INITIATIVE_PUBLIC_ALL())
                .groupBy(simpleExpression)
                .map(simpleExpression, qInitiative.count());

        return new OptionalHashMap<>(map);

    }

    @Override
    @Transactional(readOnly = true)
    public SupportCount getSupportCount(Long id) {
        SupportCount supportCount = queryFactory.from(qInitiative)
                .where(qInitiative.id.eq(id))
                .singleResult(supportCountMapping);

        if (supportCount == null) {
            throw new NotFoundException(qInitiative.getTableName(), id);
        }
        return supportCount;
    }

    private static final MappingProjection<SupportCount> supportCountMapping =
        new MappingProjection<SupportCount>(SupportCount.class,
                qInitiative.supportcount,
                qInitiative.externalsupportcount) {
            @Override
            protected SupportCount map(Tuple row) {
                return new SupportCount(row.get(qInitiative.supportcount), row.get(qInitiative.externalsupportcount));
            }
        };


    @Override
    @Transactional(readOnly=false)
    public void updateInitiative(InitiativeManagement initiative, Long userId, boolean basic, boolean extra) {
        Assert.notNull(initiative, "initiative");
        Assert.notNull(userId, "userId");

        Long initiativeId = initiative.getId();
        Assert.notNull(initiativeId, "initiative.id");

        SQLUpdateClause update = queryFactory.update(qInitiative);
        populateInitiative(update, initiative, userId, basic, extra);
        update.where(qInitiative.id.eq(initiative.getId()));
        
        if (update.execute() != 1) {
            throw new NotFoundException(qInitiative.getTableName(), initiative.getId());
        } 
    }

    @Override
    @Transactional(readOnly=false)
    public void updateLinks(Long initiativeId, List<Link> links) {
        List<Long> linkIds = Lists.newArrayList();
        for (Link link : links) {
            if (!link.isDeleted()) {
                Long linkId = link.getId();
                if (linkId != null) {
                    updateLink(link, initiativeId);
                } else {
                    linkId = insertLink(link, initiativeId);
                }
                linkIds.add(linkId);
            }
        }
        // Delete other (i.e. removed) links
        SQLDeleteClause delete = queryFactory
                .delete(qInitiativeLink)
                .where(qInitiativeLink.initiativeId.eq(initiativeId));
        
        if (!linkIds.isEmpty()) {
            delete.where(qInitiativeLink.id.notIn(linkIds));
        }
        
        delete.execute();
    }

    @Override
    @Transactional(readOnly=false)
    public void updateInvitations(Long initiativeId, List<Invitation> initiatorInvitations, List<Invitation> representativeInvitations, List<Invitation> reserveInvitations) {
        updateInvitations(initiativeId, initiatorInvitations, AuthorRole.INITIATOR);
        updateInvitations(initiativeId, representativeInvitations, AuthorRole.REPRESENTATIVE);
        updateInvitations(initiativeId, reserveInvitations, AuthorRole.RESERVE);
    }
    
    private void updateInvitations(Long initiativeId, List<Invitation> unsentInvitations, AuthorRole role) {
        List<Long> invitationIds = Lists.newArrayList();
        for (Invitation invitation : unsentInvitations) {
            if (!invitation.isDeleted()) {
                invitation.assignRole(role);
                Long invitationId = invitation.getId();
                if (invitationId != null) {
                    updateUnsentInvitation(initiativeId, invitation);
                } else {
                    invitationId = insertInvitation(initiativeId, invitation);
                }
                invitationIds.add(invitationId);
            }
        }
        // Delete other (i.e. removed) unsent invitations
        SQLDeleteClause delete = queryFactory
                .delete(qInvitation)
                .where(qInvitation.initiativeId.eq(initiativeId), 
                       qInvitation.role.eq(role),
                       qInvitation.sent.isNull());
        
        if (!invitationIds.isEmpty()) {
            delete.where(qInvitation.id.notIn(invitationIds));
        }
        
        delete.execute();
    }
    
    private void updateLink(Link link, Long initiativeId) {
        Long linkId = link.getId();
        Assert.notNull(linkId, "link.id");

        SQLUpdateClause update = queryFactory
                .update(qInitiativeLink)
                .where(
                        qInitiativeLink.id.eq(linkId),
                        qInitiativeLink.initiativeId.eq(initiativeId) // Defensive... 
                    );
        populateLink(update, link);
        
        if (update.execute() != 1) {
            throw new NotFoundException(qInitiativeLink.getTableName(), linkId);
        }
    }
    
    private void updateUnsentInvitation(Long initiativeId, Invitation invitation) {
        if (invitation.getSent() == null) {
            Long invitationId = invitation.getId();
            Assert.notNull(invitationId, "invitation.id");
    
            SQLUpdateClause update = queryFactory
                    .update(qInvitation)
                    .where(
                            qInvitation.id.eq(invitationId),
                            qInvitation.initiativeId.eq(initiativeId) // Defensive... 
                        );
            update.set(qInvitation.email, invitation.getEmail());
            
            if (update.execute() != 1) {
                throw new NotFoundException(qInitiativeLink.getTableName(), invitationId);
            }
        }
    }

    @Override
    @Transactional(readOnly=true)
    public InitiativeSublistWithTotalCount findInitiatives(InitiativeSearch search, Long userId, MinSupportCountSettings minSupportCountSettings) {
        PostgresQuery qry = queryFactory.from(qInitiative);
        BooleanBuilder where = new BooleanBuilder();

        if (search.getSearchView() == SearchView.pub || search.getSearchView() == SearchView.om) {
            generateSearchWhere(minSupportCountSettings, where, search.getShow());
        }
        else if (search.getSearchView() == SearchView.own && userId != null) {
            SQLSubQuery authorOf = queryFactory.subQuery(qAuthor);
            authorOf.where(
                    qAuthor.userId.eq(userId),
                    qAuthor.initiativeId.eq(qInitiative.id)
                );
            where.or(authorOf.exists());
        }
        else {
            throw new IllegalStateException("No filtering for view: " + search.getSearchView());
        }

        if (search.getMinSupportCount() != null) {
            qry.where(qInitiative.externalsupportcount.add(qInitiative.supportcount).goe(search.getMinSupportCount()));
        }

        qry.where(where);

        long totalCount = qry.count();

        if (search.getOffset() != null) {
            qry.offset(search.getOffset());
        }
        if (search.getLimit() != null) {
            qry.limit(search.getLimit());
        }

        qry.orderBy(createOrderBy(search.getOrderBy()), qInitiative.id.desc());

        return new InitiativeSublistWithTotalCount(qry.list(initiativeInfoMapping), totalCount);
    }

    @Override
    @Transactional(readOnly = false)
    public void updateSendToParliament(Long initiativeId, SendToParliamentData data) {
        SQLUpdateClause update = queryFactory.update(qInitiative)
                .set(qInitiative.parliamentidentifier, data.getParliamentIdentifier())
                .set(qInitiative.parliamentsenttime, data.getParliamentSentTime())
                .set(qInitiative.parliamenturl, data.getParliamentURL())
                .set(qInitiative.state, InitiativeState.DONE)
                .set(qInitiative.statedate, CURRENT_TIME)
                .where(qInitiative.id.eq(initiativeId));

        if (update.execute() != 1) {
            throw new NotFoundException(qInitiative.getTableName(), initiativeId);
        }
    }

    @Override
    public void endInitiative(Long initiativeId, LocalDate lastRunningDate) {
        SQLUpdateClause update = queryFactory.update(qInitiative)
                .set(qInitiative.enddate, lastRunningDate)
                .where(qInitiative.id.eq(initiativeId));

        if (update.execute() != 1) {
            throw new NotFoundException(qInitiative.getTableName(), initiativeId);
        }
    }

    @Override
    public List<InitiativeInfo> listInitiativesWithEndDate(LocalDate endDate) {

        PostgresQuery qry = queryFactory
                .from(qInitiative)
                .leftJoin(qInitiative._authorInitiativeIdFk, qAuthor)
                .where(qInitiative.enddate.eq( endDate));

        return qry.list(initiativeInfoMapping);
    }

    private static void generateSearchWhere(MinSupportCountSettings minSupportCountSettings, BooleanBuilder where, Show show) {
        switch (show) {
            case preparation:
                where.and(INITIATIVE_IS_PREPARATION);
                break;
            case review:
                where.and(INITIATIVE_IS_REVIEW);
                break;
            case omCanceled:
                where.and(INITIATIVE_IS_OM_CANCELED);
                break;
            case omAll:
                break;

            // End of om view states

            case running:
                where.and(INITIATIVE_IS_RUNNING(minSupportCountSettings));
                break;
            case ended:
                where.and(INITIATIVE_IS_ENDED(minSupportCountSettings));
                break;
            case canceled:
                where.and(INITIATIVE_IS_CANCELED);
                break;
            case sentToParliament:
                where.and(INITIATIVE_IS_SENT_TO_PARLIAMENT);
                break;
            case waiting:
                where.and(INITIATIVE_IS_WAITING);
            case all:
                where.and(INITIATIVE_PUBLIC_ALL());
                break;
            default:
                throw new RuntimeException("No dao implementation for initiatives with: " + show);
        }
    }

    private static OrderSpecifier<?> createOrderBy(OrderBy orderBy) {
        switch (orderBy) {
            case mostTimeLeft:
                return qInitiative.enddate.desc();
            case leastTimeLeft:
                return qInitiative.enddate.asc();
            case id:
                return qInitiative.id.desc();
            case leastSupports:
                return qInitiative.supportcount.add(qInitiative.externalsupportcount).asc();
            case mostSupports:
                return qInitiative.supportcount.add(qInitiative.externalsupportcount).desc();
            case createdNewest:
                return qInitiative.startdate.desc();
            case createdOldest:
                return qInitiative.startdate.asc();
            default:
                throw new IllegalStateException("Invalid orderBy value: " + orderBy);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public OptionalHashMap<InitiativeState, Long> getInitiativeCountByState() {

        Map<InitiativeState, Long> map = queryFactory
                .from(qInitiative)
                .where(INITIATIVE_PUBLIC_ALL())
                .groupBy(qInitiative.state)
                .map(qInitiative.state, Wildcard.count);
        return new OptionalHashMap<InitiativeState, Long>(map);

    }

    @Override
    @Transactional(readOnly=true)
    public List<InitiativeInfo> findInitiativesWithUnremovedVotes() {
        PostgresQuery qry = queryFactory.from(qInitiative)
                .where(qInitiative.supportcount.gt(0),
                       qInitiative.supportstatementsremoved.isNull())
                .orderBy(qInitiative.id.desc());
      
        return Lists.newArrayList(qry.map(qInitiative.id, initiativeInfoMapping).values());
    }

    @Override
    @Transactional(readOnly=false)
    public void insertAuthor(Long initiativeId, Long userId, Author author) {
        SQLInsertClause insert = queryFactory.insert(qAuthor)
                .set(qAuthor.userId, userId)
                .set(qAuthor.initiativeId, initiativeId);

        populateAuthor(insert, author);

        insert.set(qAuthor.confirmed, CURRENT_TIME);

        insert.execute();
    }

    @Override
    @Transactional(readOnly=false)
    public void clearConfirmations(Long initiativeId, Long userId) {
        Expression<DateTime> confirmed = new CaseBuilder()
            .when(qAuthor.userId.eq(userId))
            .then(CURRENT_TIME)
            .otherwise(NULL_TIME);

        SQLUpdateClause update = queryFactory.update(qAuthor)
            .set(qAuthor.confirmed, confirmed)
            .set(qAuthor.confirmationrequestsent, NULL_TIME)
            .where(qAuthor.initiativeId.eq(initiativeId));

        update.execute();
    }

    @Override
    @Transactional(readOnly=false)
    public void updateConfirmationRequestSent(Long initiativeId, Long userId) {
        SQLUpdateClause update = queryFactory.update(qAuthor)
                .set(qAuthor.confirmationrequestsent, CURRENT_TIME)
                .where(qAuthor.initiativeId.eq(initiativeId), qAuthor.userId.eq(userId));

        update.execute();
    }

    @Override
    @Transactional(readOnly=false)
    public void confirmAuthor(Long initiativeId, Long userId) {
        SQLUpdateClause update = queryFactory.update(qAuthor)
                .set(qAuthor.confirmed, CURRENT_TIME)
                .where(qAuthor.initiativeId.eq(initiativeId), qAuthor.userId.eq(userId));

        if (update.execute() != 1) {
            throw new NotFoundException("author", initiativeId + ":" + userId);
        }
    }

    @Override
    @Transactional(readOnly=false)
    public void deleteAuthor(Long initiativeId, Long userId) {
        SQLDeleteClause delete = queryFactory.delete(qAuthor)
                .where(qAuthor.initiativeId.eq(initiativeId), qAuthor.userId.eq(userId));

        if (delete.execute() != 1) {
            throw new NotFoundException("author", initiativeId + ":" + userId);
        }
    }

    private void populateAuthor(StoreClause<?> store, Author author) {
        store
        .set(qAuthor.firstnames, author.getFirstNames())
        .set(qAuthor.homemunicipalityFi, author.getHomeMunicipality().getFi())
        .set(qAuthor.homemunicipalitySv, author.getHomeMunicipality().getSv())
        .set(qAuthor.initiator, author.isInitiator())
        .set(qAuthor.lastname, author.getLastName())
        .set(qAuthor.role, getAuthorRole(author));

        ContactInfo contactInfo = author.getContactInfo();
        if (contactInfo != null) {
            store
            .set(qAuthor.address, contactInfo.getAddress())
            .set(qAuthor.email, contactInfo.getEmail())
            .set(qAuthor.phone, contactInfo.getPhone());
        }
    }

    private AuthorRole getAuthorRole(Author author) {
        if (author.isRepresentative()) {
            return AuthorRole.REPRESENTATIVE;
        } else if (author.isReserve()) {
            return AuthorRole.RESERVE;
        } else if (author.isInitiator()) {
            return AuthorRole.INITIATOR;
        } else {
            throw new IllegalArgumentException("Author is not representative, reserver nor initiator");
        }
    }

    @Override
    @Transactional(readOnly=true)
    public Author getAuthor(Long initiativeId, Long userId) {
        PostgresQuery qry = queryFactory
                .from(qAuthor)
                .innerJoin(qAuthor.authorUserIdFk, qUser)
                .where(qAuthor.initiativeId.eq(initiativeId), qAuthor.userId.eq(userId));
        return qry.uniqueResult(authorManagementMapping);
    }

    @Override
    @Transactional(readOnly=true)
    public List<SchemaVersion> getSchemaVersions() {
        PostgresQuery qry = queryFactory
                .from(qSchemaVersion)
                .orderBy(qSchemaVersion.executed.asc());
        return qry.list(schemaVersionMapping);
    }

    @Override
    @Transactional(readOnly=false)
    public void updateAuthor(Long initiativeId, Long userId, Author author) {
        SQLUpdateClause update = queryFactory
                .update(qAuthor)
                .where(qAuthor.initiativeId.eq(initiativeId), qAuthor.userId.eq(userId));
        
        populateAuthor(update, author);
        
        if (update.execute() != 1) {
            throw new NotFoundException("author", initiativeId + ":" + userId);
        }
    }
    
    @Override
    @Transactional(readOnly=false)
    public void updateInvitationSent(Long initiativeId, Long invitationId, String invitationCode) {
        Assert.notNull(initiativeId, "initiativeId");
        Assert.notNull(invitationId, "invitationId");
        Assert.notNull(invitationCode, "invitationCode");

        SQLUpdateClause update = queryFactory
                .update(qInvitation)
                .where(
                        qInvitation.id.eq(invitationId),
                        qInvitation.initiativeId.eq(initiativeId),  // Defensive... 
                        qInvitation.sent.isNull()
                    );
        update.set(qInvitation.sent, CURRENT_TIME);
        update.set(qInvitation.invitationcode, invitationCode);
        
        if (update.execute() != 1) {
            throw new NotFoundException(qInvitation.getTableName(), invitationId);
        }
    }
    
    @Override
    @Transactional(readOnly=true)
    public Invitation getOpenInvitation(Long initiativeId, String invitationCode, Integer invitationExpirationDays) {

        Predicate dateCondition; 
        if (invitationExpirationDays == null) {
            dateCondition = qInvitation.sent.isNotNull();
        }
        else {
            DateTime afterDate = new DateTime().minusDays(invitationExpirationDays);
            dateCondition = qInvitation.sent.after(afterDate);
        }
        
        PostgresQuery qry = queryFactory
                .from(qInvitation)
                .where(qInvitation.initiativeId.eq(initiativeId), 
                       qInvitation.invitationcode.eq(invitationCode),
                       dateCondition);
        return qry.uniqueResult(invitationMapping);
    }

    @Override
    @Transactional(readOnly=false)
    public void removeInvitation(Long initiativeId, String invitationCode) {
        Assert.notNull(initiativeId, "initiativeId");
        Assert.notNull(invitationCode, "invitationCode");

        SQLDeleteClause delete = queryFactory
                .delete(qInvitation)
                .where(qInvitation.initiativeId.eq(initiativeId), 
                       qInvitation.invitationcode.eq(invitationCode),
                       qInvitation.sent.isNotNull());
        
        if (delete.execute() != 1) {
            throw new NotFoundException(qInvitation.getTableName(), initiativeId + ":" + invitationCode);
        }
    }

    @Override
    @Transactional(readOnly=false)
    public void removeInvitations(Long initiativeId) {
        Assert.notNull(initiativeId, "initiativeId");
        
        //remove all remaining invitations from initiative: unsent, unanswered and expired  
        SQLDeleteClause delete = queryFactory
                .delete(qInvitation)
                .where(qInvitation.initiativeId.eq(initiativeId));
        
        delete.execute(); // no need to check removed count, since it can be anything between 0 and n
    }

    @Override
    @Transactional(readOnly=false)
    public void removeUnconfirmedAuthors(Long initiativeId) {
        Assert.notNull(initiativeId, "initiativeId");
        
        SQLDeleteClause delete = queryFactory
                .delete(qAuthor)
                .where(qAuthor.initiativeId.eq(initiativeId),
                       qAuthor.confirmed.isNull());
        
        delete.execute(); // no need to check removed count, since it can be anything between 0 and n
    }

    @Override
    @Transactional(readOnly=true)
    public List<String> getAuthorEmailsWhichAreNotNull(Long initiativeId) {
        PostgresQuery qry = queryFactory
                .from(qAuthor)
                .where(qAuthor.initiativeId.eq(initiativeId))
                .where(qAuthor.email.isNotNull());

        List<String> emails = qry.distinct().list(qAuthor.email);

        return emails;
    }

    @Override
    @Transactional(readOnly=false)
    public void updateVRKResolution(Long initiativeId, int verifiedSupportCount, LocalDate verified, String verificationIdentifier, Long userId) {
        queryFactory
        .update(qInitiative)
        .set(qInitiative.modifierId, userId)
        .set(qInitiative.modified, CURRENT_TIME)
        .set(qInitiative.verifiedsupportcount, verifiedSupportCount)
        .set(qInitiative.verified, verified)
        .set(qInitiative.verificationidentifier, verificationIdentifier)
        .where(qInitiative.id.eq(initiativeId))
        .execute();
    }

}
