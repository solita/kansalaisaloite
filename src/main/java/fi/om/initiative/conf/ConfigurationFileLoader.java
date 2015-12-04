package fi.om.initiative.conf;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

public class ConfigurationFileLoader {
    public static File getFile(String fileName) throws FileNotFoundException {

        File jarLocation = Paths.get("").toFile();
        File configDir = new File(jarLocation.getAbsolutePath() + "/config/");

        if (!configDir.isDirectory()) {
            throw new FileNotFoundException("Config dir not found: " + configDir.getAbsolutePath());
        }

        File file = new File(configDir.getAbsolutePath() + "/" + fileName);
        if (!file.exists()) {
            throw new FileNotFoundException("Configuration file not found: " + file.getPath());
        }
        return file;
    }
}
