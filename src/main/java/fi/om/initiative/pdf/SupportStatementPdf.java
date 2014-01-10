package fi.om.initiative.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import org.joda.time.LocalDate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SupportStatementPdf {
    
    // TODO: Handle both FI and SV PDFs. View-page shows both versions according to user's language
    // NOTE: Should we save the PDF somewhere or generate it on the fly or when user clicks "Generate PDF"-link.
    // Chapter 9: Integrating iText in your web applications: http://www.itextpdf.com/examples/iia.php?id=174

    public static final String INITIATIVE_NAME = "kansl";
    public static final String INITIATIVE_DAY = "pv1";
    public static final String INITIATIVE_MONTH = "pv2";
    public static final String INITIATIVE_YEAR = "pv3";
    
    /** User password. */
    public static byte[] USER = "Hello".getBytes(); // TODO
    /** Owner password. */
    public static byte[] OWNER = "World".getBytes(); // TODO

    private static void fill(AcroFields form, String name, LocalDate startDate)
            throws IOException, DocumentException {

        form.setField(INITIATIVE_NAME, name);
        form.setField(INITIATIVE_DAY, startDate.toString("dd"));
        form.setField(INITIATIVE_MONTH, startDate.toString("MM"));
        form.setField(INITIATIVE_YEAR, startDate.toString("yy"));
    }

    public static void modifyPdf(InputStream pdfTemplate, OutputStream modifiedPdf, String name, LocalDate startDate) throws IOException, DocumentException {
        // NOTE: Can we use this?
//        PdfReader.unethicalreading = true;

        PdfReader reader = new PdfReader(pdfTemplate);
        PdfStamper stamper = new PdfStamper(reader, modifiedPdf);

        fill(stamper.getAcroFields(), name, startDate);

        stamper.setFormFlattening(true);
        stamper.partialFormFlattening(INITIATIVE_NAME);
        stamper.partialFormFlattening(INITIATIVE_DAY);
        stamper.partialFormFlattening(INITIATIVE_MONTH);
        stamper.partialFormFlattening(INITIATIVE_YEAR);

        stamper.close();
        reader.close();
    }

    /**
     * TODO: PDF decryption / encryption
     * 
     * http://itextpdf.com/examples/iia.php?id=219
     * 
     * Chapter 12: Protecting your PDF:
     * http://www.itextpdf.com/book/examples.php
     * 
     */
//    public void decryptPdf(String src, String dest) throws IOException, DocumentException {
//        PdfReader reader = new PdfReader(src, OWNER);
//        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
//        stamper.close();
//        reader.close();
//    }
// 
//    public void encryptPdf(String src, String dest) throws IOException, DocumentException {
//        PdfReader reader = new PdfReader(src);
//        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
//        stamper.setEncryption(USER, OWNER,
//            PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128 | PdfWriter.DO_NOT_ENCRYPT_METADATA);
//        stamper.close();
//        reader.close();
//    }
    
//    public static PdfReader unlockPdf(PdfReader reader) { 
//        if (reader == null) { 
//                return reader; 
//        } 
//        try { 
//                Field f = reader.getClass().getDeclaredField("encrypted"); 
//                f.setAccessible(true); 
//                f.set(reader, false); 
//        } catch (Exception e) { // ignore 
//        } 
//        return reader; 
//    } 

 
}
