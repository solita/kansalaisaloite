package fi.om.initiative;

import fi.om.initiative.conf.PropertyNames;
import fi.om.initiative.server.JettyServer;
import org.eclipse.jetty.server.Server;

public class StartJetty {
        public static Server startService(int port, String profile) {
        try {
            return JettyServer.start(new JettyServer.JettyProperties(
                    port,
                    10,
                    profile,
                    "config/log4j.properties",
                    "src/main/webapp/"));

        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);

        }
    }

    public static void main(String[] args) throws Throwable {
        System.setProperty(PropertyNames.optimizeResources, "false");
        startService(8090, "dev,disableSecureCookie").join();

    }
    
}
