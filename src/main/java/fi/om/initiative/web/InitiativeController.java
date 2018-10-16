package fi.om.initiative.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.lowagie.text.DocumentException;
import fi.om.initiative.dto.*;
import fi.om.initiative.dto.author.Author;
import fi.om.initiative.dto.initiative.*;
import fi.om.initiative.dto.search.*;
import fi.om.initiative.json.SupportCount;
import fi.om.initiative.pdf.SupportStatementPdfGenerator;
import fi.om.initiative.service.*;
import fi.om.initiative.util.Locales;
import fi.om.initiative.util.Maybe;
import fi.om.initiative.util.ReviewHistoryDiff;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static fi.om.initiative.dto.EditMode.FULL;
import static fi.om.initiative.dto.EditMode.NONE;
import static fi.om.initiative.web.Urls.*;
import static fi.om.initiative.web.Views.*;
import static fi.om.initiative.web.WebConstants.JSON;
import static fi.om.initiative.web.WebConstants.JSONP;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class InitiativeController extends BaseController {

    private final Logger log = LoggerFactory.getLogger(InitiativeController.class);

    private static final String ATTR_INVITATION_CODE = "invitationCode";

    private static final String ATTR_VOTING_INFO = "votingInfo";

    private ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    private InitiativeService initiativeService;

    @Resource
    private SupportVoteService supportVoteService;

    @Resource
    private InitiativeSettings initiativeSettings;

    @Resource
    private MessageSource messageSource;

    @Resource
    private SupportStatementPdfGenerator supportStatementPdfGenerator;

    @Resource
    private FollowService followService;

    public InitiativeController() {
        super(true);
    }

    /*
     * Create
     */
    @RequestMapping(value={ CREATE_FI, CREATE_SV }, method=GET)
    public String createGet(Model model, Locale locale, HttpServletRequest request) {
        Urls urls = Urls.get(locale);
        model.addAttribute(ALT_URI_ATTR, urls.alt().createNew());

        User user;
        try {
            user = userService.getUserInRole(Role.AUTHENTICATED);
        } catch (AuthenticationRequiredException e) {
            return BEFORE_CREATE_VIEW;
        }

        if (!user.isAdult()) {
            addRequestMessage(RequestMessage.ADULT_REQUIRED_AS_AUTHOR, model, request);
        }

        Author author = toAuthor(user);
        InitiativeManagement initiative = new InitiativeManagement(author, Locales.LOCALE_SV.equals(locale) ? LanguageCode.SV : LanguageCode.FI);

        return managementView(model, initiative, null, FULL, request, null);
    }

    @RequestMapping(value={ CREATE_FI, CREATE_SV }, method=POST) // , params=ACTION_SAVE - default action
    public String createPost(@ModelAttribute("initiative") InitiativeManagement initiative, BindingResult bindingResult, Model model, Locale locale, HttpServletRequest request) {
        Urls urls = Urls.get(locale);
        if (create(initiative, bindingResult, model, locale, request)) {
            return redirectWithMessage(urls.view(initiative.getId()), RequestMessage.SAVE, request);
        } else {
            return managementView(model, initiative, bindingResult, FULL, request, null);
        }
    }

    @RequestMapping(value={ CREATE_FI, CREATE_SV }, method=POST, params=ACTION_SAVE_AND_SEND_INVITATIONS)
    public String createAndSendInvitationsPost(@ModelAttribute("initiative") InitiativeManagement initiative, BindingResult bindingResult, Model model, Locale locale, HttpServletRequest request) {
        Urls urls = Urls.get(locale);
        if (create(initiative, bindingResult, model, locale, request)) {
            return sendInvitations(initiative.getId(), urls, FULL, model, RequestMessage.SAVE_AND_SEND_INVITATIONS, RequestMessage.SAVE, locale, request);
        } else {
            return managementView(model, initiative, bindingResult, FULL, request, null);
        }
    }

    private boolean create(InitiativeManagement initiative, BindingResult bindingResult, Model model, Locale locale, HttpServletRequest request) {
        Urls urls = Urls.get(locale);
        model.addAttribute(ALT_URI_ATTR, urls.alt().createNew());

        Long id = initiativeService.create(initiative, bindingResult);
        initiative.assignId(id);
        return id != null;
    }

    @RequestMapping(value={ VIEW_FI, VIEW_SV }, method=POST) // , params=ACTION_SAVE - default action
    public String updatePost(@PathVariable Long id, @ModelAttribute("initiative") InitiativeManagement initiative, BindingResult bindingResult, Model model, Locale locale, HttpServletRequest request) {
        Urls urls = Urls.get(locale);
        EditMode editMode = getEditMode(request);

        initiative = update(id, initiative, bindingResult, editMode, model, locale, request);
        if (!bindingResult.hasErrors()) {
            return redirectWithMessage(urls.view(initiative.getId()), RequestMessage.SAVE, request);
        } else {
            return managementView(model, initiative, bindingResult, editMode, request, null);
        }
    }

    @RequestMapping(value={ VIEW_FI, VIEW_SV }, method=POST, params=ACTION_SAVE_AND_SEND_INVITATIONS)
    public String updateAndSendInvitationsPost(@PathVariable Long id, @ModelAttribute("initiative") InitiativeManagement initiative, BindingResult bindingResult, Model model, Locale locale, HttpServletRequest request) {
        Urls urls = Urls.get(locale);
        EditMode editMode = getEditMode(request);

        initiative = update(id, initiative, bindingResult, editMode, model, locale, request);
        if (!bindingResult.hasErrors()) {
            return sendInvitations(initiative.getId(), urls, editMode, model, RequestMessage.SAVE_AND_SEND_INVITATIONS, RequestMessage.SAVE, locale, request);
        } else {
            return managementView(model, initiative, bindingResult, editMode, request, null);
        }
    }

    private InitiativeManagement update(Long id, InitiativeManagement initiative, BindingResult bindingResult, EditMode editMode, Model model, Locale locale, HttpServletRequest request) {
        Urls urls = Urls.get(locale);
        model.addAttribute(ALT_URI_ATTR, urls.alt().view(id));
        initiative.assignId(id);

        if (editMode != NONE) {
            return initiativeService.update(initiative, editMode, bindingResult);
        } else {
            return initiative;
        }
    }

    @RequestMapping(value={ VIEW_FI, VIEW_SV }, method=POST, params=ACTION_UPDATE_VRK_RESOLUTION)
    public String updateVRKResolution(@PathVariable Long id, @ModelAttribute("initiative") InitiativeManagement initiative, BindingResult bindingResult, Model model, Locale locale, HttpServletRequest request) {
        Urls urls = Urls.get(locale);
        model.addAttribute(ALT_URI_ATTR, urls.alt().view(id));
        initiative.assignId(id);

        if (initiativeService.updateVRKResolution(initiative, bindingResult)) {
            return redirectWithMessage(urls.view(id), RequestMessage.SAVE_VRK_RESOLUTION, request);
        } else {
            return managementView(model, initiativeService.getInitiativeForManagement(id), bindingResult, NONE, request, null);
        }
    }

    @RequestMapping(value={ VIEW_FI, VIEW_SV }, method=POST, params=ACTION_SEND_INVITATIONS)
    public String sendInvitations(@PathVariable Long id, Model model, Locale locale, HttpServletRequest request) {
        Urls urls = Urls.get(locale);

        return sendInvitations(id, urls, NONE, model, RequestMessage.SEND_INVITATIONS, null, locale, request);
    }

    private String sendInvitations(Long initiativeId, Urls urls, EditMode editMode, Model model, RequestMessage successMessage, RequestMessage fallbackMessage, Locale locale, HttpServletRequest request) {
        if (initiativeService.sendInvitations(initiativeId)) {
            return redirectWithMessage(urls.view(initiativeId), successMessage, request);
        } else {
            if (fallbackMessage != null) {
                // Save success?
                addRequestMessage(fallbackMessage, null, request);
            }
            return redirectWithMessage(urls.view(initiativeId), RequestMessage.SEND_INVITATIONS_FAILED, request);
        }
    }

    /*
     * Search
     */
    @RequestMapping(value={ SEARCH_FI, SEARCH_SV }, method=GET)
    public String search(InitiativeSearch search, Model model, Locale locale) {
        Urls urls = Urls.get(locale);
        model.addAttribute(ALT_URI_ATTR, urls.alt().search());

        addPiwicIdIfNotAuthenticated(model);

        boolean isOmUser = userService.getCurrentUser().isOm();

        model.addAttribute("omUser", isOmUser);
        model.addAttribute("currentSearch", search);
        model.addAttribute("searchParameters", new SearchParameterGenerator(search));
        InitiativeSublistWithTotalCount initiativeSublistWithTotalCount = initiativeService.findInitiatives(search);
        List<InitiativeInfo> initiatives = initiativeSublistWithTotalCount.list;

        model.addAttribute("initiatives", initiatives);
        model.addAttribute("totalCount", initiativeSublistWithTotalCount.total);

        if (isOmUser && search.getSearchView() == SearchView.om) {
            model.addAttribute("initiativeCounts", initiativeService.getOmInitiativeCountByState());
        }
        else {
            model.addAttribute("initiativeCounts", initiativeService.getPublicInitiativeCountByState());
        }

        return SEARCH_VIEW;
 //       }
    }

    private boolean initiativeDeleted(Long initiativeId) {
        Set<Long> deletedInitiatives = new HashSet<>();
        deletedInitiatives.add(3348L);
        //For now we just hard code deleted initiatives
        return deletedInitiatives.contains(initiativeId);
    }
    /*
     * View  
     */
    @RequestMapping(value={ VIEW_HASH_FI, VIEW_HASH_SV }, method=GET)
    public String view(@PathVariable("id") Long initiativeId,
                       @PathVariable("hash") String hash,
                       @RequestParam(value = HISTORY_ITEM_PARAMETER, required = false) Long historyItemId,
                       @ModelAttribute("followInitiative") FollowInitiativeDto followInitiativeDto,
                       Model model, Locale locale, HttpServletRequest request) {
        if (initiativeDeleted(initiativeId)) {
            return ERROR_INITIATIVE_DELETED_VIEW;
        }

        Urls urls = Urls.get(locale);
        model.addAttribute(ALT_URI_ATTR, urls.alt().view(initiativeId, hash));
        model.addAttribute("idHash", hash);
        User user = userService.getCurrentUser();
        Author author = getCurrentAuthor(initiativeId, user.getId());

        String invitationCode = getSessionInvitationCode(initiativeId, request);

        addPiwicIdIfNotAuthenticated(model);

        // User needs to respond to invitation
        if (invitationCode != null) {
            InitiativePublic initiative = initiativeService.getInitiativeForPublic(initiativeId, hash);

            model.addAttribute(ATTR_INVITATION_CODE, invitationCode); // May be null
            model.addAttribute("initiative", initiative);

            addVotingInfo(initiative, model);

            return INVITATION_VIEW;
        }
        // Management view for authors, om and vrk officials 
        else if (author != null || user.isOm() || user.isVrk()) {
            InitiativeManagement initiative = initiativeService.getInitiativeForManagement(initiativeId);
            if (!(user.isOm() || user.isVrk())) {
                initiative = doNotShowMiddleName(initiative);
            }

            EditMode editMode;
            if (initiative.getState() == InitiativeState.DRAFT) {
                editMode = FULL;
            } else {
                editMode = getEditMode(request);
            }

            return managementView(model, initiative, null, editMode, request, historyItemId);
        }
        // Public view for anyone else
        else {
            InitiativePublic initiative = initiativeService.getInitiativeForPublic(initiativeId, hash);
            initiative = doNotShowMiddleName(initiative);
            model.addAttribute("initiative", initiative);
            addVotingInfo(initiative, model);

            return PUBLIC_VIEW;
        }

    }

    private InitiativeManagement doNotShowMiddleName(InitiativeManagement initiative) {
        initiative.getAuthors()
                .forEach(author -> author.assignFirstNames(getFirstOfFirstNames(author.getFirstNames())));
        initiative.getCurrentAuthor().assignFirstNames(getFirstOfFirstNames(initiative.getCurrentAuthor().getFirstNames()));
        initiative.getInitiators()
                .forEach(initiator -> initiator.assignFirstNames(getFirstOfFirstNames(initiator.getFirstNames())));
        initiative.getRepresentatives()
                .forEach(representative -> representative.assignFirstNames(getFirstOfFirstNames(representative.getFirstNames())));
        initiative.getReserves()
                .forEach(reserve -> reserve.assignFirstNames(getFirstOfFirstNames(reserve.getFirstNames())));
        return initiative;
    }

    private InitiativePublic doNotShowMiddleName(InitiativePublic initiative) {
        initiative.getInitiators()
                .forEach(initiator -> initiator.assignFirstNames(getFirstOfFirstNames(initiator.getFirstNames())));
        initiative.getRepresentatives()
                .forEach(representative -> representative.assignFirstNames(getFirstOfFirstNames(representative.getFirstNames())));
        initiative.getReserves()
                .forEach(reserve -> reserve.assignFirstNames(getFirstOfFirstNames(reserve.getFirstNames())));
        return initiative;
    }

    private String getFirstOfFirstNames(String firstNames) {
        return firstNames.split(" ")[0];
    }

    @RequestMapping(value={ VIEW_FI, VIEW_SV }, method=GET)
    public String view(@PathVariable("id") Long initiativeId,
                       @RequestParam(value = HISTORY_ITEM_PARAMETER, required = false) Long historyItemId,
                       @ModelAttribute("followInitiative") FollowInitiativeDto followInitiativeDto,
                       Model model, Locale locale, HttpServletRequest request) {
        return view(initiativeId, null, historyItemId, followInitiativeDto, model, locale, request);
    }

    @RequestMapping(value = { VIEW_FI, VIEW_SV }, method = RequestMethod.POST, params = "action-follow")
    public String followInitiative(@PathVariable("id") long initiativeId,
                                   @RequestParam(value = "g-recaptcha-response", required = false) String gRecaptchaResponse,
                                   @ModelAttribute("followInitiative") FollowInitiativeDto followInitiativeDto, Model model, BindingResult bindingResult, Locale locale, HttpServletRequest request) {
        // This also validates the data. For some reason @Valid did not work.
        followService.followInitiative(initiativeId, followInitiativeDto, bindingResult, gRecaptchaResponse);

        if (bindingResult.hasErrors()) {
            return view(initiativeId, null, null, followInitiativeDto, model, locale, request);
        }
        else {
            return redirectWithMessage(Urls.get(locale).view(initiativeId), RequestMessage.FOLLOW_ACCEPTED, request);
        }
    }

    @RequestMapping(value = { UNSUBSCRIBE_FOLLOW_FI, UNSUBSCRIBE_FOLLOW_SV}, method = GET)
    public String unsubscribeFollowing(@PathVariable("id") Long initiativeId,
                                       @PathVariable("hash") String unsubscribeHash,
                                       Locale locale,
                                       HttpServletRequest request) {

        followService.unFollowInitiative(initiativeId, unsubscribeHash);
        return redirectWithMessage(Urls.get(locale).view(initiativeId), RequestMessage.FOLLOW_CANCELLED, request);
    }

    @ModelAttribute
    public void addInitiativeDefaults(Locale locale, HttpServletRequest request, Model model) {
        model.addAttribute("invitationExpirationDays", initiativeSettings.getInvitationExpirationDays());
        model.addAttribute("votingDuration", initiativeSettings.getVotingDuration());
        model.addAttribute("minSupportCountForSearch", initiativeSettings.getMinSupportCountForSearch());
        model.addAttribute("requiredMinSupportCountDuration", initiativeSettings.getRequiredMinSupportCountDuration());
        model.addAttribute("requiredVoteCount", initiativeSettings.getRequiredVoteCount());
        model.addAttribute("sendToVrkDuration", initiativeSettings.getSendToVrkDuration());
        model.addAttribute("sendToParliamentDuration", initiativeSettings.getSendToParliamentDuration());
        model.addAttribute("omSearchBeforeVotesRemovalDuration", initiativeSettings.getOmSearchBeforeVotesRemovalDuration());
        model.addAttribute("votesRemovalDuration", initiativeSettings.getVotesRemovalDuration());
    }

    @RequestMapping(value={ VIEW_HASH_FI, VIEW_HASH_SV }, method=GET, params=PARAM_INVITATION_CODE)
    public String respondToInvitationGet(@PathVariable("id") Long initiativeId,
                                         @PathVariable("hash") String hash,
                                         @RequestParam(PARAM_INVITATION_CODE) String invitationCode, Locale locale, HttpServletRequest request) {
        Urls urls = Urls.get(locale);
        InitiativePublic initiative = initiativeService.getInitiativeForPublic(initiativeId, hash);
        if (initiative.getState() == InitiativeState.PROPOSAL) {    //accepting invitations is allowed only in PROPOSAL state
            // Store attribute for later reference
            setSessionInvitationCode(initiativeId, invitationCode, request);
        } else {
            return redirectWithMessage(urls.view(initiativeId, hash), RequestMessage.ACCEPTING_INVITATIONS_NOT_ALLOWED, request);
        }

        return contextRelativeRedirect(urls.view(initiativeId, hash));
    }


    @RequestMapping(value={ VIEW_HASH_FI, VIEW_HASH_SV }, method=POST, params=ACTION_DECLINE_INVITATION)
    public String declineInvitationPost(@PathVariable("id") Long initiativeId,
                                        @PathVariable("hash") String hash,
                                        @RequestParam(PARAM_INVITATION_CODE) String invitationCode, Model model, Locale locale, HttpServletRequest request) {
        Urls urls = Urls.get(locale);

        clearSessionInvitationCode(initiativeId, request);

        if (initiativeService.declineInvitation(initiativeId, invitationCode)) {
            return redirectWithMessage(urls.view(initiativeId, hash), RequestMessage.DECLINE_INVITATION, request);
        } else {
            return redirectWithMessage(urls.view(initiativeId, hash), RequestMessage.OPEN_INVITATION_FAILED, request);
        }
    }

    @RequestMapping(value={ VIEW_HASH_FI, VIEW_HASH_SV }, method=GET, params=ACTION_ACCEPT_INVITATION)
    public String acceptInvitationGet(@PathVariable("id") Long initiativeId,
                                      @PathVariable("hash") String hash,
                                      Model model, Locale locale, HttpServletRequest request) {
        // Allow redirecting to authentication
        User user = userService.getUserInRole(Role.AUTHENTICATED);

        Urls urls = Urls.get(locale);
        model.addAttribute(ALT_URI_ATTR, urls.alt().view(initiativeId));

        String invitationCode = getSessionInvitationCode(initiativeId, request);
        if (Strings.isNullOrEmpty(invitationCode)) {
            return contextRelativeRedirect(urls.view(initiativeId));
        }

        Invitation invitation = initiativeService.getInvitation(initiativeId, invitationCode);
         if (invitation == null) { // missing, used or expired invitation
            clearSessionInvitationCode(initiativeId, request);
            return redirectWithMessage(urls.view(initiativeId, hash), RequestMessage.OPEN_INVITATION_FAILED, request);
        }

        Author author = getCurrentAuthor(initiativeId, user.getId());
        if (author != null) {
            author.setInitiator(author.isInitiator() || invitation.isInitiator());
            author.setRepresentative(author.isRepresentative() || invitation.isRepresentative());
            author.setReserve(author.isReserve() || invitation.isReserve());
        } else {
            author = toAuthor(user, invitation);
        }

        return acceptInvitationView(initiativeId, invitation, author, model, hash);
    }

    @RequestMapping(value={ VIEW_HASH_FI, VIEW_HASH_SV }, method=POST, params=ACTION_ACCEPT_INVITATION)
    public String acceptInvitationPost(@PathVariable("id") Long initiativeId,
                                       @PathVariable("hash") String hash,
                                       @RequestParam(PARAM_INVITATION_CODE) String invitationCode, @ModelAttribute("currentAuthor") Author author, BindingResult bindingResult, Model model, Locale locale, HttpServletRequest request) {
        Urls urls = Urls.get(locale);
        model.addAttribute(ALT_URI_ATTR, urls.alt().view(initiativeId));

        if (initiativeService.acceptInvitation(initiativeId, invitationCode, author, bindingResult)) {
            clearSessionInvitationCode(initiativeId, request);
            return redirectWithMessage(urls.view(initiativeId), RequestMessage.ACCEPT_INVITATION, request);
        } else {
            Invitation invitation = initiativeService.getInvitation(initiativeId, invitationCode);
            return acceptInvitationView(initiativeId, invitation, author, model, hash);
        }
    }

    @RequestMapping(value={ VIEW_FI, VIEW_SV }, method=POST, params=ACTION_CONFIRM_CURRENT_AUTHOR)
    public String confirmCurrentAuthorPost(@PathVariable("id") Long initiativeId, Locale locale, HttpServletRequest request) {
        Urls urls = Urls.get(locale);

        initiativeService.confirmCurrentAuthor(initiativeId);

        return redirectWithMessage(urls.view(initiativeId), RequestMessage.CONFIRM_CURRENT_AUTHOR, request);
    }

    @RequestMapping(value={ VIEW_FI, VIEW_SV }, method=POST, params=ACTION_DELETE_CURRENT_AUTHOR)
    public String deleteCurrentAuthorPost(@PathVariable("id") Long initiativeId, Locale locale, HttpServletRequest request) {
        Urls urls = Urls.get(locale);

        initiativeService.deleteCurrentAuthor(initiativeId);

        return redirectWithMessage(urls.view(initiativeId), RequestMessage.DELETE_CURRENT_AUTHOR, request);
    }

    @RequestMapping(value={ VIEW_FI, VIEW_SV }, method=POST, params=ACTION_SEND_TO_OM)
    public String sendToOMPost(@PathVariable("id") Long initiativeId, Model model, Locale locale, HttpServletRequest request) {
        Urls urls = Urls.get(locale);

        initiativeService.sendToOM(initiativeId);

        return redirectWithMessage(urls.view(initiativeId), RequestMessage.SEND_TO_OM, request);
    }

    @RequestMapping(value={ VIEW_FI, VIEW_SV }, method=POST, params=ACTION_ACCEPT_BY_OM)
    public String acceptByOm(@PathVariable("id") Long initiativeId, @RequestParam(value="comment", required=false) String comment,
            @RequestParam(value="acceptanceIdentifier", required=false) String acceptanceIdentifier, Locale locale, HttpServletRequest request) {

        Urls urls = Urls.get(locale);

        initiativeService.respondByOm(initiativeId, true, comment, acceptanceIdentifier);

        return redirectWithMessage(urls.view(initiativeId), RequestMessage.ACCEPT_BY_OM, request);
    }

    @RequestMapping(value={VIEW_FI, VIEW_SV}, method = POST, params = ACTION_SEND_TO_PARLIAMENT_BY_OM)
    public String sendToParliamentByOm(@PathVariable("id") Long initiativeId,
                                       @ModelAttribute("initiative") InitiativeManagement initiative,
                                       BindingResult errors, Model model, Locale locale, HttpServletRequest request) {

        Urls urls = Urls.get(locale);

        initiative.assignId(initiativeId);
        initiativeService.updateSendToParliament(initiative, errors);

        if (errors.hasErrors()) {
            return managementView(model, initiativeService.getInitiativeForManagement(initiativeId), errors, NONE, request, null);
        } else {
            return redirectWithMessage(urls.view(initiativeId), RequestMessage.SENT_TO_PARLIAMENT_UPDATED, request);
        }
    }

    @RequestMapping(value={ VIEW_FI, VIEW_SV }, method=POST, params=ACTION_REJECT_BY_OM)
    public String rejectByOm(@PathVariable("id") Long initiativeId, @RequestParam(value="comment", required=false) String comment, Locale locale, HttpServletRequest request) {
        Urls urls = Urls.get(locale);

        initiativeService.respondByOm(initiativeId, false, comment, null);
        return redirectWithMessage(urls.view(initiativeId), RequestMessage.REJECT_BY_OM, request);
    }

    @RequestMapping(value={ VIEW_FI, VIEW_SV }, method=POST, params=ACTION_COMMENT_BY_OM)
    public String commentByOm(@PathVariable("id") Long initiativeId, @RequestParam(value="moderatorComment", required=true) String comment, Locale locale, HttpServletRequest request) {
        Urls urls = Urls.get(locale);

        initiativeService.commentByOm(initiativeId, comment);
        return redirectWithMessage(urls.view(initiativeId), RequestMessage.COMMENT_BY_OM, request);
    }

    /*
     * REST
     */
    @RequestMapping(value=INITIATIVES, method=GET, produces=JSON)
    public @ResponseBody List<InitiativeInfo> jsonList(@RequestParam(value = JSON_OFFSET, required = false) Integer offset,
                                                       @RequestParam(value = JSON_LIMIT, required = false) Integer limit,
                                                       @RequestParam(value = JSON_MINSUPPORTCOUNT, required = false) Integer minSupportCount,
                                                       @RequestParam(value = JSON_ORDER_BY, required = false) OrderBy orderBy) {

        if (limit == null) {
            limit = DEFAULT_INITIATIVE_JSON_RESULT_COUNT;
        }

        if (minSupportCount == null) {
            minSupportCount = DEFAULT_INITIATIVE_MINSUPPORTCOUNT;
        }

        // Do not expose own initiatives through this method
        InitiativeSearch search = new InitiativeSearch();
        search.setLimit(Math.min(MAX_INITIATIVE_JSON_RESULT_COUNT, limit));
        search.setMinSupportCount(minSupportCount);
        search.setOrderBy(OrderBy.id);
        search.setShow(Show.all);
        if (offset != null) {
            search.setOffset(offset);
        }
        if (orderBy != null) {
            search.setOrderBy(orderBy);
        }
        return initiativeService.findInitiatives(search).list;
    }
    @RequestMapping(value=INITIATIVES, method=GET, produces=JSONP, params=JSONP_CALLBACK)
    public @ResponseBody JsonpObject<List<InitiativeInfo>> jsonpList(@RequestParam(JSONP_CALLBACK) String callback,
                                                                     @RequestParam(value = JSON_OFFSET, required = false) Integer offset,
                                                                     @RequestParam(value = JSON_LIMIT, required = false) Integer limit,
                                                                     @RequestParam(value = JSON_MINSUPPORTCOUNT, required = false) Integer minSupportCount,
                                                                     @RequestParam(value = JSON_ORDER_BY, required = false) OrderBy orderBy) {
        return new JsonpObject<>(callback, jsonList(offset, limit, minSupportCount, orderBy));
    }

    @RequestMapping(value = SUPPORT_COUNT, method = GET, produces = JSON)
    public @ResponseBody SupportCount jsonSupportCount(@PathVariable Long id) {
        return initiativeService.getSupportCount(id);
    }

    @RequestMapping(value=INITIATIVE, method=GET, produces=JSON)
    public @ResponseBody InitiativePublic jsonGet(@PathVariable Long id) {
        return initiativeService.getInitiativeForPublic(id);
    }
    @RequestMapping(value=INITIATIVE, method=GET, produces=JSONP, params=JSONP_CALLBACK)
    public @ResponseBody JsonpObject<InitiativePublic> jsonGet(@PathVariable Long id, @RequestParam(JSONP_CALLBACK) String callback) {
        return new JsonpObject<>(callback, jsonGet(id));
    }

    @RequestMapping(value = SUPPORTS_BY_DATE, method=GET, produces=JSON)
    public @ResponseBody JsonNode jsonSupportsByDate(@PathVariable Long id) throws IOException {
        return objectMapper.readTree(supportVoteService.getSupportVotesPerDateJson(id));
    }

    @RequestMapping(value=SUPPORTS_BY_DATE, method=GET, produces=JSONP, params=JSONP_CALLBACK)
    public @ResponseBody JsonpObject<JsonNode> jsonSupportsByDate(@PathVariable Long id, @RequestParam(JSONP_CALLBACK) String callback) throws IOException {
        return new JsonpObject<>(callback, jsonSupportsByDate(id));
    }

    @RequestMapping(value=KEEPALIVE, method=POST, produces=JSON)
    public @ResponseBody Boolean keepalivePost() {
        try {
            return Boolean.valueOf(userService.getCurrentUser().isAuthenticated());
        } catch (RuntimeException e) {
            log.warn("Keepalive userService.getCurrentUser() threw an exception", e);
            return Boolean.FALSE;
        }
    }

    @RequestMapping(value={SUPPORT_STATEMENT_PDF_FI, SUPPORT_STATEMENT_PDF_SV}, method=GET)
    public void supportStatementPdf(@PathVariable Long id, Locale locale, HttpServletResponse response) throws IOException, DocumentException {

        InitiativePublic initiativeForManagement = initiativeService.getInitiativeForPublic(id);

        LocalizedString localizedName = initiativeForManagement.getName();

        String name;
        if (Locales.LOCALE_SV.equals(locale) && localizedName.hasTranslation(Locales.LOCALE_SV)) {
            name = localizedName.getSv();
        }
        else {
            name = localizedName.getFi();
        }

        ByteArrayOutputStream byteArrayOutputStream = supportStatementPdfGenerator.generatePdf(name, initiativeForManagement.getStartDate(), Locales.LOCALE_FI.equals(locale));
        byte[] bytes = byteArrayOutputStream.toByteArray();

        response.setContentType(MediaType.parseMediaType("application/pdf").toString());
        response.setContentLength(bytes.length);

        // These are for old IE-versions for not being able to download pdf-files via https if headers have some no-cache attributes.
        response.setHeader("Cache-Control", "private");
        response.setHeader("Pragma", "private");
        response.getOutputStream().write(bytes);

    }

    @RequestMapping(value={ IFRAME_FI, IFRAME_SV }, method=GET)
    public String iFrame(@PathVariable("id") Long initiativeId, Model model, Locale locale, HttpServletRequest request) {
    	InitiativePublic initiative = initiativeService.getInitiativeForPublic(initiativeId, null);

        model.addAttribute("initiative", initiative);
        addVotingInfoToIframe(initiative, model);

        return IFRAME_VIEW;
    }

    @RequestMapping(value={ IFRAME_GENERATOR_FI, IFRAME_GENERATOR_SV }, method=GET)
    public String iFrameGenerator(Model model, Locale locale) {
        model.addAttribute(ALT_URI_ATTR, Urls.get(locale).alt().widget());
        return IFRAME_GENERATOR_VIEW;
    }


    @InitBinder
    public void initBinder(WebDataBinder binder, Locale locale) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(LocalDate.class, new LocalDateEditor(messageSource.getMessage("date.format", null, locale)));
        // NOTE: This editor is implicit by Java Bean PropertyEditor naming convention
    }
    
    
    /*
     * HELPERS
     */

    private Author getCurrentAuthor(Long initiativeId, Long userId) {
        if (userId != null) {
            return initiativeService.getAuthor(initiativeId, userId);
        } else {
            return null;
        }
    }

    private Author toAuthor(User user) {
        Author author = new Author(user);
        author.setInitiator(true);
        author.setRepresentative(true);
        return author;
    }

    private Author toAuthor(User user, Invitation invitation) {
        Author author = new Author(user);
        author.assignEmail(invitation.getEmail());
        author.setInitiator(invitation.isInitiator());
        author.setRepresentative(invitation.isRepresentative());
        author.setReserve(invitation.isReserve());
        return author;
    }

    private String getSessionInvitationCode(Long initiativeId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        } else {
            return (String) session.getAttribute(invitationCodeSessionAttribute(initiativeId));
        }
    }

    private void clearSessionInvitationCode(Long initiativeId, HttpServletRequest request) {
        setSessionInvitationCode(initiativeId, null, request);
    }

    private void setSessionInvitationCode(Long initiativeId, String invitationCode, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String attributeName = invitationCodeSessionAttribute(initiativeId);
        if (invitationCode == null) {
            session.removeAttribute(attributeName);
        } else {
            session.setAttribute(attributeName, invitationCode);
        }
    }

    private String invitationCodeSessionAttribute(Long initiativeId) {
        return ATTR_INVITATION_CODE + ":" + initiativeId;
    }

    private void addVotingInfo(InitiativeBase initiative, Model model) {
        model.addAttribute(ATTR_VOTING_INFO, supportVoteService.getVotingInfo(initiative));
        model.addAttribute("supportCountData", supportVoteService.getSupportVotesPerDateJson(initiative.getId()));
    }

    private void addVotingInfoToIframe(InitiativePublic initiative, Model model) {
        model.addAttribute(ATTR_VOTING_INFO, supportVoteService.getVotingInfoWithoutUserData(initiative));
        model.addAttribute("supportCountData", supportVoteService.getSupportVotesPerDateJson(initiative.getId()));
    }

    private String acceptInvitationView(Long initiativeId, Invitation invitation, Author author, Model model, String hash) {
        model.addAttribute("invitation", invitation);
        model.addAttribute("currentAuthor", author);
        model.addAttribute("initiative", initiativeService.getInitiativeForPublic(initiativeId, hash));

        return ACCEPT_INVITATION_VIEW;
    }

    private String managementView(Model model, InitiativeManagement initiative, BindingResult bindingResult, EditMode editMode, HttpServletRequest request, Long historyItemId) {
        Author currentAuthor = initiative.getCurrentAuthor();
        model.addAttribute("currentAuthor", currentAuthor);

        User currentUser = userService.getUserInRole(Role.AUTHENTICATED);
        addVotingInfo(initiative, model);

        ManagementSettings managementSettings  = initiativeSettings.getManagementSettings(initiative, editMode, currentUser);

        if (managementSettings.isAllowConfirmCurrentAuthor()) {
            model.addAttribute("initiative", new InitiativePublic(initiative));

            return UNCONFIRMED_AUTHOR;
        } else {

            model.addAttribute("initiative", initiative);
            // NOTE: BindingAwareModelMap removes BindingResults if object doesn't match target -> re-add binding results
            if (bindingResult != null) {
                model.addAttribute(BindingResult.MODEL_KEY_PREFIX + "initiative", bindingResult);
            }
            model.addAttribute("managementSettings", managementSettings);

            if (initiative.getId() != null) {
                model.addAttribute("supportVoteBatches", supportVoteService.getSupportVoteBatches(initiative));
            } else {
                model.addAttribute("supportVoteBatches", Collections.EMPTY_LIST);
            }

            if (currentAuthor != null) {
                return INITIATIVE_AUTHOR;
            } else if (currentUser.isOm()) {
                if (initiative.getState() == InitiativeState.REVIEW) {
                    model.addAttribute("potentialLinks", initiativeService.checkProposalAndRationalForLinks(initiative));
                }
                addReviewHistory(initiative.getId(), historyItemId, model, currentUser);
                return INITIATIVE_OM;
            } else if (currentUser.isVrk()) {
                return INITIATIVE_VRK;
            } else {
                throw new IllegalStateException("User is not an author, om or vrk official");
            }
        }
    }

    private void addReviewHistory(Long initiativeId, Long historyItemId, Model model, User user) {
        List<ReviewHistoryRow> reviewHistory = initiativeService.findReviewHistory(initiativeId);
        model.addAttribute("reviewHistories", reviewHistory);
        Maybe<ReviewHistoryDiff> reviewHistoryDiff = Maybe.absent();
        if (historyItemId != null) {
            reviewHistoryDiff = Maybe.of(ReviewHistoryDiff.from(reviewHistory, historyItemId));
        }
        model.addAttribute("reviewHistoryDiff", reviewHistoryDiff);

    }


    private EditMode getEditMode(HttpServletRequest request) {
        String str = request.getParameter("edit");
        EditMode editMode;
        if (str != null) {
            editMode = EditMode.valueOf(str);
        } else {
            editMode = EditMode.NONE;
        }
        return editMode;
    }

}
