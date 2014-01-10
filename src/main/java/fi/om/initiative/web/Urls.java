package fi.om.initiative.web;

import com.google.common.base.Strings;
import fi.om.initiative.dto.search.SearchView;
import fi.om.initiative.dto.search.Show;
import fi.om.initiative.util.Locales;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import static fi.om.initiative.util.Locales.LOCALE_FI;
import static fi.om.initiative.util.Locales.LOCALE_SV;


public final class Urls {
    
    public static final String ID_PARAMETER = "{id}";

    public static final String HASH_PARAMETER = "{hash}";

    public static final String HELP_PAGE_PARAMETER = "{helpPage}";

    public static final String INFO_PAGE_PARAMETER = "{infoPage}";
    
    public static final String FILE_NAME_PARAMETER = "{fileName}";
    
    public static final String FRONT =        "/";

    public static final String FRONT_FI =     "/fi";
    
    public static final String FRONT_SV =     "/sv";

    public static final String HELP_INDEX_FI =    "/fi/ohjeet";

    public static final String HELP_INDEX_SV =    "/sv/anvisningar";

    public static final String HELP_FI =    HELP_INDEX_FI + "/" + HELP_PAGE_PARAMETER;

    public static final String HELP_SV =    HELP_INDEX_SV + "/" + HELP_PAGE_PARAMETER;

    public static final String HELP_EDIT_INDEX_FI = "/fi/ohjemuokkaus";

    public static final String HELP_EDIT_INDEX_SV = "/sv/ohjemuokkaus";
    
    public static final String CONTENT_EDITOR_HELP = "/fi/sisallonsyoton-ohje";

    public static final String HELP_EDIT_FI =    HELP_EDIT_INDEX_FI + "/" + HELP_PAGE_PARAMETER;

    public static final String HELP_EDIT_SV =    HELP_EDIT_INDEX_SV + "/" + HELP_PAGE_PARAMETER;

    public static final String INFO_INDEX_FI =    "/fi/tietoa";
    
    public static final String INFO_INDEX_SV =    "/sv/om-tjansten";

    public static final String INFO_FI =    INFO_INDEX_FI + "/" + INFO_PAGE_PARAMETER;
    
    public static final String INFO_SV =    INFO_INDEX_SV + "/" + INFO_PAGE_PARAMETER;

    public static final String CREATE_FI =    "/fi/aloite";
    
    public static final String CREATE_SV =    "/sv/initiativ";

    public static final String LOGIN_FI =        "/fi/login";

    public static final String LOGIN_SV =        "/sv/login";

    public static final String VETUMA_ERROR_FI = "/fi/loginerror";

    public static final String VETUMA_ERROR_SV = "/sv/loginerror";
    
    public static final String LOGOUT_FI =       "/fi/logout";
    
    public static final String LOGOUT_SV =       "/sv/logout";

    public static final String VIEW_FI = CREATE_FI + "/" + ID_PARAMETER;

    public static final String VIEW_SV = CREATE_SV + "/" + ID_PARAMETER;

    public static final String VIEW_HASH_FI = CREATE_FI + "/" + ID_PARAMETER+"/"+HASH_PARAMETER;

    public static final String VIEW_HASH_SV = CREATE_SV + "/" + ID_PARAMETER+"/"+HASH_PARAMETER;
    
    public static final String SEARCH_FI =    "/fi/hae";
    
    public static final String SEARCH_SV =    "/sv/sok";
    
    public static final String MY_ACCOUNT_FI =    "/fi/omat-tiedot";
    
    public static final String MY_ACCOUNT_SV =    "/sv/egna-upgifter";

    public static final String TEST_DATA_GENERATION_FI =    "/fi/testdata";
    
    public static final String TEST_DATA_GENERATION_SV =    "/sv/testdata";
    
    public static final String DOWNLOAD_VOTES = "/support-votes/" + ID_PARAMETER + "/" + FILE_NAME_PARAMETER;
    
    public static final String API = "/api";
    
    public static final String INITIATIVES =  API + "/v1/initiatives";

    public static final String INITIATIVE =   INITIATIVES + "/" + ID_PARAMETER;

    public static final String SUPPORT_COUNT = API+"/v1/supports/" + ID_PARAMETER;

    public static final String STATUS =  "/status";
    
    public static final String SERVICES = "/services";
    
    public static final String KEEPALIVE =  SERVICES + "/keepalive";

    public static final String SUPPORT_STATEMENT_PDF_FI = "/fi/kannatusilmoituslomake/Kansalaisaloite-"+ID_PARAMETER+"-kannatusilmoitus.pdf";

    public static final String SUPPORT_STATEMENT_PDF_SV = "/sv/stodforklaring/Medborgarinitiativ-"+ID_PARAMETER+"-stodforklaring.pdf";

    public static final String ERROR_404 = "/404";

    public static final String ERROR_404_GLOBAL = "/404-global";
    
