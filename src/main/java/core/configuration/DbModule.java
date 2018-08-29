package core.configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.AvailableSettings;

@Slf4j
public class DbModule extends AbstractModule {

  // TODO: 25.08.2018 check persistence context annotation (later)
  private static final ThreadLocal<EntityManager> ENTITY_MANAGER_CACHE
      = new ThreadLocal<EntityManager>();

  private static final Properties properties = new Properties();
  // so we can configure it from tests
  public static String APPLICATION_PROPERTIES = "src/main/resources/application.properties";

  // hack for vertx injection
  public static Properties getProperties() {
    return properties;
  }

  @Override
  public void configure() {
    try {
      properties.load(new FileReader(APPLICATION_PROPERTIES));
    } catch (IOException e) {
      log.error("Failed to load properties from " + APPLICATION_PROPERTIES);
      throw new RuntimeException(e);
    }
    Names.bindProperties(binder(), properties);
  }

  @Provides
  @Singleton
  public EntityManagerFactory provideEntityManagerFactory() {

    Map<Object, Object> hibernateProperties = new HashMap<>();

    hibernateProperties.put(AvailableSettings.DRIVER, properties.get(AvailableSettings.DRIVER));
    hibernateProperties.put(AvailableSettings.URL, properties.get(AvailableSettings.URL));
    hibernateProperties.put(AvailableSettings.USER, properties.get(AvailableSettings.USER));
    hibernateProperties.put(AvailableSettings.PASS, properties.get(AvailableSettings.PASS));
    hibernateProperties.put(AvailableSettings.POOL_SIZE,
        Integer.parseInt((String) properties.get(AvailableSettings.POOL_SIZE)));
    hibernateProperties.put(AvailableSettings.DIALECT, properties.get(AvailableSettings.DIALECT));
    hibernateProperties.put(AvailableSettings.HBM2DDL_AUTO,
        properties.get(AvailableSettings.HBM2DDL_AUTO));

    return Persistence.createEntityManagerFactory("db-manager", hibernateProperties);
  }

  @Provides
  public EntityManager provideEntityManager(EntityManagerFactory entityManagerFactory) {
    EntityManager entityManager = ENTITY_MANAGER_CACHE.get();
    if (entityManager == null) {
      ENTITY_MANAGER_CACHE.set(entityManager = entityManagerFactory.createEntityManager());
    }
    return entityManager;
  }

}