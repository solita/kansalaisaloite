package fi.om.initiative.web;

import fi.om.initiative.dto.User;
import fi.om.initiative.dto.initiative.InitiativePublic;
import fi.om.initiative.service.InitiativeService;
import fi.om.initiative.service.Role;
import fi.om.initiative.service.SupportVoteService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static fi.om.initiative.web.Urls.*;
import static fi.om.initiative.web.Views.VOTE_VIEW;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class SupportVoteController extends BaseController {

    private final Logger log = LoggerFactory.getLogger(SupportVoteController.class); 
    
    @Resource SupportVoteService supportVoteService;
    
    @Resource InitiativeService initiativeService;
    
    public SupportVoteController(boolean optimizeResources, String resourcesVersion) {
        super(optimizeResources, resourcesVersion);
    }

    @RequestMapping(value={ VIEW_FI, VIEW_SV }, method=GET, params=ACTION_VOTE)
    public String voteGet(@PathVariable("id") Long initiativeId, Model model, Locale locale, HttpServletRequest request) {
        User user = userService.getUserInRole(Role.AUTHENTICATED);

        Urls urls = Urls.get(locale);
        model.addAttribute(ALT_URI_ATTR, urls.alt().vote(initiativeId));
        
        
        DateTime now = new DateTime();
        
        DateTime votingTime = supportVoteService.getVotingTime(initiativeId);
        if (!user.isAllowedToVote(now)) {
            return redirectWithMessage(urls.view(initiativeId), RequestMessage.VOTING_NOT_ALLOWED, request);
        }
        else if (votingTime != null) {
            return redirectWithMessage(urls.view(initiativeId), RequestMessage.ALREADY_VOTED, request);
        } else {
            return voteView(initiativeId, model);
        }
    }

    @RequestMapping(value={ VIEW_FI, VIEW_SV }, method=POST, params=ACTION_VOTE)
    public String votePost(@PathVariable("id") Long initiativeId, @RequestParam(value="confirm", defaultValue="false") boolean confirm, Model model, Locale locale, HttpServletRequest request) {
        Urls urls = Urls.get(locale);
        if (confirm) {
            try {
                supportVoteService.vote(initiativeId, locale);
            } catch (DuplicateKeyException e) {
                log.debug("PK prevented duplicate vote", e);
                return redirectWithMessage(urls.view(initiativeId), RequestMessage.ALREADY_VOTED, request);
            }
            return redirectWithMessage(urls.view(initiativeId), RequestMessage.CONFIRM_VOTE, request);
        } else {
            return redirectWithMessage(urls.vote(initiativeId), RequestMessage.CONFIRM_VOTE_UNCHECKED, request);
        }
    }
    
    private String voteView(Long initiativeId, Model model) {
        InitiativePublic initiative = initiativeService.getInitiativeForPublic(initiativeId);
        
        model.addAttribute("initiative", initiative);
        
        return VOTE_VIEW;
    }
    
    @RequestMapping(value={ VIEW_FI, VIEW_SV }, method=POST, params=ACTION_SEND_TO_VRK)
    public String sendToVRKPost(@PathVariable("id") Long initiativeId, Model model, Locale locale, HttpServletRequest request) {
        Urls urls = Urls.get(locale);

        supportVoteService.sendToVRK(initiativeId);

        return redirectWithMessage(urls.view(initiativeId), RequestMessage.SEND_TO_VRK, request);
    }

    @RequestMapping(value={ VIEW_FI, VIEW_SV }, method=POST, params=ACTION_REMOVE_SUPPORT_VOTES)
    public String removeSupportVotesPost(@PathVariable("id") Long initiativeId, @RequestParam(value="confirm", defaultValue="false") boolean confirm, Model model, Locale locale, HttpServletRequest request) {
        Urls urls = Urls.get(locale);

        if (confirm) {
            supportVoteService.removeSupportVotes(initiativeId);
            return redirectWithMessage(urls.view(initiativeId), RequestMessage.REMOVE_SUPPORT_VOTES, request);
        } else {
            return redirectWithMessage(urls.view(initiativeId)+"?remove-support-votes=confirm", RequestMessage.CONFIRM_REMOVAL_UNCHECKED, request);
        }
    }
    
    @RequestMapping(value={ DOWNLOAD_VOTES }, method=GET)
    public void downloadVotes(@PathVariable("id") Long batchId, HttpServletResponse response) throws IOException {
        List<String> votes = supportVoteService.getVoteDetails(batchId);
        response.setContentType("text/plain;charset=ISO-8859-1");
        
        ServletOutputStream out = response.getOutputStream();
        try {
            for (String vote : votes) {
                out.println(vote);
            }
        } finally {
            out.close();
        }
    }

}