    public static final String ERROR_500 = "/500";
    
    
    public static final String NEWS_FI = "/fi/tiedotteet";
    
    public static final String NEWS_SV = "/sv/aktuellt";

    public static final String IMAGE_JSON =    "/imageJson";

    public static final String IMAGES = "/images";

    public static Urls FI = null;
    
    public static Urls SV = null;
    
    public static final String ENCODING = "UTF-8";
    
    public static final String PARAM_INVITATION_CODE = "invitation";
    
    public static final String SEARCH_UNREMOVED_VOTES = "searchUnremovedVotes";

    public static final String SEARCH_OWN_ONLY = "searchView="+ SearchView.own.name();

    public static final String SEARCH_OM_VIEW = "searchView="+ SearchView.om.name();

    public static final String ACTION_SAVE_AND_SEND_INVITATIONS = "action-save-and-send-invitations";
    
    public static final String ACTION_SEND_INVITATIONS = "action-send-invitations";
    
    public static final String ACTION_SAVE = "action-save";

    public static final String ACTION_ACCEPT_INVITATION = "action-accept-invitation";

    public static final String ACTION_DECLINE_INVITATION = "action-decline-invitation";
    
    public static final String ACTION_UPDATE_VRK_RESOLUTION = "action-vrk-update-resolution";
    
    public static final String ACTION_VOTE = "action-vote";
    
    public static final String ACTION_SEND_TO_OM = "action-send-to-om";
    
    public static final String ACTION_ACCEPT_BY_OM = "action-accept-by-om";

    public static final String ACTION_SEND_TO_PARLIAMENT_BY_OM = "action-send-to-parliament-by-om";
    
    public static final String ACTION_REJECT_BY_OM = "action-reject-by-om";
    
    public static final String ACTION_SEND_TO_VRK = "action-send-to-vrk"; 

    public static final String ACTION_CONFIRM_CURRENT_AUTHOR = "action-confirm-current-author";

    public static final String ACTION_DELETE_CURRENT_AUTHOR = "action-delete-current-author";

    public static final String ACTION_REMOVE_SUPPORT_VOTES = "action-remove-support-votes";

    public static final String JSONP_CALLBACK = "jsonp";

    public static final String JSON_OFFSET = "offset";

    public static final String JSON_LIMIT = "limit";

    public static final String JSON_MINSUPPORTCOUNT = "minSupportCount";

    // Actions for the content editor 
    
    public static final String ACTION_EDITOR_SAVE_DRAFT = "action-editor-save-draft";
    
    public static final String ACTION_EDITOR_PUBLISH_DRAFT = "action-editor-publish-draft";
    
    public static final String ACTION_EDITOR_RESTORE_PUBLISHED = "action-editor-restore-published";
    
    public static final String ACTION_EDITOR_UPLOAD_IMAGE = "action-editor-upload-image";
    
    // Following values are here so they can be used at help-pages.

    public static final int MAX_INITIATIVE_JSON_RESULT_COUNT = 50;

    public static final int DEFAULT_INITIATIVE_JSON_RESULT_COUNT = 20;

    public static final int DEFAULT_INITIATIVE_MINSUPPORTCOUNT = 50;

    public static final int DEFAULT_INITIATIVE_SEARCH_LIMIT = 20;

    public static final int MAX_INITIATIVE_SEARCH_LIMIT = 500;

    private final String baseUrl;
    
    private final Locale locale;
    
    public static void initUrls(String baseUrl) {
        FI = new Urls(baseUrl, LOCALE_FI);
        SV = new Urls(baseUrl, LOCALE_SV);
    }
    
