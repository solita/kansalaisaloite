package fi.om.initiative.validation;

import fi.om.initiative.dto.User;
import fi.om.initiative.dto.author.AuthorRole;
import org.joda.time.DateTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AuthorRoleValidator implements ConstraintValidator<ValidAuthorRole, Boolean> {

    private static final ThreadLocal<User> currentUser = new ThreadLocal<User>();

    private AuthorRole role;
    
    @Override
    public void initialize(ValidAuthorRole constraintAnnotation) {
        this.role = constraintAnnotation.role();
    }

    @Override
    public boolean isValid(Boolean userInRole, ConstraintValidatorContext context) {
        if (userInRole != null && userInRole.booleanValue()) {
            User user = currentUser.get();
            DateTime now = new DateTime();
            return user.isAllowRole(role, now);
        } else {
            return true;
        }
    }
    

    public static void setCurrentUser(User user) {
        currentUser.set(user);
    }

    public static void clearCurrentUser() {
        currentUser.set(null);
    }

}
