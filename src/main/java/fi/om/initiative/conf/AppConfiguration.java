package fi.om.initiative.conf;


import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import fi.om.initiative.conf.AppConfiguration.AppDevConfiguration;
import fi.om.initiative.conf.AppConfiguration.ProdPropertiesConfiguration;
import fi.om.initiative.conf.AppConfiguration.TestPropertiesConfigurer;
import fi.om.initiative.dao.*;
import fi.om.initiative.dto.InitiativeSettings;
import fi.om.initiative.dto.initiative.FlowStateAnalyzer;
import fi.om.initiative.pdf.SupportStatementPdfGenerator;
import fi.om.initiative.service.*;
import fi.om.initiative.util.FileImageFinder;
import fi.om.initiative.util.ImageFinder;
import fi.om.initiative.util.TaskExecutorAspect;
import fi.om.initiative.validation.LocalValidatorFactoryBeanFix;
import fi.om.initiative.web.*;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.utility.XmlEscape;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.SessionCookieConfig;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableTransactionManagement(proxyTargetClass=false)
@EnableAspectJAutoProxy(proxyTargetClass=false)
@Import({ProdPropertiesConfiguration.class, TestPropertiesConfigurer.class, JdbcConfiguration.class, AppDevConfiguration.class})
@EnableCaching
@EnableScheduling
public class AppConfiguration {

    private static Logger logger = LoggerFactory.getLogger(AppConfiguration.class);

    @Inject Environment env;
    
    @Resource JdbcConfiguration jdbcConfiguration; 

    //default values for test properties:
    private static final boolean TEST_EMAIL_CONSOLE_OUTPUT_DEFAULT = false;
    private static final int TEST_MESSAGE_SOURCE_CACHE_SECONDS_DEFAULT = -1;
    private static final boolean TEST_FREEMARKER_SHOW_ERRORS_ON_PAGE_DEFAULT = false;
    
    private PeriodFormatter periodFormatter = ISOPeriodFormat.standard();

    @Inject ServletContext servletContext;

    /**
     * PRODUCTION PROPERTIES CONFIGURATION: encrypted app.properties
     */
    @Configuration
    @Profile({"prod", "dev", "vetumamock"})
    @PropertySource({"classpath:default.properties"})
    public static class ProdPropertiesConfiguration {

        @Bean
        public static EncryptablePropertiesConfigurer propertyProcessor() {
            File appProperties = new File("config/app.properties");
            if (!appProperties.exists()) {
                logger.warn("config/app.properties not found: \n USING DEFAULT PROPERTIES!");
            }

            return new EncryptablePropertiesConfigurer(new FileSystemResource(appProperties));
        }
        
    }

    /**
     * TEST PROPERTIES CONFIGURATION: test.properties
     */
    @Configuration
    @Profile({"test"})
    @PropertySource({"classpath:default.properties", "classpath:test.properties"})
    // NOTE: default.properties has to be here, in AppConfiguration level default.properties would override test.properties
    public static class TestPropertiesConfigurer {

    }

    /**
     * TEST PROPERTIES CONFIGURATION: test.properties
     */
    @Configuration
    @Profile({"vetumamock"})
    @PropertySource({"classpath:default.properties", "classpath:vetumamock.properties"})
    // NOTE: default.properties has to be here, in AppConfiguration level default.properties would override test.properties
    public static class VetumaMockPropertiesController {

    }
    
    @Configuration
    @Profile({"dev", "test", "vetumamock"})
    public static class AppDevConfiguration {

        @Bean
        public TestDataService testDataService() {
            return new TestDataServiceImpl();
        }

    }
    
    
    /*
     * BEANS
     */
    
    @Bean
    public InitiativeDao initiativeDao() {
        return new InitiativeDaoImpl(queryFactory());
    }

    @Bean
    public InfoTextDao infoTextDao() {
        return new InfoTextDaoImpl();
    }
    
    @Bean
    public SupportVoteDao supportVoteDao() {
        return new SupportVoteDaoImpl();
    }

    @Bean
    public ReviewHistoryDao reviewHistoryDao() {return new ReviewHistoryDaoImpl();}

    @Bean
    public FollowInitiativeDao followInitiativeDao() {return new FollowInitiativeDaoImpl();}

    private PostgresQueryFactory queryFactory() {
        return jdbcConfiguration.queryFactory();
    }
    
    @Bean
    public InitiativeService initiativesService() {
        return new InitiativeServiceImpl();
    }
    
    @Bean
    public SupportVoteService supportVoteService() {
        return new SupportVoteServiceImpl();
    }
    
    @Bean
    public HttpUserServiceImpl userService() {
        return new HttpUserServiceImpl(userDao(), encryptionService(), disableSecureCookie());
    }

    @Bean
    public InfoTextService infoTextService(InfoTextDao infoTextDao, UserService userService) {
        return new InfoTextService(infoTextDao, userService);
    }
    
