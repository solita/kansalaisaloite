package fi.om.initiative.dto.initiative;

import fi.om.initiative.json.JsonId;
import fi.om.initiative.web.Urls;

public class LocalizedInitiativeUrl {

    private Long id;

    public LocalizedInitiativeUrl(Long id) {
        this.id = id;
    }

    @JsonId(path =  Urls.VIEW_FI)
    public Long getFi() {
        return id;
    }

    @JsonId(path =  Urls.VIEW_SV)
    public Long getSv() {
        return id;
    }


}
