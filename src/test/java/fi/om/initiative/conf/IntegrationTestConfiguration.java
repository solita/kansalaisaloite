package fi.om.initiative.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import fi.om.initiative.dao.TestHelper;

@Configuration
@Import(AppConfiguration.class)
@PropertySource({"classpath:default.properties", "classpath:test.properties"})
@EnableTransactionManagement(proxyTargetClass=false)
public class IntegrationTestConfiguration {

    @Bean
    public TestHelper testHelper() {
        return new TestHelper();
    }
    
}