    @Bean
    public StatusService statusService() throws NoSuchAlgorithmException, KeyManagementException {
        String testEmailSendTo = env.getProperty(PropertyNames.testEmailSendTo);
        boolean testEmailConsoleOutput = env.getProperty(PropertyNames.testEmailConsoleOutput, Boolean.class, TEST_EMAIL_CONSOLE_OUTPUT_DEFAULT);
        int messageSourceCacheSeconds = env.getProperty(PropertyNames.testMessageSourceCacheSeconds, Integer.class, TEST_MESSAGE_SOURCE_CACHE_SECONDS_DEFAULT);
        boolean testFreemarkerShowErrorsOnPage = env.getProperty(PropertyNames.testFreemarkerShowErrorsOnPage, Boolean.class, TEST_FREEMARKER_SHOW_ERRORS_ON_PAGE_DEFAULT);

        return new StatusServiceImpl(testEmailSendTo,
                testEmailConsoleOutput, messageSourceCacheSeconds, testFreemarkerShowErrorsOnPage,
                    WebConfiguration.optimizeResources(env),
                WebConfiguration.resourcesVersion(env),
                WebConfiguration.omPiwicId(env),
                WebConfiguration.appVersion(env),
                WebConfiguration.commitHash(env),
                WebConfiguration.appEnvironment(env),
                env.getActiveProfiles()
                );
    }
    
    @Bean
    public UserDao userDao() {
        return new UserDaoImpl(queryFactory());
    }

    @Bean
    public InfoTextManager infoTextManager() {
        return new InfoTextManager();
    }
    
    @Bean
    public EncryptionService encryptionService() {
        return new EncryptionService(
                env.getRequiredProperty(PropertyNames.registeredUserSecret),
                env.getProperty(PropertyNames.vetumaSharedSecret)
            );
    }

    @Bean
    public FollowService followService(){
        return new FollowService();
    }

