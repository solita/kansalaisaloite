package fi.om.initiative.conf;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.spring31.properties.EncryptablePropertiesPropertySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

import com.google.common.io.Files;

public class EncryptablePropertiesConfigurer implements BeanFactoryPostProcessor, EnvironmentAware {

    private static final Logger log = LoggerFactory.getLogger(EncryptablePropertiesConfigurer.class); 

    private static final int KEY_OBTENTION_ITERATIONS = 1000;

    private static final String ALGORITHM = "PBEWITHSHA256AND128BITAES-CBC-BC";
    
    private static final String MISSING_PASSWORD_MESSAGE = "Unable to load configuration password from ~/.initpass";

    private final Resource location;
    
    private ConfigurableEnvironment environment;
    
    public EncryptablePropertiesConfigurer(Resource location) {
        this.location = location;
    }
    
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (location.exists()) { // ignore missing configuration
            Properties properties = new Properties();
            try {
                properties.load(location.getInputStream());
            } catch (IOException e) {
                throw new ApplicationContextException("Unable to load " + location, e);
            }
    
            String password = getPassword();
            MutablePropertySources propertySources = environment.getPropertySources();
            PropertySource<?> propertySource;
            if (password == null) { // properties aren't encrypted
                propertySource = new PropertiesPropertySource(location.toString(), properties);
            } else {
                StandardPBEStringEncryptor encryptor = getConfigurationEncryptor(password);
                propertySource = new EncryptablePropertiesPropertySource("encrypted properties: " + location, properties, encryptor);
            }
            propertySources.addFirst(propertySource);
        } else {
            log.warn(location.toString() + " not found.");
        }
    }
    
    private static StandardPBEStringEncryptor getConfigurationEncryptor(String password) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setProvider(new BouncyCastleProvider());
        encryptor.setAlgorithm(ALGORITHM);
        encryptor.setPassword(password);
        encryptor.setKeyObtentionIterations(KEY_OBTENTION_ITERATIONS);
        return encryptor;
    }
    
    private static String getPassword() {
        try {
            return Files.toString(new File(System.getProperty("user.home"), ".initpass"), Charset.forName("US-ASCII"));
        } catch (IOException e) {
            log.warn(MISSING_PASSWORD_MESSAGE);
            return null;
        }
    }

    public static void main(String[] args) {
        if (args.length == 0 || args.length > 3) {
            System.err.println(
                    "Parameters: encrypt|decrypt <message>             # with password from ~/.initpass\n" +
            		"        OR  encrypt|decrypt <password> <message>  # with given password"
                );
            return;
        }
        String operation = args[0].toLowerCase();
        String password;
        String text;
        if (args.length == 2) {
            password = getPassword();
            if (password == null) {
                System.err.println(MISSING_PASSWORD_MESSAGE);
                return;
            }
            text = args[1];
        } else {
            password = args[1];
            text = args[2];
        }
        
        StandardPBEStringEncryptor encryptor = getConfigurationEncryptor(password);
        if ("encrypt".equals(operation)) {
            System.out.println(encryptor.encrypt(text));
        } else if ("decrypt".equals(operation)) {
            System.out.println(encryptor.decrypt(text));
        } else {
            System.err.println("First parameter must be either encrypt or decrypt");
        }
    }
}
