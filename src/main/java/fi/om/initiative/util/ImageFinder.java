package fi.om.initiative.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageFinder {
    List<FileImageFinder.FileJson> getImages();

    FileImageFinder.FileInfo getFile(String fileName, String version) throws IOException;

    void validateAndSaveFile(MultipartFile multipartFile) throws IOException;
}
