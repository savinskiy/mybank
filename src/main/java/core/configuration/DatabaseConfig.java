// consider this yandex embedded db later

//package web.routers;
//
//import static java.util.Arrays.asList;
//import static ru.yandex.qatools.embed.postgresql.EmbeddedPostgres.DEFAULT_DB_NAME;
//import static ru.yandex.qatools.embed.postgresql.EmbeddedPostgres.DEFAULT_HOST;
//import static ru.yandex.qatools.embed.postgresql.EmbeddedPostgres.DEFAULT_PASSWORD;
//import static ru.yandex.qatools.embed.postgresql.EmbeddedPostgres.DEFAULT_USER;
//import static ru.yandex.qatools.embed.postgresql.EmbeddedPostgres.cachedRuntimeConfig;
//import static ru.yandex.qatools.embed.postgresql.distribution.Version.Main.PRODUCTION;
//
//import java.nio.file.Paths;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.util.List;
//import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;
//
//public class DatabaseConfig {
//
//  public static final String PG_CONFIG_DIR = "pg-config";
//  private static final List<String> DEFAULT_ADD_PARAMS = asList(
//      "-E", "SQL_ASCII",
//      "--locale=C",
//      "--lc-collate=C",
//      "--lc-ctype=C");
//
//  public static void run() {
//    Connection conn = null;
//    EmbeddedPostgres postgres = null;
//    try {
//      postgres = new EmbeddedPostgres(PRODUCTION, PG_CONFIG_DIR);
//      final String url = postgres.start(
//          cachedRuntimeConfig(Paths.get(PG_CONFIG_DIR + "/cache")), DEFAULT_HOST,
//          5432, DEFAULT_DB_NAME, DEFAULT_USER, DEFAULT_PASSWORD, DEFAULT_ADD_PARAMS
//      );
//// connecting to a running Postgres and feeding up the database
//      conn = DriverManager.getConnection(url);
//      conn.createStatement().execute("CREATE TABLE films (code char(5));");
//      conn.createStatement().execute("INSERT INTO films VALUES ('movie');");
//    } catch (Exception e) {
//      e.printStackTrace();
//    } finally {
//
//// close db connection
//      try {
//        conn.close();
//      } catch (SQLException e) {
//
//      } finally {
//        postgres.stop();
//      }
//// stop Postgres
//
//    }
//
//  }
//}
