package fi.om.initiative.conf;

import fi.om.initiative.dao.TestHelper;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import java.io.IOException;

import static org.mockito.Mockito.mock;

@Configuration
@Import(AppConfiguration.class)
@PropertySource({"classpath:default.properties", "classpath:test.properties"})
@EnableTransactionManagement(proxyTargetClass=false)
public class IntegrationTestConfiguration {

    @Bean
    public ServletContext servletContext() {
        return mock(ServletContext.class);
    }

    @Bean
    public TestHelper testHelper() {
        return new TestHelper();
    }

    @Inject
    private Environment env;

    // Tests are using own schema. When migrations have run to test-schema, we have to grant some rights to them for test-user.
    @PostConstruct
    public void updateDatabase() throws IOException {
        Flyway flyway = new Flyway();
        flyway.setEncoding("UTF-8");
        flyway.setTable("flyway_schema");
        flyway.setLocations("testmigration");
        flyway.setSchemas(env.getProperty(PropertyNames.jdbcUser));
        flyway.setDataSource(
                env.getProperty(PropertyNames.jdbcURL),
                env.getProperty(PropertyNames.flywayUser),
                env.getProperty(PropertyNames.flywayPassword));

        flyway.setBaselineOnMigrate(true);
        flyway.migrate();
    }
    
}
