package fi.om.initiative.conf;

import java.io.File;

import javax.inject.Inject;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import fi.om.initiative.dao.TestHelper;
import fi.om.initiative.service.EncryptionService;


@Configuration
@Import(JdbcConfiguration.class)
@PropertySource({"classpath:default.properties", "classpath:test.properties"})
@EnableTransactionManagement(proxyTargetClass=true)
public class WebTestConfiguration {
    
    @Inject Environment env;

    @Bean
    public TestHelper testHelper() {
        return new TestHelper();
    }

    @Bean
    public EncryptionService encryptionService() {
        // return new EncryptionService("registeredUserSecret", "vetumaSharedSecret");
        // NOTE: in WebTests these parameters must much parameters used in AppConfiguration with TEST profile 
        return new EncryptionService(
                env.getRequiredProperty(PropertyNames.registeredUserSecret),
                env.getProperty(PropertyNames.vetumaSharedSecret)
            );
    }
    
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        File file = new File(System.getProperty("user.dir"), "src/main/webapp/WEB-INF/messages");
        messageSource.setBasename(file.toURI().toString());
        return messageSource;
    }
    
}
