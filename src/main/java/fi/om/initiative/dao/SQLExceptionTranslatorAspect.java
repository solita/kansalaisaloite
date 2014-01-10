package fi.om.initiative.dao;

import java.sql.SQLException;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.jdbc.support.SQLExceptionTranslator;

@Aspect
public class SQLExceptionTranslatorAspect {
    
    private SQLExceptionTranslator translator;
    
    public SQLExceptionTranslatorAspect(SQLExceptionTranslator translator) {
        this.translator = translator;
    }

    @AfterThrowing(pointcut="@within(fi.om.initiative.dao.SQLExceptionTranslated) or @annotation(fi.om.initiative.dao.SQLExceptionTranslated)", throwing="ex", argNames="ex")
    public void translateException(JoinPoint jp, RuntimeException ex) {
        Throwable t = ex.getCause();
        if (t instanceof SQLException) {
            throw translator.translate(jp.getSignature().toShortString(), null, (SQLException) t);
        } else {
            throw ex;
        }
    }
}
