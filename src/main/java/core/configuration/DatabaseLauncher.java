package core.configuration;

import static java.util.Arrays.asList;
import static ru.yandex.qatools.embed.postgresql.EmbeddedPostgres.DEFAULT_DB_NAME;
import static ru.yandex.qatools.embed.postgresql.EmbeddedPostgres.DEFAULT_HOST;
import static ru.yandex.qatools.embed.postgresql.EmbeddedPostgres.DEFAULT_PASSWORD;
import static ru.yandex.qatools.embed.postgresql.EmbeddedPostgres.DEFAULT_USER;
import static ru.yandex.qatools.embed.postgresql.EmbeddedPostgres.cachedRuntimeConfig;
import static ru.yandex.qatools.embed.postgresql.distribution.Version.Main.PRODUCTION;

import java.nio.file.Paths;
import java.util.List;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;

// TODO: 30.08.2018 check how to run as privileged user
// TODO: 30.08.2018 move out properties to config
public class DatabaseLauncher {

  private static EmbeddedPostgres postgres = null;

  private static final List<String> DEFAULT_ADD_PARAMS = asList(
      "-E", "SQL_ASCII",
      "--locale=C",
      "--lc-collate=C",
      "--lc-ctype=C");

  public static void run(int port, String pgConfigDir) {

    try {
      postgres = new EmbeddedPostgres(PRODUCTION, pgConfigDir);
      final String url = postgres.start(
          cachedRuntimeConfig(Paths.get(pgConfigDir + "/cache")), DEFAULT_HOST,
          port, DEFAULT_DB_NAME, DEFAULT_USER, DEFAULT_PASSWORD, DEFAULT_ADD_PARAMS
      );
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void stop() {
    postgres.stop();
  }
}
