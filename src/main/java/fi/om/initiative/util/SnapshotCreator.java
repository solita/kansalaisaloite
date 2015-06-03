package fi.om.initiative.util;

import fi.om.initiative.dto.initiative.InitiativeManagement;

public class SnapshotCreator {
    public static String create(InitiativeManagement initiative) {
        return initiative.getName().getFi()
                + "\n\n"
                + initiative.getProposal().getFi()
                + "\n\n"
                + initiative.getRationale().getFi()
                + "\n\n"
                + initiative.getName().getSv()
                + "\n\n"
                + initiative.getProposal().getSv()
                + "\n\n"
                + initiative.getRationale().getSv()
                + "\n\n"
                + initiative.getLinks().toString();
    }
}
