package fi.om.initiative.util;

import com.google.common.base.Optional;
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
                + emptyStringOrValue(initiative.getFinancialSupportURL().toString())
                + "\n\n"
                + parseLinkList(initiative.getLinks());

    }

    private static String emptyStringOrValue(String value) {
        return Optional.fromNullable(value).or("");
    }

    private static String parseLinkList(List<Link> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Link link : list) {
            stringBuilder
                    .append(link.getLabel())
                    .append(": ")
                    .append(link.getUri())
                    .append("\n");
        }

        return stringBuilder.toString();
    }
}