    private Urls(String baseUrl, Locale locale) {
        this.baseUrl = baseUrl;
        this.locale = locale;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    private String getLocalizedPageUrl(String fiSuffix, String svSuffix) {
        StringBuilder stringBuilder = new StringBuilder(baseUrl);
        if (this == FI) {
            return stringBuilder.append(fiSuffix).toString();
        } else {
            return stringBuilder.append(svSuffix).toString();
        }
    }
    
    public String frontpage() {
        return getLocalizedPageUrl(FRONT_FI, FRONT_SV);
    }
    
    public String helpIndex() {
        return getLocalizedPageUrl(HELP_INDEX_FI, HELP_INDEX_SV);
    }

    public String help(String localizedPageName) {
        return getLocalizedPageUrl(HELP_FI, HELP_SV).replace(HELP_PAGE_PARAMETER, localizedPageName);
    }

    public String helpEdit(String localizedPageName) {
        return getLocalizedPageUrl(HELP_EDIT_FI, HELP_EDIT_SV).replace(HELP_PAGE_PARAMETER, localizedPageName);
    }
    
    public String contentEditorHelp() {
        return baseUrl + CONTENT_EDITOR_HELP;
    }

    public String view(Long initiativeId) {
        return getLocalizedPageUrl(VIEW_FI, VIEW_SV).replace(ID_PARAMETER, initiativeId.toString());
    }

    public String view(Long initiativeId, String hash) {
        if (hash != null) {
            return getLocalizedPageUrl(VIEW_HASH_FI, VIEW_HASH_SV)
                    .replace(ID_PARAMETER, initiativeId.toString())
                    .replace(HASH_PARAMETER, hash);
        }
        return view(initiativeId);
    }
    
    public String vote(Long initiativeId) {
        return view(initiativeId) + "?" + ACTION_VOTE;
    }
    
    public String initiative(Long initiativeId) {
        return baseUrl + INITIATIVE.replace(ID_PARAMETER, initiativeId.toString());
    }
    
    public String initiatives() {
        return baseUrl + INITIATIVES;
    }
    
    public String voteAction(Long initiativeId) {
        return view(initiativeId);
    }

    public String invitation(Long initiativeId, String hash, String invitationCode) {
        return view(initiativeId, hash) + "?" + PARAM_INVITATION_CODE + "=" + invitationCode;
    }

    public String confirmAcceptInvitation(Long initiativeId, String idHash) {
        return view(initiativeId, idHash) + "?" + ACTION_ACCEPT_INVITATION;
    }
    
    public String search() {
        return getLocalizedPageUrl(SEARCH_FI, SEARCH_SV);
    }

    public String news() {
        return getLocalizedPageUrl(NEWS_FI, NEWS_SV);
    }

    public String supportStatementPdf(Long initiativeId) {
        return getLocalizedPageUrl(SUPPORT_STATEMENT_PDF_FI, SUPPORT_STATEMENT_PDF_SV).replace(ID_PARAMETER, initiativeId.toString());
    }

    public String vetumaError() {
        return getLocalizedPageUrl(VETUMA_ERROR_FI, VETUMA_ERROR_SV);
    }

    public String searchUnremovedVotes() {
        return searchUnremovedVotes(""); // default search
    }

    public String searchUnremovedVotes(String periodBeforeDeadLine) {
        return search() +  "?" + SEARCH_UNREMOVED_VOTES + "=" + periodBeforeDeadLine;
    }

    public String searchOwnOnly() {
        return search() + "?" + SEARCH_OWN_ONLY;
    }

    public String searchOmView() {
        return search() + "?" + SEARCH_OM_VIEW+"&show="+ Show.preparation.name();
    }

    public String createNew() {
        return getLocalizedPageUrl(CREATE_FI, CREATE_SV);
    }

    public String images() {
        return IMAGES;
    }

    public String paramSendInvitations() {
        return ACTION_SEND_INVITATIONS;
    }

    public String login() {
        return getLocalizedPageUrl(LOGIN_FI, LOGIN_SV);
    }

    public String login(String target) {
        if (Strings.isNullOrEmpty(target)) {
            target = baseUrl;
        }

        return login() + "?target=" + urlEncode(target);
    }

    public String testDataGeneration() {
        return getLocalizedPageUrl(TEST_DATA_GENERATION_FI, TEST_DATA_GENERATION_SV);
    }

    public String downloadVotes(Long batchId, String fileName) {
        String url = DOWNLOAD_VOTES
                .replace(ID_PARAMETER, batchId.toString())
                .replace(FILE_NAME_PARAMETER, urlEncode(fileName));
        return baseUrl + url;
    }

    public String logout() {
        return getLocalizedPageUrl(LOGOUT_FI, LOGOUT_SV);
    }

    public Locale getLocale() {
        return locale;
    }

    public String getLang() {
        return locale.getLanguage();
    }

    public Urls alt() {
        if (this == FI) {
            return SV;
        } else {
            return FI;
        }
    }

    public String myAccount() {
        return getLocalizedPageUrl(MY_ACCOUNT_FI, MY_ACCOUNT_SV);
    }

    public Locale getAltLocale() {
        return alt().getLocale();
    }

    public String getAltLang() {
        return getAltLocale().getLanguage();
    }

    public static Urls get(Locale locale) {
        if (Locales.LOCALE_SV.equals(locale)) {
            return SV;
        } else {
            return FI;
        }
    }

    public static String urlPercentEncode(String s) {
        String ret = s;
        ret = ret.replaceAll("%", "%25");
        ret = ret.replaceAll(" ", "%20");
        ret = ret.replaceAll(":", "%3A");
        ret = ret.replaceAll("ä|\u00E4|&auml;",  "%C3%A4");
        ret = ret.replaceAll("å|\u00E5|&aring;", "%C3%A5");
        ret = ret.replaceAll("ö|\u00F6|&ouml;",  "%C3%B6");

        // NOTE: add other chars if needed or would be nice to user some existing converter ...
        return ret;
    }

    private static String urlEncode(String s) {
        if (s == null) {
            return "";
        } else {
            try {
                return URLEncoder.encode(s, ENCODING);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isLoginPage(String target) {
        return target.startsWith(login());
    }
}
