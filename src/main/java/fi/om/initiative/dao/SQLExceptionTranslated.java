package fi.om.initiative.dao;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target( { METHOD, TYPE })
@Retention(RUNTIME)
@Documented
public @interface SQLExceptionTranslated {

}
