package fi.om.initiative.web;

import java.io.IOException;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import fi.om.initiative.util.Locales;

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
            Resource resource = new ClassPathResource(fileName);
            String result = Files.toString(resource.getFile(), Charsets.UTF_8);
            result = result.trim();
            if (result.length() == 0) {
                return null;
            }
            else {
                return result;
            }
        } catch (IOException e) {
            log.warn("No current info file: " + fileName);
            return null;
        }
    }
    
    public static String getInfoRibbonText(Locale locale) {
        if (Locales.LOCALE_SV.equals(locale)) {
            return infoRibbonSv;
        } else {
            return infoRibbonFi;
        }
    }

}
