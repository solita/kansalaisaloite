package fi.om.initiative.web;

import fi.om.initiative.service.Role;
import fi.om.initiative.service.StatusService;
import fi.om.initiative.util.Locales;
import org.opensaml.saml2.metadata.provider.AbstractReloadingMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static fi.om.initiative.web.Urls.STATUS;
import static fi.om.initiative.web.Views.STATUS_VIEW;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class StatusPageController extends BaseController {

    @Resource
    StatusService statusService;

    @Resource
    AbstractReloadingMetadataProvider idpMetadataProvider;

    public StatusPageController() {
        super(false);
    }

    @RequestMapping(value=STATUS, method=GET)
    public String statusGet(Model model,
                            @RequestParam(value="ribbon", required=false) String ribbon,
                            HttpServletRequest request) throws URISyntaxException, KeyManagementException, NoSuchAlgorithmException, MetadataProviderException {

        model.addAttribute("applicationInfoRows", statusService.getApplicationInfo());
        model.addAttribute("schemaVersionInfoRows", statusService.getSchemaVersionInfo());
        model.addAttribute("configurationInfoRows", statusService.getConfigurationInfo());
        model.addAttribute("configurationTestInfoRows", statusService.getConfigurationTestInfo());
        model.addAttribute("systemInfoRows", statusService.getSystemInfo());
        model.addAttribute("hardCodedUris", statusService.getInvalidHelpUris());

        if ("refresh".equals(ribbon)) {
            InfoRibbon.refreshInfoRibbonTexts();
            model.addAttribute("infoRibbon", InfoRibbon.getCachedInfoRibbonText(Locales.LOCALE_FI));
            idpMetadataProvider.refresh();
        }

        return STATUS_VIEW;
    }
}
