package fi.om.initiative.conf;

import fi.om.initiative.dao.TestHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.ServletContext;

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
    
}
