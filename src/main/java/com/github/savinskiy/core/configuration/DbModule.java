package com.github.savinskiy.core.configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;

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
      log.trace("Loaded properties: {}", properties);
    } catch (IOException e) {
      log.error("Failed to load properties from {}, cause: {}",
          APPLICATION_PROPERTIES, e);
      throw new RuntimeException(e);
    }
    Names.bindProperties(binder(), properties);
  }

  @Provides
  @Singleton
  public EntityManagerFactory provideEntityManagerFactory() {

    Map<Object, Object> hibernateProperties = properties.entrySet().stream()
        .filter(e -> String.valueOf(e.getKey()).startsWith("hibernate."))
        .collect(Collectors.toMap(Entry::getKey, this::mapValue));
    log.trace("Hibernate properties: {}", hibernateProperties);

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

  private Object mapValue(Entry entry) {
    final String value = (String) entry.getValue();
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
//      not long
    }
    try {
      return Float.parseFloat(value);
    } catch (NumberFormatException e) {
//      not double
    }
    final Boolean parseBoolean = tryParseBoolean(value);
    if (parseBoolean != null) {
      return parseBoolean;
    }
    return value;
  }

  public Boolean tryParseBoolean(String inputBoolean) {
    if (!inputBoolean.equals("true") && !inputBoolean.equals("false")) {
      return null;
    }
    return Boolean.valueOf(inputBoolean);
  }
}