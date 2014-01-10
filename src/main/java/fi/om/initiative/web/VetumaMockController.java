package fi.om.initiative.web;

import fi.om.initiative.dto.vetuma.VetumaLoginResponse;
import fi.om.initiative.service.EncryptionService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.text.MessageFormat;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@Profile("vetumamock")
public class VetumaMockController extends BaseController {

    public static final String TVJ_DATA = "%3C%3Fxml+version%3D%221.0%22+encoding%3D%22ISO-8859-1%22+standalone%3D%22yes%22%3F%3E%3Cns2%3AVTJHenkiloVastaussanoma+versio%3D%221.0%22+sanomatunnus%3D%22PERUSJHHS2%22+tietojenPoimintaaika%3D%2220130327165011%22+xmlns%3Ans2%3D%22http%3A%2F%2Fxml.vrk.fi%2Fschema%2Fvtjkysely%22+xmlns%3D%22http%3A%2F%2Ftempuri.org%2F%22%3E%3Cns2%3AAsiakasinfo%3E%3Cns2%3AInfoS%3E27.03.2013+16%3A50%3C%2Fns2%3AInfoS%3E%3Cns2%3AInfoR%3E27.03.2013+16%3A50%3C%2Fns2%3AInfoR%3E%3Cns2%3AInfoE%3E27.03.2013+16%3A50%3C%2Fns2%3AInfoE%3E%3C%2Fns2%3AAsiakasinfo%3E%3Cns2%3APaluukoodi+koodi%3D%220000%22%3EHaku+onnistui%3C%2Fns2%3APaluukoodi%3E%3Cns2%3AHakuperusteet%3E%3Cns2%3AHenkilotunnus+hakuperusteTekstiE%3D%22Found%22+hakuperusteTekstiR%3D%22Hittades%22+hakuperusteTekstiS%3D%22L%F6ytyi%22+hakuperustePaluukoodi%3D%221%22%3E{0}%3C%2Fns2%3AHenkilotunnus%3E%3Cns2%3ASahkoinenAsiointitunnus+hakuperusteTekstiE%3D%22Not+used%22+hakuperusteTekstiR%3D%22Beteckningen+har+inte+anv%E4ndat%22+hakuperusteTekstiS%3D%22Tunnushakuperustetta+ei+ole+kaytetty%22+hakuperustePaluukoodi%3D%224%22%3E%3C%2Fns2%3ASahkoinenAsiointitunnus%3E%3C%2Fns2%3AHakuperusteet%3E%3Cns2%3AHenkilo%3E%3Cns2%3AHenkilotunnus+voimassaolokoodi%3D%221%22%3E010190-0001%3C%2Fns2%3AHenkilotunnus%3E%3Cns2%3ANykyinenSukunimi%3E%3Cns2%3ASukunimi%3E{1}%3C%2Fns2%3ASukunimi%3E%3C%2Fns2%3ANykyinenSukunimi%3E%3Cns2%3ANykyisetEtunimet%3E%3Cns2%3AEtunimet%3E{2}%3C%2Fns2%3AEtunimet%3E%3C%2Fns2%3ANykyisetEtunimet%3E%3Cns2%3AVakinainenKotimainenLahiosoite%3E%3Cns2%3ALahiosoiteS%3EOsoite+3+B+1%3C%2Fns2%3ALahiosoiteS%3E%3Cns2%3ALahiosoiteR%3EBert%E5kerst%E5et+3+B+23%3C%2Fns2%3ALahiosoiteR%3E%3Cns2%3APostinumero%3E00001%3C%2Fns2%3APostinumero%3E%3Cns2%3APostitoimipaikkaS%3EHELSINKI%3C%2Fns2%3APostitoimipaikkaS%3E%3Cns2%3APostitoimipaikkaR%3EHELSINGFORS%3C%2Fns2%3APostitoimipaikkaR%3E%3Cns2%3AAsuminenAlkupvm%3E20120220%3C%2Fns2%3AAsuminenAlkupvm%3E%3Cns2%3AAsuminenLoppupvm%3E%3C%2Fns2%3AAsuminenLoppupvm%3E%3C%2Fns2%3AVakinainenKotimainenLahiosoite%3E%3Cns2%3AVakinainenUlkomainenLahiosoite%3E%3Cns2%3AUlkomainenLahiosoite%3E%3C%2Fns2%3AUlkomainenLahiosoite%3E%3Cns2%3AUlkomainenPaikkakuntaJaValtioS%3E%3C%2Fns2%3AUlkomainenPaikkakuntaJaValtioS%3E%3Cns2%3AUlkomainenPaikkakuntaJaValtioR%3E%3C%2Fns2%3AUlkomainenPaikkakuntaJaValtioR%3E%3Cns2%3AUlkomainenPaikkakuntaJaValtioSelvakielinen%3E%3C%2Fns2%3AUlkomainenPaikkakuntaJaValtioSelvakielinen%3E%3Cns2%3AValtiokoodi3%3E%3C%2Fns2%3AValtiokoodi3%3E%3Cns2%3AAsuminenAlkupvm%3E%3C%2Fns2%3AAsuminenAlkupvm%3E%3Cns2%3AAsuminenLoppupvm%3E%3C%2Fns2%3AAsuminenLoppupvm%3E%3C%2Fns2%3AVakinainenUlkomainenLahiosoite%3E%3Cns2%3AKotikunta%3E%3Cns2%3AKuntanumero%3E091%3C%2Fns2%3AKuntanumero%3E%3Cns2%3AKuntaS%3E{3}%3C%2Fns2%3AKuntaS%3E%3Cns2%3AKuntaR%3E{4}%3C%2Fns2%3AKuntaR%3E%3Cns2%3AKuntasuhdeAlkupvm%3E20120220%3C%2Fns2%3AKuntasuhdeAlkupvm%3E%3C%2Fns2%3AKotikunta%3E%3Cns2%3AKuolintiedot%3E%3Cns2%3AKuolinpvm%3E%3C%2Fns2%3AKuolinpvm%3E%3C%2Fns2%3AKuolintiedot%3E%3Cns2%3AAidinkieli%3E%3Cns2%3AKielikoodi%3Efi%3C%2Fns2%3AKielikoodi%3E%3Cns2%3AKieliS%3Esuomi%3C%2Fns2%3AKieliS%3E%3Cns2%3AKieliR%3Efinska%3C%2Fns2%3AKieliR%3E%3Cns2%3AKieliSelvakielinen%3E%3C%2Fns2%3AKieliSelvakielinen%3E%3C%2Fns2%3AAidinkieli%3E%3Cns2%3ASuomenKansalaisuusTietokoodi%3E{5}%3C%2Fns2%3ASuomenKansalaisuusTietokoodi%3E%3C%2Fns2%3AHenkilo%3E%3C%2Fns2%3AVTJHenkiloVastaussanoma%3E";

