package fi.om.initiative.service;


import fi.om.initiative.dao.InitiativeDao;
import fi.om.initiative.dto.initiative.InitiativeInfo;
import org.joda.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

public class FollowService {

    @Resource
    InitiativeDao initiativeDao;

    @Transactional
    public List<InitiativeInfo> getInitiativesThatEndedYesterday() {

        LocalDate today =  LocalDate.now();
        return initiativeDao.listInitiativesWithEndDate(today.minusDays(1));
    }
}
