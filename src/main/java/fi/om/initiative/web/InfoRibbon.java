package fi.om.initiative.web;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import fi.om.initiative.conf.ConfigurationFileLoader;
import fi.om.initiative.util.Locales;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;

public class InfoRibbon {
    
    private static final Logger log = LoggerFactory.getLogger(InfoRibbon.class); 

    private static String infoRibbonFi = null;

    private static String infoRibbonSv = null;
    
    public static void refreshInfoRibbonTexts() {
        infoRibbonFi = getRefreshedInfoRibbonText("info-ribbon_fi.txt");
        infoRibbonSv = getRefreshedInfoRibbonText("info-ribbon_sv.txt");
        log.info("Refreshed infoRibbons");
        log.info("fi: " + infoRibbonFi);
        log.info("sv: " + infoRibbonSv);
    }
    
    private static String getRefreshedInfoRibbonText(String fileName) {
        try {
            String result = Files.toString(ConfigurationFileLoader.getFile(fileName), Charsets.UTF_8);
            result = result.trim();
            if (result.length() == 0) {
                return null;
            } else {
                return result;
            }
        } catch (IOException e) {
            log.warn("No current info file: " + fileName);
            return null;
        }
    }
    
    public static String getCachedInfoRibbonText(Locale locale) {
        if (Locales.LOCALE_SV.equals(locale)) {
            return infoRibbonSv;
        } else {
            return infoRibbonFi;
        }
    }

}
