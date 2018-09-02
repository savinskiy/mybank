package core.configuration;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

// TODO: 30.08.2018 move out properties to config
@Slf4j
public class DatabaseLauncher {

  private static final String DEFAULT_USERNAME = "postgres";
  private static final String TEST_DATABASE_NAME = "test";

  private static EmbeddedPostgres embeddedPostgres;

  public static void run(int port, String pgConfigDir) {
    log.info("Start embedded postgres on localhost by port: {}", port);
    log.debug("Database config directory: {}", pgConfigDir);
    try {
      embeddedPostgres = EmbeddedPostgres.builder()
          .setPort(port)
          .setLocaleConfig("locale", "C")
          .setLocaleConfig("lc-collate", "C")
          .setLocaleConfig("lc-ctype", "C")
          .setDataDirectory(Paths.get(pgConfigDir).toAbsolutePath())
          .start();
      try (Connection connection = embeddedPostgres.getPostgresDatabase().getConnection()) {
        createDatabase(connection, TEST_DATABASE_NAME, DEFAULT_USERNAME);
      } catch (SQLException e) {
        log.warn("Failed to create database: " , e.getMessage(), e);
      }
    } catch (IOException e) {
      throw new IllegalStateException("Couldn't set up postgres", e);
    } catch (IllegalStateException e) {
      throw new IllegalStateException("Failed to start postgres due to: " + e.getMessage()
          + "\nTry to delete pg-config directory in this project.\n"
          + "If it wouldn't help, check that there are no running PG processes on the port", e);
    }
  }

  public static void stop() {
    if (embeddedPostgres != null) {
      try {
        embeddedPostgres.close();
      } catch (Exception e) {
        throw new IllegalStateException("Couldn't shut down database", e);
      }
    }
  }

  private static void createDatabase(Connection c, String dbName, String userName)
      throws SQLException {
    String createQuery = "CREATE DATABASE %s OWNER %s ENCODING = 'utf8'";

    log.info("Create database '{}' with user: {}", userName);
    try (PreparedStatement ps = c.prepareStatement(String.format(createQuery, dbName, userName))) {
      ps.execute();
    }
  }
}
