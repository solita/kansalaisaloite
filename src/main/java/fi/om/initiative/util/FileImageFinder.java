package fi.om.initiative.util;

import com.google.common.collect.Lists;
import fi.om.initiative.dao.NotFoundException;
import fi.om.initiative.web.Urls;
import org.aspectj.util.FileUtil;
import org.joda.time.DateTime;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class FileImageFinder implements ImageFinder {

    private static final String[] FILE_TYPES = { "png", "jpg" };
    private static final String[] CONTENT_TYPES = { "image/png", "image/jpg", "image/jpeg" };

    private static final String FILE_PATTERN = "^[_A-Za-z0-9-]+$";
    static final String FILE_TIMESTAMP_FORMAT = "yyyy-MM-dd_HH-mm-ss";

    private String path;
    private String baseUrl;

    public FileImageFinder(String path, String baseUrl) {
        this.path = path;
        this.baseUrl = baseUrl;
    }

    @Override
    public List<FileJson> getImages() {
        ArrayList<FileJson> images = Lists.newArrayList();

        for (String fileName : FileUtil.listFiles(new File(path))) {
            if (isAcceptableFile(fileName)) {
                images.add(new FileJson(fileName, toUri(fileName)));
            }
        }
        return images;
    }

    private String toUri(String file) {
        return baseUrl+Urls.IMAGES + "/"+ file + "?version="+ new Date().getTime();
    }

    public static boolean isAcceptableFile(String fileName) {

        String[] split = fileName.split("\\.");
        if (split.length != 2)
            return false;

        String fileBodyName = split[0];
        String filePattern = split[1];

        if (!Pattern.matches(FILE_PATTERN, fileBodyName))
            return false;

        for (String fileType : FILE_TYPES) {
            if (filePattern.equals(fileType))
                return true;
        }

        return false;
    }

    @Override
    @Cacheable("infoTextImages")
    public FileInfo getFile(String fileName, String version) throws IOException {

        assertFileName(fileName);

        File file = new File(path + "/" + fileName);
        if (!file.exists()) {
            throw new NotFoundException("image", fileName);
        }
        byte[] bytes = FileUtil.readAsByteArray(file);
        return new FileInfo(Arrays.copyOf(bytes,bytes.length), file.lastModified());
    }

    private static String directory(File file) {
        return file.getParentFile().getAbsolutePath();
    }

    private static void assertFileName(String fileName) {
        if (!isAcceptableFile(fileName)) {
            throw new RuntimeException("Invalid filename");
        }
    }

    @Override
    public void validateAndSaveFile(MultipartFile multipartFile) throws IOException {
        assertFileName(multipartFile.getOriginalFilename());
        assertContentType(multipartFile.getContentType());

        File file = new File(path + "/" + multipartFile.getOriginalFilename());
        while (file.exists()) {
             file = new File(directory(file)+"/"+addTimestampToFileName(file.getName(), new DateTime().toString(FILE_TIMESTAMP_FORMAT)));
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(file, false)) {
            fileOutputStream.write(multipartFile.getBytes());
        }

    }

    public static String addTimestampToFileName(String fileName, String timestamp) {
        String[] split = fileName.split("\\.");
        assert(split.length == 2);
        return split[0]+timestamp+"."+split[1];
    }

    private static void assertContentType(String contentType) {
        for (String type : CONTENT_TYPES) {
            if (type.equals(contentType))
                return;
        }
        throw new RuntimeException("Invalid content-type:" + contentType);
    }

    public static class FileJson {
        private String file;
        private String url;

        public FileJson(String file, String url) {
            this.file = file;
            this.url = url;
        }

        public String getFile() {
            return file;
        }

        public String getUrl() {
            return url;
        }
    }

    public class FileInfo {
        public final byte[] bytes;
        public final String modifyTime;

        public FileInfo(byte[] bytes, long modifyTime) {
            this.bytes = bytes;
            this.modifyTime = new DateTime(modifyTime).toString("E, dd MMM yyyy HH:mm:ss z");
        }
    }

}
