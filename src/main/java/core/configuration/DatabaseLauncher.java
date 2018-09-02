package core.configuration;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// TODO: 30.08.2018 move out properties to config
public class DatabaseLauncher {

  private static final String DEFAULT_USERNAME = "postgres";
  private static final String TEST_DATABASE_NAME = "test";

  private static EmbeddedPostgres embeddedPostgres;

  public static void run(int port, String pgConfigDir) {
    try {
      embeddedPostgres = EmbeddedPostgres.builder()
          .setPort(port)
          .setLocaleConfig("locale", "C")
          .setLocaleConfig("lc-collate", "C")
          .setLocaleConfig("lc-ctype", "C")
          .setDataDirectory(Paths.get("pg-config").toAbsolutePath())
          .start();
      try (Connection connection = embeddedPostgres.getPostgresDatabase().getConnection()) {
        createDatabase(connection, TEST_DATABASE_NAME, DEFAULT_USERNAME);
      } catch (SQLException e) {
        System.out.println("Failed to create database: " + e.getMessage());
      }
    } catch (IOException e) {
      throw new IllegalStateException("Couldn't set up postgres", e);
    } catch (IllegalStateException e) {
      throw new IllegalStateException("Failed to start postgres due to: " + e.getMessage()
          + ". Try to delete pg-config directory in this project", e);
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

    try (PreparedStatement ps = c.prepareStatement(String.format(createQuery, dbName, userName))) {
      ps.execute();
    }
  }
}
