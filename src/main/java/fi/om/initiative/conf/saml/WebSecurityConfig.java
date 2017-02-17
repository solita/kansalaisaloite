package fi.om.initiative.conf.saml;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import fi.om.initiative.conf.FileTemplateMetadataProvider;
import fi.om.initiative.web.Urls;
import org.apache.velocity.app.VelocityEngine;
import org.opensaml.common.SAMLException;
import org.opensaml.saml2.common.Extensions;
import org.opensaml.saml2.common.impl.ExtensionsBuilder;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.saml2.metadata.provider.ResourceBackedMetadataProvider;
import org.opensaml.util.resource.FilesystemResource;
import org.opensaml.util.resource.ResourceException;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.impl.XSAnyBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.saml.*;
import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.security.saml.context.SAMLContextProviderLB;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.key.EmptyKeyManager;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.log.SAMLDefaultLogger;
import org.springframework.security.saml.metadata.*;
import org.springframework.security.saml.parser.ParserPoolHolder;
import org.springframework.security.saml.processor.HTTPPostBinding;
import org.springframework.security.saml.processor.HTTPRedirectDeflateBinding;
import org.springframework.security.saml.processor.SAMLBinding;
import org.springframework.security.saml.processor.SAMLProcessorImpl;
import org.springframework.security.saml.util.VelocityFactory;
import org.springframework.security.saml.websso.*;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.inject.Inject;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.util.*;

/**
 * "Forked" from https://github.com/vdenotaris/spring-boot-security-saml-sample
 * Thanks Vincenzo !
 */
