package fi.om.initiative.util;

import fi.om.initiative.dto.initiative.InitiativeManagement;
import fi.om.initiative.dto.initiative.Link;

import java.util.List;

public class SnapshotCreator {
    public static String create(InitiativeManagement initiative) {
        return emptyStringOrValue(initiative.getName().getFi())
                + "\n\n"
                + emptyStringOrValue(initiative.getProposal().getFi())
                + "\n\n"
                + emptyStringOrValue(initiative.getRationale().getFi())
                + "\n\n"
                + emptyStringOrValue(initiative.getName().getSv())
                + "\n\n"
                + emptyStringOrValue(initiative.getProposal().getSv())
                + "\n\n"
                + emptyStringOrValue(initiative.getRationale().getSv())
                + "\n\n"
                + emptyStringOrValues(initiative.getLinks());
    }

    private static String emptyStringOrValue(String value) {
        if (value == null) {
            return "";
        }
        else {
            return value;
        }
    }
    private static String emptyStringOrValues(List<Link> list) {
        if (list.isEmpty()) {
            return "";
        }
        else {
            return list.toString();
        }
    }
}
