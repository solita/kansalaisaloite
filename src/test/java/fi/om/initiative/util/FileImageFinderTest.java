package fi.om.initiative.util;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class FileImageFinderTest {

    private static final String TEST_BASEURL = "http://baseurl";
    private String appPropertiesImageDirectory;

    @Before
    public void setUp() throws Exception {
        // This is default image directory inside the war file including for example gif and png files.
        appPropertiesImageDirectory = System.getProperty("user.dir") + "/src/test/resources";
    }

    @Test
    public void get_images_returns_images_and_only_valid_image_file_names() {

        FileImageFinder imageFinder = new FileImageFinder(appPropertiesImageDirectory, TEST_BASEURL);

        List<FileImageFinder.FileJson> images = imageFinder.getImages();

        assertThat(images.size(), is(4));

        for (FileImageFinder.FileJson s : images) {
            assertThat(FileImageFinder.isAcceptableFile(s.getFile()), is(true));
            assertThat(Pattern.matches("^"+TEST_BASEURL+"(.)*\\?version=[0-9]+$", s.getUrl()), is(true));
        }
    }

    @Test
    public void invalid_file_names() {
        assertThat(FileImageFinder.isAcceptableFile("jotain.mut"), is(false));
        assertThat(FileImageFinder.isAcceptableFile("jotain.moi.png"), is(false));
        assertThat(FileImageFinder.isAcceptableFile("jota$in.png"), is(false));
        assertThat(FileImageFinder.isAcceptableFile("jota+in.png"), is(false));
    }

    @Test
    public void valid_file_names() {
        FileImageFinder imageFinder = new FileImageFinder("", TEST_BASEURL);
        assertThat(FileImageFinder.isAcceptableFile("jotain.png"), is(true));
        assertThat(FileImageFinder.isAcceptableFile("jotain_muuta.jpg"), is(true));
        assertThat(FileImageFinder.isAcceptableFile("jotain_aivan-muuta.png"), is(true));
    }

    @Test
    public void get_file_fails_if_invalid_file_name() {
        try {
            new FileImageFinder(appPropertiesImageDirectory, TEST_BASEURL).getFile("file$name.jpg", null);
            fail("Should have failed due invalid file name");
        } catch (Exception e) {
            assertThat(e.getMessage(), containsString("Invalid filename"));
        }
    }

    @Test
    // XXX: Some of the files are not really image files - maybe should be fixed or then not?
    public void get_file_succeeds_if_valid_file_name_and_contentType() throws IOException {
        FileImageFinder imageFinder = new FileImageFinder(appPropertiesImageDirectory, TEST_BASEURL);

        assertThat(imageFinder.getFile("gif-content-type.jpg", null).bytes.length, is(not(0)));
        assertThat(imageFinder.getFile("jpg-content-type.jpg", null).bytes.length, is(not(0)));
        assertThat(imageFinder.getFile("png-content-type.png", null).bytes.length, is(not(0)));
        assertThat(imageFinder.getFile("text-content-type.jpg", null).bytes.length, is(not(0)));

    }

    @Test
    public void adds_timestamp_to_fileName() {
        assertThat(FileImageFinder.addTimestampToFileName("file_one.png", "TIMESTAMP"), is("file_oneTIMESTAMP.png"));

        String timestamp = new DateTime(2010, 1, 2, 12, 15).toString(FileImageFinder.FILE_TIMESTAMP_FORMAT);
        assertThat(FileImageFinder.addTimestampToFileName("file.jpg", timestamp), is("file2010-01-02_12-15-00.jpg"));
    }

}