@Configuration
@EnableWebSecurity
@ImportResource("classpath:samlConfig.xml")
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Inject
    Environment environment;

    // Initialization of the velocity engine
    @Bean
    public VelocityEngine velocityEngine() {
        return VelocityFactory.getEngine();
    }

    // XML parser pool needed for OpenSAML parsing
    @Bean(initMethod = "initialize")
    public StaticBasicParserPool parserPool() {
        StaticBasicParserPool staticBasicParserPool = new StaticBasicParserPool();

        // https://github.com/spring-projects/spring-security-saml/commit/925c8925fa0d0645d7b177b6e65cfb920fc6782f
        // org.opensaml.xml.encryption.Decrypter.buildParserPool()
        // Even though most of these are default, let's just define them so
        // there's a little smaller change of screwing things up if any new features are introduced later.

        Map<String, Boolean> features = new HashMap<>();

        staticBasicParserPool.setNamespaceAware(true);
        features.put("http://apache.org/xml/features/dom/defer-node-expansion", Boolean.FALSE);
        staticBasicParserPool.setExpandEntityReferences(false);
        features.put(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        features.put("http://apache.org/xml/features/disallow-doctype-decl", true);

        staticBasicParserPool.setBuilderFeatures(features);
        return staticBasicParserPool;
    }

    @Bean(name = "parserPoolHolder")
    public ParserPoolHolder parserPoolHolder() {
        return new ParserPoolHolder();
    }

    // SAML Authentication Provider responsible for validating of received SAML
    // messages
    @Bean
    public SAMLAuthenticationProvider samlAuthenticationProvider() {
        SAMLAuthenticationProvider samlAuthenticationProvider = new SAMLAuthenticationProvider();
        samlAuthenticationProvider.setUserDetails(new SamlUserLoader());
        samlAuthenticationProvider.setForcePrincipalAsString(false);
        return samlAuthenticationProvider;
    }

    // Provider of default SAML Context
    @Bean
    public SAMLContextProviderImpl contextProvider() throws MalformedURLException {
        SAMLContextProviderLB samlContextProviderLB = new SAMLContextProviderLB();

        // This is here because apparently spring saml expects ../saml/SSO request to use http if server is answering from http
        URL url = new URL(environment.getProperty("app.baseURL"));

        samlContextProviderLB.setScheme(url.getProtocol());
        samlContextProviderLB.setServerName(url.getHost());
        samlContextProviderLB.setContextPath(url.getPath());
        return samlContextProviderLB;
    }

    // Initialization of OpenSAML library
    @Bean
    public static SAMLBootstrap sAMLBootstrap() {
        return new SAMLBootstrap();
    }

    // Logger for SAML messages and events
    @Bean
    public SAMLDefaultLogger samlLogger() {
        return new SAMLDefaultLogger();
    }

    // SAML 2.0 WebSSO Assertion Consumer
    @Bean
    public WebSSOProfileConsumer webSSOprofileConsumer() {
        return new WebSSOProfileConsumerImpl();
    }

    // SAML 2.0 Holder-of-Key WebSSO Assertion Consumer
    @Bean
    public WebSSOProfileConsumerHoKImpl hokWebSSOprofileConsumer() {
        return new WebSSOProfileConsumerHoKImpl();
    }

    // SAML 2.0 Web SSO profile
    @Bean
    public WebSSOProfile webSSOprofile() {
        return new WebSSOProfileImpl() {

            @Override
            protected AuthnRequest getAuthnRequest(SAMLMessageContext context, WebSSOProfileOptions options, AssertionConsumerService assertionConsumer, SingleSignOnService bindingService) throws SAMLException, MetadataProviderException {
                AuthnRequest authnRequest = super.getAuthnRequest(context, options, assertionConsumer, bindingService);
                authnRequest.setExtensions(buildExtensions());
                return authnRequest;
            }

            /**
             * Language extension to AuthnRequest:
             *
             *  <samlp:Extensions>
                    <vetuma xmlns="urn:vetuma:SAML:2.0:extensions">
                        <LG>[fi|sv]</LG>
                    </vetuma>
                </samlp:Extensions>
             */
            private Extensions buildExtensions() {
                Extensions extensions = new ExtensionsBuilder()
                        .buildObject("urn:oasis:names:tc:SAML:2.0:protocol", "Extensions", "saml2p");
                XSAny vetuma = new XSAnyBuilder().buildObject(new QName("urn:vetuma:SAML:2.0:extensions", "vetuma"));
                XSAny language = new XSAnyBuilder().buildObject(new QName("urn:vetuma:SAML:2.0:extensions", "LG"));

                String idpLanguageFromTarget = TargetStoringFilter.getRequestParamTarget(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest())
                        .map(t -> t.startsWith(Urls.FRONT_SV) ? "sv" : "fi")
                        .orElse("fi");

                language.setTextContent(idpLanguageFromTarget);
                extensions.getUnknownXMLObjects().add(vetuma);
                vetuma.getUnknownXMLObjects().add(language);
                return extensions;
            }
        };
    }



    // SAML 2.0 Holder-of-Key Web SSO profile
    @Bean
    public WebSSOProfileConsumerHoKImpl hokWebSSOProfile() {
        return new WebSSOProfileConsumerHoKImpl();
    }

    // SAML 2.0 ECP profile
    @Bean
    public WebSSOProfileECPImpl ecpprofile() {
        return new WebSSOProfileECPImpl();
    }

    @Bean
    public SingleLogoutProfile logoutprofile() {
        return new SingleLogoutProfileImpl();
    }

    // Central storage of cryptographic keys
    @Bean
    public KeyManager keyManager() {

        if (!Strings.isNullOrEmpty(environment.getProperty("keystore.location"))) {
            try {
                Resource storeFile = new FileSystemResourceLoader().getResource(environment.getProperty("keystore.location"));

                String keystoreKey = environment.getProperty("keystore.key");
                String storePass = environment.getProperty("keystore.password");

                Map<String, String> passwords = new HashMap<>();

                passwords.put(keystoreKey, environment.getProperty("keystore.key.password"));

                KeyStore jceks = KeyStore.getInstance("JCEKS");

                jceks.load(storeFile.getInputStream(), storePass.toCharArray());
                return new JKSKeyManager(storeFile, storePass, passwords, keystoreKey);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return new EmptyKeyManager();
        }

    }

    @Bean
    public WebSSOProfileOptions defaultWebSSOProfileOptions() {
        WebSSOProfileOptions webSSOProfileOptions = new WebSSOProfileOptions();
        webSSOProfileOptions.setIncludeScoping(false);
        return webSSOProfileOptions;
    }

    // Entry point to initialize authentication, default values taken from
    // properties file
    @Bean
    public SAMLEntryPoint samlEntryPoint() {
        SAMLEntryPoint samlEntryPoint = new SAMLEntryPoint();
        samlEntryPoint.setDefaultProfileOptions(defaultWebSSOProfileOptions());
        return samlEntryPoint;
    }

    // Setup advanced info about metadata
    @Bean
    public ExtendedMetadata extendedMetadata() {
        ExtendedMetadata extendedMetadata = new ExtendedMetadata();
        extendedMetadata.setIdpDiscoveryEnabled(false);
        extendedMetadata.setSignMetadata(true);
        extendedMetadata.setRequireLogoutRequestSigned(true);
        extendedMetadata.setRequireLogoutResponseSigned(true);

        return extendedMetadata;
    }

    @Bean
    public ExtendedMetadataDelegate idpMetaDataDelegate() throws MetadataProviderException, ResourceException {

        ResourceBackedMetadataProvider metadataProvider = idpMetadataProvider();

        ExtendedMetadataDelegate extendedMetadataDelegate =
                new ExtendedMetadataDelegate(metadataProvider, extendedMetadata());
        extendedMetadataDelegate.setMetadataTrustCheck(false);
        extendedMetadataDelegate.setMetadataRequireSignature(false);

        return extendedMetadataDelegate;
    }

    @Bean
    public ResourceBackedMetadataProvider idpMetadataProvider() throws MetadataProviderException, ResourceException {
        ResourceBackedMetadataProvider metadataProvider = new ResourceBackedMetadataProvider(new Timer(), new FilesystemResource(environment.getProperty("saml.idp.metadata.location")));
        metadataProvider.setFailFastInitialization(false);
        metadataProvider.setParserPool(parserPool());
        return metadataProvider;
    }

    // IDP Metadata configuration - paths to metadata of IDPs in circle of trust
    // is here
    // Do no forget to call iniitalize method on providers
    @Bean
    public CachingMetadataManager metadata() throws Exception {
        List<MetadataProvider> providers = new ArrayList<MetadataProvider>();
        providers.add(idpMetaDataDelegate());


        ExtendedMetadata defaultMetadata = new ExtendedMetadata();
        defaultMetadata.setLocal(true);
//        defaultMetadata.setSignMetadata(true);

        FileTemplateMetadataProvider delegate = new FileTemplateMetadataProvider(new ClassPathResource("saml/our-metadata.xml"),
                environment.getProperty("jks.public.key"),
                environment.getProperty("app.baseURL"));
        delegate.setParserPool(parserPool());

        ExtendedMetadataDelegate localMetadataDelegate = new ExtendedMetadataDelegate(delegate, defaultMetadata);
        providers.add(localMetadataDelegate);
        return new CachingMetadataManager(providers);
    }

    // Filter automatically generates default SP metadata
    @Bean
    public MetadataGenerator metadataGenerator() {
        MetadataGenerator metadataGenerator = new MetadataGenerator();
        metadataGenerator.setExtendedMetadata(extendedMetadata());
        metadataGenerator.setIncludeDiscoveryExtension(false);
        metadataGenerator.setKeyManager(keyManager());
        metadataGenerator.setEntityBaseURL(environment.getProperty("app.baseURL"));

        return metadataGenerator;
    }

    // The filter is waiting for connections on URL suffixed with filterSuffix
    // and presents SP metadata there
    @Bean
    public MetadataDisplayFilter metadataDisplayFilter() {
        return new MetadataDisplayFilter();
    }

    // Handler deciding where to redirect user after successful login
    @Bean
    public AuthenticationSuccessHandler successRedirectHandler() {
        return new SessionStoringAuthenticationSuccessHandler(appURI(""));
    }

    // Handler deciding where to redirect user after failed login
    @Bean
    public RedirectingAuthenticationFailureHandler authenticationFailureHandler() {
        return new RedirectingAuthenticationFailureHandler(appURI(""));
    }

    // Processing filter for WebSSO profile messages
    @Bean
    public SAMLProcessingFilter samlWebSSOProcessingFilter() throws Exception {
        SAMLProcessingFilter samlWebSSOProcessingFilter = new SAMLProcessingFilter();
        samlWebSSOProcessingFilter.setAuthenticationManager(authenticationManager());
        samlWebSSOProcessingFilter.setAuthenticationSuccessHandler(successRedirectHandler());
        samlWebSSOProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
        return samlWebSSOProcessingFilter;
    }

    @Bean
    public MetadataGeneratorFilter metadataGeneratorFilter() {
        return new MetadataGeneratorFilter(metadataGenerator());
    }

    @Bean
    public SessionDestroyingLogoutHandler logoutHandler() {
        return new SessionDestroyingLogoutHandler();
    }

    @Bean
    public SuccessfulLogoutRedirectHandler successfulLogoutRedirectHandler() {
        return new SuccessfulLogoutRedirectHandler(appURI(""));
    }

    private String appURI(String s) {
        return environment.getProperty("app.baseURL") + s;
    }

    // Filter processing incoming logout messages
    // First argument determines URL user will be redirected to after successful
    // global logout
    @Bean
    public SAMLLogoutProcessingFilter samlLogoutProcessingFilter() {
        return new SAMLLogoutProcessingFilter(
                successfulLogoutRedirectHandler(),
                logoutHandler()
        );
    }

    // Overrides default logout processing filter with the one processing SAML
    // messages
    @Bean
    public SAMLLogoutFilter samlLogoutFilter() {
        return new SAMLLogoutFilter(successfulLogoutRedirectHandler(),
                new LogoutHandler[] { logoutHandler() },
                new LogoutHandler[] { logoutHandler() });
    }

    @Bean
    public HTTPPostBinding httpPostBinding() {
        return new HTTPPostBinding(parserPool(), velocityEngine());
    }

    @Bean
    public HTTPRedirectDeflateBinding httpRedirectDeflateBinding() {
        return new HTTPRedirectDeflateBinding(parserPool());
    }

    // Processor
    @Bean
    public SAMLProcessorImpl processor() {
        Collection<SAMLBinding> bindings = new ArrayList<SAMLBinding>();
        bindings.add(httpRedirectDeflateBinding());
        bindings.add(httpPostBinding());
        return new SAMLProcessorImpl(bindings);
    }

    /**
     * Define the security filter chain in order to support SSO Auth by using SAML 2.0
     *
     * @return Filter chain proxy
     * @throws Exception
     */
    @Bean
    public FilterChainProxy samlFilter() throws Exception {
        List<SecurityFilterChain> chains = new ArrayList<SecurityFilterChain>();
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/login/**"),
                new TargetStoringFilter(),
                samlEntryPoint()));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/logout/**"),
                new TargetStoringFilter(),
                samlLogoutFilter()));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/metadata/**"),
                metadataDisplayFilter()));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSO/**"),
                samlWebSSOProcessingFilter()));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SingleLogout/**"),
                samlLogoutProcessingFilter()));
        return new FilterChainProxy(chains);
    }

    /**
     * Returns the authentication manager currently used by Spring.
     * It represents a bean definition with the aim allow wiring from
     * other classes performing the Inversion of Control (IoC).
     *
     * @throws  Exception
     */
    @Bean(name= "authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Sets a custom authentication provider.
     *
     * @param   auth SecurityBuilder used to create an AuthenticationManager.
     * @throws  Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .authenticationProvider(samlAuthenticationProvider());
    }

}
