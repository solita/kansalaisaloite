package fi.om.initiative.dto;

import fi.om.initiative.validation.group.Basic;
import fi.om.initiative.validation.group.CurrentAuthor;
import fi.om.initiative.validation.group.Extra;
import fi.om.initiative.validation.group.Organizers;

public enum EditMode {
    FULL(Basic.class, Extra.class, Organizers.class, CurrentAuthor.class),
    BASIC(Basic.class),
    EXTRA(Extra.class),
    ORGANIZERS(Organizers.class),
    CURRENT_AUTHOR(CurrentAuthor.class),
    NONE();
    
    private final Class<?>[] validationGroups;
    
    private EditMode(Class<?>... validationGroups) {
        this.validationGroups = validationGroups;
    }
    
    public Class<?>[] getValidationGroups() {
        return validationGroups;
    }

}
