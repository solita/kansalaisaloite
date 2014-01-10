package fi.om.initiative.pdf;

import com.lowagie.text.DocumentException;
import org.joda.time.LocalDate;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SupportStatementPdfTest {

    public static final String SRC = System.getProperty("user.dir") + "/src/main/resources/pdf/Kannatusilmoitus_W.pdf";
    public static final String RESULT = System.getProperty("user.dir")+"/Kannatusilmoitus.pdf";

    public static final String NAME = "Tämä on kansalaisaloitteen otsikko";
    public static final LocalDate START_DATE = new LocalDate(2010, 12, 24);

    public static void main(String[] stamp)
            throws IOException, DocumentException {

        FileInputStream inputStream = new FileInputStream(SRC);
        FileOutputStream outputStream = new FileOutputStream(RESULT);

        SupportStatementPdf.modifyPdf(inputStream, outputStream, NAME, START_DATE);
    }
}
