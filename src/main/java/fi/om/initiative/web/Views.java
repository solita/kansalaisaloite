package fi.om.initiative.web;


public final class Views {

    private Views() {}
    
    public static final String INITIATIVE_AUTHOR = "initiative-author";
    
    public static final String INITIATIVE_OM = "initiative-om";
    
    public static final String INITIATIVE_VRK = "initiative-vrk";
    
    public static final String BEFORE_CREATE_VIEW = "before-create";
    
    public static final String PUBLIC_VIEW = "initiative-public";
    
    public static final String INVITATION_VIEW = "invitation-response";
    
    public static final String ACCEPT_INVITATION_VIEW = "invitation-accept";
    
    public static final String UNCONFIRMED_AUTHOR = "unconfirmed-author";
    
    public static final String VOTE_VIEW = "vote";
    
    public static final String DUMMY_LOGIN_VIEW = "dummy-login";
    
    public static final String VETUMA_LOGIN_VIEW = "vetuma-login";
    
    public static final String SEARCH_VIEW = "search";
    
    public static final String INDEX_VIEW = "index";

    public static final String STATUS_VIEW = "status";
    
    public static final String REGISTERED_USER = "registered-user";

    public static final String TEST_DATA_GENERATION = "test-data-generation";
    
    public static final String ERROR_500_VIEW = "error/500";
    
    public static final String ERROR_404_VIEW = "error/404";

    public static final String ERROR_404_GLOBAL_VIEW = "error/404-global";

    public static final String ERROR_VETUMA_VIEW ="error/vetuma";

    public static final String API_VIEW = "api";
    
    public static final String NEWS_VIEW = "pages/news";

    public static final String HELP_VIEW = "pages/help";

    public static final String HELP_EDIT_VIEW = "pages/edit_help";

    public static final String INFO_VIEW = "pages/info";
    
    public static final String CONTENT_EDITOR_HELP_VIEW = "pages/content-editor-help";
    
    /**
     * Context relative redirect: context is prefixed to relative URLs.
     * 
     * @param targetUri
     * @return
     */
    public static String contextRelativeRedirect(String targetUri) {
        return "redirect:" + targetUri;
    }
    
}
