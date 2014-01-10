package fi.om.initiative.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.springframework.beans.NotReadablePropertyException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class LocalValidatorFactoryBeanFix extends LocalValidatorFactoryBean {
    
    @Override
    protected void processConstraintViolations(Set<ConstraintViolation<Object>> violations, Errors errors) {
        for (ConstraintViolation<Object> violation : violations) {
            String field = violation.getPropertyPath().toString();
            FieldError fieldError = errors.getFieldError(field);
            if (fieldError == null || !fieldError.isBindingFailure()) {
                try {
                    String errorCode = violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
                    Object[] errorArgs = getArgumentsForConstraint(errors.getObjectName(), field, violation.getConstraintDescriptor());
                    // NOTE: Super implementation of getInvalidValue() failed on a nested component. 
                    // E.g. LocalizedString.fi returned LocalizedString instead of String as invalidValue
                    errors.rejectValue(field, errorCode, errorArgs, violation.getMessage());
                }
                catch (NotReadablePropertyException ex) {
                    throw new IllegalStateException("JSR-303 validated property '" + field +
                            "' does not have a corresponding accessor for Spring data binding - " +
                            "check your DataBinder's configuration (bean property versus direct field access)", ex);
                }
            }
        }
    }

}
