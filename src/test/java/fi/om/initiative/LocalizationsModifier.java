package fi.om.initiative;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * - Reads all localization files.
 * - First file is considered as master-file.
 * - All localizations that are missing from any other files but found from master files are reported.
 * - All localizations that are found from any other files but not from master file are removed.
 * - All localizations, comments and empty lines are ordered according to master file.
 */
public class LocalizationsModifier {

    private final static String resourcesDir = System.getProperty("user.dir") + "/src/main/webapp/WEB-INF/";

    // All localization files. First file is "master" file, all localization keys found in that file are
    // supposed to be found from other files. Comments, empty lines and localizations in other files will be ordered according to the master file.
    private final static String[] resourceFiles = {"messages.properties", "messages_sv.properties"};

    // Localization-missing text will not be printed if localization key begins with any of these strings
    private final static String[] exludePrefix = {"api.", "editor.", "success.editor"};

    public static void main(String[] s) throws IOException {

        List<ResourceFile> localizationFiles = readResourceFiles();

        ResourceFile masterFile = localizationFiles.get(0);

        for (int i = 1; i < localizationFiles.size(); ++i) {
            System.out.println("Handling localization file: " + resourceFiles[i]);

            ResourceFile currentLocalizationFile = localizationFiles.get(i);

            String modifiedLocalizationFileContents = getModifiedFileContent(masterFile, currentLocalizationFile);

            FileWriter fileWriter = new FileWriter(new File(resourcesDir + resourceFiles[i]), false);
            fileWriter.write(modifiedLocalizationFileContents);
            fileWriter.close();
        }


    }

    private static String getModifiedFileContent(ResourceFile masterFile, ResourceFile fileToModify) {
        StringBuilder modifiedFileContent = new StringBuilder();
        for (ResourceFileLine masterFileLine : masterFile.lines) {

            if (masterFileLine.type != LineType.LOCALIZATION) {
                modifiedFileContent
                        .append(masterFileLine.toString())
                        .append("\n");
            } else {
                ResourceFileLine otherLocalization = fileToModify.getPossibleLocalizationLine(masterFileLine.key);
                if (otherLocalization != null) {
                    modifiedFileContent
                            .append(otherLocalization.toString())
                            .append("\n");
                } else if (isNotExcludeKey(masterFileLine.key)) {
                    System.err.println("Localization missing: " + masterFileLine.key + " = " + masterFileLine.localization);
                }

            }
        }

        return modifiedFileContent.toString();
    }

    private static List<ResourceFile> readResourceFiles() throws IOException {
        List<ResourceFile> localizationFiles = new ArrayList<>();
        for (String resourceFile : resourceFiles) {
            ResourceFile file = new ResourceFile();

            BufferedReader br = new BufferedReader(new FileReader(resourcesDir + resourceFile));
            String line;
            while ((line = br.readLine()) != null) {
                file.addResourceLine(line);
            }
            br.close();

            localizationFiles.add(file);
        }
        return localizationFiles;
    }

    private static boolean isNotExcludeKey(String key) {
        for (String s : exludePrefix) {
            if (key.startsWith(s)) {
                return false;
            }
        }
        return true;
    }

    private static class ResourceFile {
        private List<ResourceFileLine> lines = new ArrayList<>();

        public void addResourceLine(String line) {
            lines.add(new ResourceFileLine(line));
        }

        public ResourceFileLine getPossibleLocalizationLine(String key) {
            for (ResourceFileLine line : lines) {
                if (line.type == LineType.LOCALIZATION && line.key.equals(key)) {
                    return line;
                }
            }
            return null;
        }
    }

    private static class ResourceFileLine {

        final public LineType type;
        final public String key;
        final public String localization;

        public ResourceFileLine(String line) {
            line = line.trim();
            if (line.length() == 0) {
                this.type = LineType.EMPTY;
                key = null;
                localization = null;
            }
            else if (line.charAt(0) == '#') {
                this.type = LineType.COMMENT;
                key = null;
                localization = line;
            }
            else {

                int indexOfEquals = line.indexOf('=');
                if (indexOfEquals == -1) {
                    throw new RuntimeException("Invalid localization line: \n" + line);
                }

                this.type = LineType.LOCALIZATION;
                key = line.substring(0, indexOfEquals).trim();
                localization = line.substring(indexOfEquals+1).trim();
            }

        }

        @Override
        public String toString() {

            switch (type) {
                case EMPTY:
                    return "";
                case COMMENT:
                    return localization;
                case LOCALIZATION:
                    return key + " = " + localization;
                default:
                    throw new RuntimeException("Invalid type: " + type);
            }
        }
    }

    private enum LineType {
        EMPTY,
        COMMENT,
        LOCALIZATION
    }
}

