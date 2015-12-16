package fi.om.initiative.service;


import fi.om.initiative.dao.InitiativeDao;
import fi.om.initiative.dto.initiative.InitiativeInfo;

import javax.annotation.Resource;
import java.util.List;

public class FollowService {

    @Resource
    InitiativeDao initiativeDao;

    public List<InitiativeInfo> getInitiativesThatEndedYesterday() {

        return null;
    }
}
