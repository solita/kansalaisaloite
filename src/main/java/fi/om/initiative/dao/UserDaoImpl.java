package fi.om.initiative.dao;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.mysema.commons.lang.Assert;
import com.mysema.query.Tuple;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.sql.postgres.PostgresQuery;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.QTuple;

import fi.om.initiative.dto.User;
import fi.om.initiative.sql.QInituser;

@SQLExceptionTranslated
public class UserDaoImpl implements UserDao {

    private static final QInituser qUser = QInituser.inituser;
    
    private static final Function<Tuple, User> tupleToUser = new Function<Tuple, User>() {

        @Override
        public User apply(Tuple input) {
            if (input == null) {
                return null;
            } else {
                return new User(
                        input.get(qUser.id), 
                        input.get(qUser.lastlogin), 
                        input.get(qUser.firstnames), 
                        input.get(qUser.lastname),
                        input.get(qUser.dateofbirth),
                        input.get(qUser.vrk), 
                        input.get(qUser.om));
            }
        }
        
    };
    
    private PostgresQueryFactory queryFactory;
    
    public UserDaoImpl(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /* (non-Javadoc)
     * @see fi.om.initiative.dao.UserDao#register(java.lang.String, org.joda.time.DateTime, boolean, boolean)
     */
    @Override
    @Transactional(readOnly=false)
    public Long register(String ssnHash, DateTime lastLogin, String firstNames, String lastName, LocalDate dateOfBirth) {
        SQLInsertClause insert = queryFactory
                .insert(qUser)
                .set(qUser.hash, ssnHash)
                .set(qUser.lastlogin, lastLogin)
                .set(qUser.firstnames, firstNames)
                .set(qUser.lastname, lastName)
                .set(qUser.dateofbirth, dateOfBirth)
                ;

        return insert.executeWithKey(qUser.id);
    }
    
    /* (non-Javadoc)
     * @see fi.om.initiative.dao.UserDao#loginRegisteredUser(java.lang.String)
     */
    @Override
    @Transactional(readOnly=false)
    public User loginRegisteredUser(String ssnHash) {
        PostgresQuery qry = queryFactory.from(qUser)
                .where(qUser.hash.eq(ssnHash));

        User user = tupleToUser.apply(qry.uniqueResult(new QTuple(qUser.all())));
        if (user != null) {
            SQLUpdateClause update = queryFactory.update(qUser);
            update.set(qUser.lastlogin, new DateTime());
            update.where(qUser.id.eq(user.getId()));
            update.execute();
        }
        return user;
    }

    @Override
    @Transactional(readOnly=false)
    public void setUserRoles(Long userId, boolean vrk, boolean om) {
        Assert.notNull(userId, "userId");
        SQLUpdateClause update = queryFactory.update(qUser);
        update.set(qUser.vrk, vrk);
        update.set(qUser.om, om);
        update.where(qUser.id.eq(userId));
        update.execute();
    }
    
}
