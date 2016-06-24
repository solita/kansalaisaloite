package fi.om.initiative.web;

import fi.om.initiative.dto.initiative.InitiativeInfo;
import fi.om.initiative.service.InitiativeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

import static fi.om.initiative.web.WebConstants.JSON;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class KapaController {

    @Resource
    private InitiativeService initiativeService;

    @RequestMapping(value="/services/kapa/v1/initiatives/{ssn}", method=GET, produces=JSON)
    public @ResponseBody
    List<InitiativeInfo> getInitiatives(@PathVariable("ssn") String ssn,
                                        @RequestHeader(value="secure", required = true) String secure) {

        // TODO: Get real initiatives
        // TODO: Assert secure
        return initiativeService.getFrontPageInitiatives();

    }
}
