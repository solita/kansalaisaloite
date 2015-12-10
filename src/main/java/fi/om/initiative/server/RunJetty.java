package fi.om.initiative.server;

import fi.om.initiative.conf.ConfigurationFileLoader;

public class RunJetty {

    private static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";
    private static final String JETTY_PORT = "jetty.port";
    private static final String JETTY_THREAD_POOL_COUNT = "jetty.thread.pool";

    public static void main(String[] args) throws Throwable {
        try {
            JettyServer.start(new JettyServer.JettyProperties(
                    Integer.valueOf(getSystemProperty(JETTY_PORT)),
                    Integer.valueOf(getSystemProperty(JETTY_THREAD_POOL_COUNT)),
                    getSystemProperty(SPRING_PROFILES_ACTIVE),
                    ConfigurationFileLoader.getFile("log4j.properties").toString()
            )).join();
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }



    private static String getSystemProperty(String variableName) {
        String s = System.getProperty(variableName);
        if (s == null) {
            throw new NullPointerException("System property was null: " + variableName);
        }
        return s;
    }
}
