package fi.om.initiative.querydsl;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import com.mysema.query.sql.Configuration;
import com.mysema.query.sql.codegen.DefaultNamingStrategy;
import com.mysema.query.sql.codegen.MetaDataExporter;

import fi.om.initiative.conf.JdbcConfiguration;

public class ExportQTypes {

    public static final String NAME_PREFIX = "Q";
    public static final String TARGET_FOLDER = "src/main/java";
    public static final String PACKAGE_NAME = "fi.om.initiative.sql";

    @org.springframework.context.annotation.Configuration
    @Import(JdbcConfiguration.class)
    @PropertySource("classpath:test.properties")
    public static class StandaloneJdbcConfiguration {
    }

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(StandaloneJdbcConfiguration.class);
        DataSource dataSource = ctx.getBean(DataSource.class);
        Configuration configuration = ctx.getBean(Configuration.class);
        
        MetaDataExporter exporter = new MetaDataExporter();
        exporter.setPackageName(PACKAGE_NAME);
        exporter.setSchemaPattern("initiative");
        exporter.setInnerClassesForKeys(false);
        exporter.setNamePrefix(NAME_PREFIX);
        exporter.setNamingStrategy(new DefaultNamingStrategy());
        exporter.setTargetFolder(new File(TARGET_FOLDER));
        exporter.setConfiguration(configuration);

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            deleteOldQTypes(TARGET_FOLDER, PACKAGE_NAME);
            exporter.export(conn.getMetaData());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    }

    private static void deleteOldQTypes(final String target, final String pack)
            throws IOException {
        Path targetDir = FileSystems.getDefault().getPath(target, pack.replace(".", "/"));
        Files.walkFileTree(targetDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                if (path.getFileName().toString().startsWith(NAME_PREFIX)) {
                    System.out.println("Delete " + path + ": " + path.toFile().delete());
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
}
