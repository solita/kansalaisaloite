package fi.om.initiative.dto.initiative;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import fi.om.initiative.dto.Deletable;
import fi.om.initiative.dto.InitURI;
import fi.om.initiative.json.InitURISerializer;
import fi.om.initiative.util.NotDeleted;
import fi.om.initiative.validation.group.Extra;

import javax.validation.constraints.NotNull;

public class Link implements Deletable {

    public static final Predicate<Link> NOT_DELETED = NotDeleted.create();
    
    private Long id; 

//    @URL(groups=Extra.class)
    @NotNull(groups=Extra.class)
    private InitURI uri;

    // NOTE This field is not @NotNull because setting it to null allows us to remove previously
    // inserted values in no-script mode
    @NotNull(groups=Extra.class)
    private String label;

    public Link() {
        this(null);
    }
    
    public Link(Long id) {
        this.id = id;
    }

    @JsonSerialize(using = InitURISerializer.class)
    public InitURI getUri() {
        return uri;
    }

    public void setUri(InitURI uri) {
        this.uri = uri;
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String labels) {
        this.label = labels;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @JsonIgnore
    public boolean isDeleted() {
        return Strings.isNullOrEmpty(label) && uri == null;
    }
    
}
