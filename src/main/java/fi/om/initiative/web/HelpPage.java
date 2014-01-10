package fi.om.initiative.web;

import fi.om.initiative.util.Locales;

/**
 * All info and help pages are saved to database.
 * Because we must be able to offer some links inside the service
 * and at footer, It's more efficient to hard-code these
 * links instead of downloading from database and saving
 * some kind of structure-information to them.
 * Some test that all these links are valid and not dead would be nice.
 */
public enum HelpPage {
    NEWS("tiedotteet", "aktuellt"),
    CONTACT("yhteystiedot", "kontaktuppgifter"),
    ORGANIZERS("ohje-vastuuhenkilolle", "anvisningar-for-ansvarspersoner"),
    INITIATIVE_STEPS("aloitteen-vaiheet", "initiativets-skeden"),
    VIESTINTAVIRASTO("viestintaviraston-hyvaksynta", "kommunikationsverkets-godkannande"),
    SECURITY("henkilotietojen-suoja-ja-tietoturva", "skydd-av-personuppgifter-och-dataskydd"),
    KANSALAISALOITE_FI("palvelun-tarkoitus", "syftet-med-webbtjansten");

    private String uriFi;
    private String uriSv;

    HelpPage(String uriFi, String uriSv) {
        this.uriFi = uriFi;
        this.uriSv = uriSv;
    }

    public String getUri(String locale) {
        return Locales.LOCALE_SV.toLanguageTag().equals(locale)
                ? uriSv
                : uriFi;
    }


}
