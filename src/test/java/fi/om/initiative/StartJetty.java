package fi.om.initiative;

import org.eclipse.jetty.http.ssl.SslContextFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

import fi.om.initiative.conf.PropertyNames;

public class StartJetty {
    
    public static final int PORT = 8443;

    public static void main(String[] args) {
        try {
            System.setProperty(PropertyNames.optimizeResources, "false");
            startService(PORT, null).join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Server startService(int port, String profile) {
        Server server = new Server();
        SslContextFactory sslContext = new SslContextFactory("keystore");
        sslContext.setKeyStorePassword("aloitepalvelu");

//        SelectChannelConnector connector = new SelectChannelConnector();
//        connector.setPort(8080);
        
        SslSelectChannelConnector sslConnector = new SslSelectChannelConnector(sslContext);
        sslConnector.setPort(port);
        server.setConnectors(new Connector[] { sslConnector });
                
        WebAppContext context = new WebAppContext();
        context.setDescriptor("src/main/webapp/WEB-INF/web.xml");
        context.setResourceBase("src/main/webapp/");
        context.setContextPath("/");
        context.setParentLoaderPriority(true);
        context.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
        
        if (profile != null) {
            context.setInitParameter("spring.profiles.active", profile);
        }

        server.setHandler(context);
        
        try {
            server.start();
            return server;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
