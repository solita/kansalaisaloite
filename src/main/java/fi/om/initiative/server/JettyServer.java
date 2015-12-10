package fi.om.initiative.server;

import com.google.common.base.Optional;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.util.Log4jConfigListener;

public class JettyServer {

    public static class JettyProperties {
        public final int jettyPort;
        public final int jettyThreadPoolCount;
        public final String springProfile;
        public final String log4jConfigPath;
        public final Optional<String> customWebappContextPath;

        public JettyProperties(int jettyPort, int jettyThreadPoolCount,
                               String springProfile, String log4jConfigPath) {
            this.jettyPort = jettyPort;
            this.jettyThreadPoolCount = jettyThreadPoolCount;
            this.springProfile = springProfile;
            this.log4jConfigPath = log4jConfigPath;
            this.customWebappContextPath = Optional.absent();
        }

        public JettyProperties(int jettyPort, int jettyThreadPoolCount,
                               String springProfile, String log4jConfigPath, String customWebappContextPath) {
            this.jettyPort = jettyPort;
            this.jettyThreadPoolCount = jettyThreadPoolCount;
            this.springProfile = springProfile;
            this.log4jConfigPath = log4jConfigPath;
            this.customWebappContextPath = Optional.of(customWebappContextPath);
        }
    }

    public static Server start(JettyProperties properties) throws Throwable {

        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMaxThreads(properties.jettyThreadPoolCount);

        Server server = new Server(threadPool);

        ServerConnector http = new ServerConnector(server,new HttpConnectionFactory());
        http.setPort(properties.jettyPort);
        server.addConnector(http);

        WebAppContext context = new WebAppContext();

        // Logging configured per environment:
        context.addEventListener(new Log4jConfigListener());
        context.setInitParameter("log4jConfigLocation", "file:" + properties.log4jConfigPath);
        context.setInitParameter("log4jExposeWebAppRoot", "false");

        context.setDescriptor(new ClassPathResource("src/main/webapp/WEB-INF/web.xml").getURI().toString());
        context.setResourceBase(new ClassPathResource("src/main/webapp").getURI().toString());

        context.setContextPath("/");
        context.setParentLoaderPriority(true);
        context.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");

        context.setInitParameter("spring.profiles.active", properties.springProfile);

        server.setHandler(context);
        server.start();
        return server;
    }


}
