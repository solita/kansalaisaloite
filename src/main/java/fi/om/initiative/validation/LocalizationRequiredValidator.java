package fi.om.initiative.validation;

import fi.om.initiative.dto.LocalizedString;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.Set;

public class LocalizationRequiredValidator implements ConstraintValidator<LocalizationRequired, LocalizedString> {

    private static final ThreadLocal<Set<String>> requiredLocales = new ThreadLocal<Set<String>>();
    private int maxLength;

    @Override
    public void initialize(LocalizationRequired constraintAnnotation) {
        this.maxLength = constraintAnnotation.maxLength();
    }

    @Override
    public boolean isValid(LocalizedString localizedString, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        boolean valid = true;
        for (String requiredLocale : requiredLocales.get()) {
            if (localizedString == null || !localizedString.hasTranslation(requiredLocale)) {
                valid = false;
                context.buildConstraintViolationWithTemplate("LocalizationRequired")
                .addNode(requiredLocale)
                .addConstraintViolation();
            }
            else if (isMaxLengthDefined() && localizedString.getTranslation(requiredLocale).length() > maxLength) {
                valid = false;
                context.buildConstraintViolationWithTemplate("LocalizationSizeExceeded")
                .addNode(requiredLocale)
                .addConstraintViolation();
            }
        }
        return valid;
    }

    private boolean isMaxLengthDefined() {
        return maxLength > 0;
    }

    public static void setRequiredLocales(Set<String> locales) {
        requiredLocales.set(locales);
    }

    public static void clearRequiredLocales() {
        requiredLocales.set(null);
    }
    
}
