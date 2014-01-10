package fi.om.initiative.validation;

import fi.om.initiative.dto.author.Author;
import fi.om.initiative.dto.author.ContactInfo;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.google.common.base.Strings.isNullOrEmpty;

public class ContactInfoValidator implements ConstraintValidator<ValidContactInfo, Author> {

    @Override
    public void initialize(ValidContactInfo constraintAnnotation) {
    }

    @Override
    public boolean isValid(Author author, ConstraintValidatorContext context) {
        if (author.isRepresentative() || author.isReserve()) {
            ContactInfo contactInfo = author.getContactInfo();
            if (isNullOrEmpty(contactInfo.getEmail()) 
                    && isNullOrEmpty(contactInfo.getPhone()) 
                    && isNullOrEmpty(contactInfo.getAddress())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("ValidContactInfo")
                .addNode("contactInfo")
                .addConstraintViolation();
                return false;
            }
        }

        return true;
    }

}