    @Bean
    public XmlEscape fmXmlEscape() {
        return new XmlEscape();
    }
    
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("WEB-INF/messages");
        messageSource.setCacheSeconds(env.getProperty(PropertyNames.testMessageSourceCacheSeconds, Integer.class, TEST_MESSAGE_SOURCE_CACHE_SECONDS_DEFAULT));
        return messageSource;
    }
    
    @Bean 
    public FreeMarkerConfigurer freemarkerConfig() {
        FreeMarkerConfigurer config = new FreeMarkerConfigurer();
        config.setDefaultEncoding("UTF-8");
        String[] paths = {"/WEB-INF/freemarker/", "classpath:/freemarker/"}; 
        config.setTemplateLoaderPaths(paths);
        
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("xml_escape", fmXmlEscape());

        config.setFreemarkerVariables(variables);
        return config;
    }
    
    @Bean
    public BeansWrapper freemarkerObjectWrapper(FreeMarkerConfigurer freeMarkerConfigurer) {
        boolean testFreemarkerShowErrorsOnPage = env.getProperty(PropertyNames.testFreemarkerShowErrorsOnPage, Boolean.class, TEST_FREEMARKER_SHOW_ERRORS_ON_PAGE_DEFAULT);
        if (!testFreemarkerShowErrorsOnPage) {
            freeMarkerConfigurer.getConfiguration().setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        } 
        return (BeansWrapper) freeMarkerConfigurer.getConfiguration().getObjectWrapper();
    }

    @Bean
    public JavaMailSender javaMailSender() {
        String smtpServer = env.getRequiredProperty(PropertyNames.emailSmtpServer);
        Integer smtpServerPort = env.getProperty(PropertyNames.emailSmtpServerPort, Integer.class, null);
        
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(smtpServer);
        if (smtpServerPort != null) { // otherwise use default port
            sender.setPort(smtpServerPort);
        }
        return sender;
    }
    
    @Bean 
    public InitiativeSettings initiativeSettings() {
        return new InitiativeSettings(
                env.getRequiredProperty(PropertyNames.invitationExpirationDays, Integer.class), 
                env.getRequiredProperty(PropertyNames.minSupportCountForSearch, Integer.class),
                env.getRequiredProperty(PropertyNames.requiredVoteCount, Integer.class), 
                getRequiredPeriod(PropertyNames.requiredMinSupportCountDuration), 
                getRequiredPeriod(PropertyNames.votingDuration), 
                getRequiredPeriod(PropertyNames.sendToVrkDuration), 
                getRequiredPeriod(PropertyNames.sendToParliamentDuration),
                getRequiredPeriod(PropertyNames.votesRemovalDuration),
                getRequiredPeriod(PropertyNames.omSearchBeforeVotesRemovalDuration)
            );
    }

    @Bean
    public HashCreator hashCreator() {
        return new HashCreator(env.getProperty(PropertyNames.saltForHashing));
    }
    
    @Bean
    public FlowStateAnalyzer flowStateAnalyzer() {
        return new FlowStateAnalyzer(initiativeSettings());
    }
    
    private Period getRequiredPeriod(String key) {
        return periodFormatter.parsePeriod(env.getRequiredProperty(key));
    }
    
    @Bean
    public EmailService emailService(FreeMarkerConfigurer freeMarkerConfigurer) {
        Urls.initUrls(env.getRequiredProperty(PropertyNames.baseURL)); // this could be moved to a right place!
        
        String baseURL = env.getRequiredProperty(PropertyNames.baseURL);
        String defaultReplyTo = env.getRequiredProperty(PropertyNames.emailDefaultReplyTo);
        String sendToOM = env.getRequiredProperty(PropertyNames.emailSendToOM);
        String sendToVRK = env.getRequiredProperty(PropertyNames.emailSendToVRK);
        int invitationExpirationDays = env.getRequiredProperty(PropertyNames.invitationExpirationDays, Integer.class);

        String testSendTo = env.getProperty(PropertyNames.testEmailSendTo);
        boolean testConsoleOutput = env.getProperty(PropertyNames.testEmailConsoleOutput, Boolean.class, TEST_EMAIL_CONSOLE_OUTPUT_DEFAULT);
        return new EmailServiceImpl(freeMarkerConfigurer, messageSource(), javaMailSender(), baseURL, defaultReplyTo, sendToOM, sendToVRK, invitationExpirationDays, testSendTo, testConsoleOutput);
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();
    }

    @Bean
    public EhCacheManagerFactoryBean ehcache() {
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        ehCacheManagerFactoryBean.setShared(true);
        return ehCacheManagerFactoryBean;
    }

    @Bean
    public CacheManager cacheManager(EhCacheManagerFactoryBean ehCacheManagerFactoryBean) {
        EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager();
        ehCacheCacheManager.setCacheManager(ehCacheManagerFactoryBean.getObject());
        return ehCacheCacheManager;
    }

    @Bean
    public LocalValidatorFactoryBeanFix validator() {
        return new LocalValidatorFactoryBeanFix();
    }
    
    @Bean
    public SQLExceptionTranslator sqlExceptionTranslator() {
        return new SQLErrorCodeSQLExceptionTranslator(jdbcConfiguration.dataSource());
    }
    
    @Bean
    public SQLExceptionTranslatorAspect sqlExceptionTranslatorAspect() {
        return new SQLExceptionTranslatorAspect(sqlExceptionTranslator());
    }
    
    @Bean 
    public TaskExecutorAspect taskExecutorAspect() {
        return new TaskExecutorAspect();
    }
    
    @Bean
    public ExecutorService executorService() {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run(){
                executorService.shutdown();
            }
         });
        return executorService;
    }
    
    @Bean
    public SecurityFilter securityFilter() {
        return new SecurityFilter();
    }
    
    @Bean
    public ErrorFilter errorFilter() {
        return new ErrorFilter(env.getRequiredProperty(PropertyNames.errorFeedbackEmail));
    }

    @Bean
    public CacheHeaderFilter resourceCacheFilter() {
        return new CacheHeaderFilter(WebConfiguration.optimizeResources(env));
    }

    @Bean
    public CacheHeaderFilter noCacheFilter() {
        return new CacheHeaderFilter(WebConfiguration.optimizeResources(env), 0);
    }

    @Bean
    public JobExecutor jobExecutor() {
        return new JobExecutor();
    }

    @Bean
    public CacheHeaderFilter apiFilter() {
        return new CacheHeaderFilter(WebConfiguration.optimizeResources(env), 0);
    }

    @Bean
    public ImageFinder imageFinder() {
        return new FileImageFinder(env.getRequiredProperty(PropertyNames.omImageDirection), env.getRequiredProperty(PropertyNames.baseURL));
    }

    @Bean
    public SupportStatementPdfGenerator supportStatementPdfGenerator(ResourceLoader resourceLoader) throws IOException {
        org.springframework.core.io.Resource pdf_fi = resourceLoader.getResource("classpath:pdf/Kannatusilmoitus_W.pdf");
        org.springframework.core.io.Resource pdf_sv = resourceLoader.getResource("classpath:pdf/Stodforklaring_sv_W.pdf");

        assert (pdf_fi.exists());
        assert (pdf_sv.exists());
        return new SupportStatementPdfGenerator(
                pdf_fi,
                pdf_sv
                );
    }

    @PostConstruct
    public void setSecureCookie() {
        SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();

        // servletContext is mocked in integrationTests so it will return null.
        if (sessionCookieConfig != null) {
            sessionCookieConfig.setSecure(!disableSecureCookie());
        }

    }

    @PostConstruct
    public void refreshInfoRibbon() {
        InfoRibbon.refreshInfoRibbonTexts();
    }

    private boolean disableSecureCookie() {
        return Sets.newHashSet(env.getActiveProfiles()).contains("disableSecureCookie");
    }
}
