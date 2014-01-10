package fi.om.initiative.dao;

import com.mysema.commons.lang.Assert;
import com.mysema.query.Tuple;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.Expression;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.expr.DateTimeExpression;
import fi.om.initiative.dto.InfoPageText;
import fi.om.initiative.dto.InfoTextFooterLink;
import fi.om.initiative.dto.InfoTextSubject;
import fi.om.initiative.dto.LanguageCode;
import fi.om.initiative.sql.QInfoText;
import org.joda.time.DateTime;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;


@Transactional(readOnly = true)
public class InfoTextDaoImpl implements InfoTextDao {

    private static final Expression<DateTime> CURRENT_TIME = DateTimeExpression.currentTimestamp(DateTime.class);

    @Resource
    PostgresQueryFactory queryFactory;

    @Override
    @Transactional(readOnly = false)
    public void publishFromDraft(String uri, String modifier) {
        assertSingleAffection(
                queryFactory.update(QInfoText.infoText)
                        .where(QInfoText.infoText.uri.eq(uri))
                        .set(QInfoText.infoText.published,QInfoText.infoText.draft)
                        .set(QInfoText.infoText.publishedSubject, QInfoText.infoText.draftSubject)
                        .set(QInfoText.infoText.modified, CURRENT_TIME)
                        .set(QInfoText.infoText.modifier, modifier)
                        .execute()
        );
    }

    @Override
    @Transactional(readOnly = false)
    public void saveDraft(InfoPageText infoPageText) {
        assertSingleAffection(
                queryFactory.update(QInfoText.infoText)
                        .where(QInfoText.infoText.uri.eq(infoPageText.getUri()))
                        .set(QInfoText.infoText.draft, infoPageText.getContent())
                        .set(QInfoText.infoText.draftSubject, infoPageText.getSubject())
                        .set(QInfoText.infoText.modified, infoPageText.getModifyTime())
                        .set(QInfoText.infoText.modifier, infoPageText.getModifierName())
                        .execute()
        );
    }

    @Override
    @Transactional(readOnly = false)
    public void draftFromPublished(String uri, String modifierName) {
        assertSingleAffection(
                queryFactory.update(QInfoText.infoText)
                        .where(QInfoText.infoText.uri.eq(uri))
                        .set(QInfoText.infoText.draft, QInfoText.infoText.published)
                        .set(QInfoText.infoText.draftSubject, QInfoText.infoText.publishedSubject)
                        .set(QInfoText.infoText.modified, CURRENT_TIME)
                        .set(QInfoText.infoText.modifier, modifierName)
                        .execute()
        );
    }

    @Override
    public List<InfoTextSubject> getNotEmptySubjects(LanguageCode languageCode) {
        return queryFactory.from(QInfoText.infoText)
                .where(QInfoText.infoText.languagecode.eq(languageCode))
                .where(QInfoText.infoText.published.isNotEmpty())
                .where(QInfoText.infoText.publishedSubject.isNotEmpty())
                .orderBy(QInfoText.infoText.orderposition.asc())
                .list(infotextSubjectMapping);
    }

    @Override
    public List<InfoTextSubject> getAllSubjects(LanguageCode languageCode) {
        return queryFactory.from(QInfoText.infoText)
                .where(QInfoText.infoText.languagecode.eq(languageCode))
                .orderBy(QInfoText.infoText.orderposition.asc())
                .list(infotextSubjectMapping);
    }

    @Override
    public InfoPageText getPublished(String uri) {
        return notFoundIfNull(queryFactory.from(QInfoText.infoText)
                .where(QInfoText.infoText.uri.eq(uri))
                .uniqueResult(publishedInfoPageTextMapping),
                uri);
    }

    @Override
    public InfoPageText getDraft(String uri) {
        return notFoundIfNull(queryFactory.from(QInfoText.infoText)
                .where(QInfoText.infoText.uri.eq(uri))
                .uniqueResult(draftInfoPageTextMapping),
                uri);
    }

    @Override
    @Cacheable("footerLinks")
    public List<InfoTextFooterLink> getFooterLinks(LanguageCode language) {
        return queryFactory.from(QInfoText.infoText)
                .where(QInfoText.infoText.footerDisplay.eq(true))
                .where(QInfoText.infoText.languagecode.eq(language))
                .where(QInfoText.infoText.publishedSubject.isNotEmpty())
                .orderBy(QInfoText.infoText.orderposition.asc())
                .list(infoTextFooterLink);


    }

    private static <T> T notFoundIfNull(T object, String key) {
        if (object == null) {
            throw new NotFoundException("Info text", key);
        }
        return object;
    }

    private static final MappingProjection<InfoTextSubject> infotextSubjectMapping
            = new MappingProjection<InfoTextSubject>(InfoTextSubject.class, QInfoText.infoText.all()) {
        @Override
        protected InfoTextSubject map(Tuple row) {
            return new InfoTextSubject(
                    row.get(QInfoText.infoText.category),
                    row.get(QInfoText.infoText.uri),
                    row.get(QInfoText.infoText.publishedSubject)
            );
        }
    };

    private static MappingProjection<InfoTextFooterLink> infoTextFooterLink
            = new MappingProjection<InfoTextFooterLink>(InfoTextFooterLink.class, QInfoText.infoText.all()) {
        @Override
        protected InfoTextFooterLink map(Tuple row) {
            return new InfoTextFooterLink(row.get(QInfoText.infoText.uri), row.get(QInfoText.infoText.publishedSubject));
        }
    };

    private static final MappingProjection<InfoPageText> publishedInfoPageTextMapping
            = new MappingProjection<InfoPageText>(InfoPageText.class, QInfoText.infoText.all()) {
        @Override
        protected InfoPageText map(Tuple row) {
            return InfoPageText.builder(row.get(QInfoText.infoText.uri))
                    .withText(row.get(QInfoText.infoText.publishedSubject), row.get(QInfoText.infoText.published))
                    .withModifier(row.get(QInfoText.infoText.modifier), row.get(QInfoText.infoText.modified))
                    .build();
        }
    };

    private static final MappingProjection<InfoPageText> draftInfoPageTextMapping
            = new MappingProjection<InfoPageText>(InfoPageText.class, QInfoText.infoText.all()) {
        @Override
        protected InfoPageText map(Tuple row) {
            return InfoPageText.builder(row.get(QInfoText.infoText.uri))
                    .withText(row.get(QInfoText.infoText.draftSubject), row.get(QInfoText.infoText.draft))
                    .withModifier(row.get(QInfoText.infoText.modifier), row.get(QInfoText.infoText.modified))
                    .build();
        }
    };

    private static void assertSingleAffection(long affectedRows) {
        Assert.isTrue(affectedRows == 1, "Affected " + affectedRows + " instead of 1.");
    }
}
