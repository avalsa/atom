package accountserver;

import accountserver.auth.AuthenticationFilter;
import main.ApplicationContext;
import main.Service;
import messageSystem.Address;
import messageSystem.MessageSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


public class AccountServer extends Service {
  private final static @NotNull Logger log = LogManager.getLogger(AccountServer.class);
  private final int port;

  public AccountServer() {
    super("account_server");
    FileInputStream fis;
    Properties property = new Properties();
    int por=8080;
    try {
      fis = new FileInputStream("src/main/resources/config.properties");
      property.load(fis);
      por = Integer.parseInt(property.getProperty("accountServerPort"));
    } catch (FileNotFoundException e) {
      log.error(e);
    } catch (IOException e) {
      log.error(e);
    }
    this.port=por;
  }
  private void startApi() {
    ServletContextHandler context = new ServletContextHandler();
    context.setContextPath("/");

    org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(port);
    server.setHandler(context);

    ServletHolder jerseyServlet = context.addServlet(
        org.glassfish.jersey.servlet.ServletContainer.class, "/*");
    jerseyServlet.setInitOrder(0);

    jerseyServlet.setInitParameter(
        "jersey.config.server.provider.packages",
        "accountserver"
    );

    jerseyServlet.setInitParameter(
        "com.sun.jersey.spi.container.ContainerRequestFilters",
        AuthenticationFilter.class.getCanonicalName()
    );

    log.info(getAddress() + " started on port " + port);
    try {
      server.start();
    } catch (Exception e) {
      log.error(e);
    }
  }

  public static void main(@NotNull String[] args) throws Exception {
    new AccountServer().startApi();
  }

  @Override
  public void run() {
    startApi();
    while (true) {
      ApplicationContext.instance().get(MessageSystem.class).execForService(this);
    }
  }
}
