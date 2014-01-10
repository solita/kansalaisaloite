package fi.om.initiative.dto.search;

import fi.om.initiative.dto.initiative.InitiativeInfo;

import java.util.List;

public class InitiativeSublistWithTotalCount {

    public final List<InitiativeInfo> list;
    public final long total;

    public InitiativeSublistWithTotalCount(List<InitiativeInfo> list, long total) {
        this.list = list;
        this.total = total;
    }

}
