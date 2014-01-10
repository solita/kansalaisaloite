package fi.om.initiative.service;

import com.google.common.io.Files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

public class InfoTextManager {

    public static final String NEWS_FILE = ".news";

    public String getNewsText() {
        try {
            return Files.toString(new File(NEWS_FILE), Charset.forName("UTF-8"));
        } catch (FileNotFoundException e) {
            return "";
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setNewsText(String newsText) {
        try {
            Files.write(newsText.getBytes(), new File(NEWS_FILE));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
