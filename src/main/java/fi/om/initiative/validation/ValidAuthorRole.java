package fi.om.initiative.validation;

import fi.om.initiative.dto.author.AuthorRole;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target( { METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = AuthorRoleValidator.class)
@Documented
public @interface ValidAuthorRole {

    String message() default "{ValidAuthorRole}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    AuthorRole role();
    
}
