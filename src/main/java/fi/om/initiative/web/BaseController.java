package fi.om.initiative.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.om.initiative.conf.EnvironmentSettings;
import fi.om.initiative.dto.EditMode;
import fi.om.initiative.dto.InfoTextCategory;
import fi.om.initiative.dto.InitiativeConstants;
import fi.om.initiative.dto.initiative.FlowState;
import fi.om.initiative.dto.initiative.FlowStateAnalyzer;
import fi.om.initiative.dto.initiative.InitiativeState;
import fi.om.initiative.service.FooterLinkProvider;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateModelException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static fi.om.initiative.web.Views.contextRelativeRedirect;

public class BaseController {

    private static final String REQUEST_MESSAGES_KEY = "requestMessages";

    private static final String CURRENT_URI_ATTR = "currentUri";

    private static final String OM_PICIW_ID = "omPiwicId";

    static final String ALT_URI_ATTR = "altUri";

    @Resource HttpUserService userService;

    @Resource BeansWrapper freemarkerObjectWrapper;

    @Resource FlowStateAnalyzer flowStateAnalyzer;

    @Resource FooterLinkProvider footerLinkProvider;

    @Resource
    protected EnvironmentSettings environmentSettings;

    private final boolean showPiwik;

    BaseController(boolean showPiwik) {
        this.showPiwik = showPiwik;
    }

    @ModelAttribute
    public void addModelDefaults(Locale locale, HttpServletRequest request, Model model) {
        Urls urls = Urls.get(locale);
        model.addAttribute("appEnvironment", environmentSettings.appEnvironment);
        model.addAttribute("currentUser", userService.getCurrentUser(false)); // Purely informative at this point
        model.addAttribute("locale", urls.getLang());
        model.addAttribute("altLocale", urls.getAltLang());
        model.addAttribute("urls", urls);
        model.addAttribute("fieldLabelKey", FieldLabelKeyMethod.INSTANCE);
        model.addAttribute(REQUEST_MESSAGES_KEY, getRequestMessages(request));
        model.addAttribute("flowStateAnalyzer", flowStateAnalyzer);
        model.addAttribute("summaryMethod", SummaryMethod.INSTANCE);
        model.addAttribute("optimizeResources", environmentSettings.optimizeResources);
        model.addAttribute("resourcesVersion", environmentSettings.resourcesVersion);
        model.addAttribute(CURRENT_URI_ATTR, urls.getBaseUrl() + request.getRequestURI());
        model.addAttribute("infoRibbon", InfoRibbon.getCachedInfoRibbonText(locale));
        model.addAttribute("footerLinks", footerLinkProvider.getFooterLinks(locale));
        model.addAttribute("superSearchEnabled", urls.getSuperSearchUrl()!=null);
        model.addAttribute("samlEnabled", environmentSettings.isSamlEnabled());

        try {
            model.addAttribute("UrlConstants", freemarkerObjectWrapper.getStaticModels().get(Urls.class.getName()));
            model.addAttribute("InitiativeConstants", freemarkerObjectWrapper.getStaticModels().get(InitiativeConstants.class.getName()));
        } catch (TemplateModelException e) {
            throw new RuntimeException(e);
        }
        
        addEnum(InitiativeState.class, model);
        addEnum(EditMode.class, model);
        addEnum(RequestMessage.class, model);
        addEnum(RequestMessageType.class, model);
        addEnum(FlowState.class, model);
        addEnum(HelpPage.class, model);
        addEnum(InfoTextCategory.class, model);
    }
    
    static void addRequestMessage(RequestMessage requestMessage, Model model, HttpServletRequest request) {
        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        addListElement(flashMap, REQUEST_MESSAGES_KEY, requestMessage);
        if (model != null) {
            addListElement(model.asMap(), REQUEST_MESSAGES_KEY, requestMessage);
        }
    }
    
    private static <T> void addListElement(Map<? super String, ? super List<T>> map, String key, T value) {
        @SuppressWarnings("unchecked")
        List<T> list = (List<T>) map.get(key);
        if (list == null) {
            list = Lists.newArrayList();
            map.put(key, list);
        }
        list.add(value);
    }

    protected String redirectWithMessage(String targetUri, RequestMessage requestMessage, HttpServletRequest request) {
        addRequestMessage(requestMessage, null, request);
        return contextRelativeRedirect(targetUri);
    }
    
    

    @SuppressWarnings("unchecked")
    private List<RequestMessage> getRequestMessages(HttpServletRequest request) {
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
        if (flashMap != null) {
            return (List<RequestMessage>) flashMap.get(REQUEST_MESSAGES_KEY);
        } else {
            return Lists.newArrayList();
        }
    }

    private <T extends Enum<?>> void addEnum(Class<T> enumType, Model model) {
        Map<String, T> values = Maps.newHashMap();
        for (T value : enumType.getEnumConstants()) {
            values.put(value.name(), value);
        }
        model.addAttribute(enumType.getSimpleName(), values);
    }

    protected void addPiwicIdIfNotAuthenticated(Model model) {
        if (!userService.getCurrentUser(false).isAuthenticated()
                && showPiwik) {
            model.addAttribute(OM_PICIW_ID, environmentSettings.omPiwikId.orNull());
        }
    }

}
