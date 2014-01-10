package fi.om.initiative.pdf;

import com.lowagie.text.DocumentException;
import org.joda.time.LocalDate;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SupportStatementPdfGenerator {
    private final Resource pdf_fi;
    private final Resource pdf_sv;

    public SupportStatementPdfGenerator(Resource pdf_fi, Resource pdf_sv) throws IOException {
        this.pdf_fi = pdf_fi;
        this.pdf_sv = pdf_sv;
    }

    public ByteArrayOutputStream generatePdf(String initiativeName, LocalDate startDate, boolean fi) throws IOException, DocumentException {
        InputStream resourceInputStream = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Resource resource = fi ? pdf_fi : pdf_sv;
            resourceInputStream = resource.getInputStream();

            SupportStatementPdf.modifyPdf(resourceInputStream, outputStream, initiativeName, startDate);

            return outputStream;
        } finally {
            if (resourceInputStream != null) {
                resourceInputStream.close();
            }
        }
    }

}
