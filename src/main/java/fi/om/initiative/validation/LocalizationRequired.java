package fi.om.initiative.validation;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target( { METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = LocalizationRequiredValidator.class)
@Documented
public @interface LocalizationRequired {
    
    String message() default "{NotNull}";

    int maxLength() default 0;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