    @Resource
    private EncryptionService encryptionService;

    public VetumaMockController(boolean optimizeResources, String resourcesVersion) {
        super(optimizeResources, resourcesVersion);
    }

    @RequestMapping(value="vetumamock", method=POST)
    public String vetumaMockPage(VetumaLoginResponse vetumaLoginResponse, Model model, HttpServletRequest request, HttpServletResponse response) {

        //vetumaLoginResponse.setSTATUS(VetumaResponse.Status.SUCCESSFUL);

        model.addAttribute("vetumaRequest", vetumaLoginResponse);
        // model.addAttribute("vetumaMac", encryptionService.vetumaMAC(vetumaLoginResponse.toMACString()));
        return "vetumamock";

    }

    @RequestMapping(value = "vetumamockreturn", method = POST)
    public String vetumaMockReturnPage(VetumaLoginResponse vetumaLoginResponse, Model model, HttpServletRequest request, HttpServletResponse response) {

        String lastName = request.getParameter("last_name");
        String firstName = request.getParameter("first_name");
        String municipality_fi = request.getParameter("municipality_fi");
        String municipality_sv = request.getParameter("municipality_sv");
        String finnish = request.getParameter("fi");

        String injectedVtjData = MessageFormat.format(TVJ_DATA, vetumaLoginResponse.getSsn(), lastName, firstName, municipality_fi, municipality_sv, finnish);

        vetumaLoginResponse.setVTJDATA(injectedVtjData);

        model.addAttribute("vetumaRequest", vetumaLoginResponse);
        model.addAttribute("vetumaRequestMAC", encryptionService.vetumaMAC(vetumaLoginResponse.toMACString()));
        return "vetumamockreturn";

    }

}
