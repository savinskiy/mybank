package com.github.savinskiy.core.configuration;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DIContainer {

  private static final Injector injector;

  static {
    log.info("Start initializing guice context");
    injector = Guice.createInjector(new DbModule(), new AppModule());
  }

  public static Injector getInjector() {
    return injector;
  }

  private DIContainer() {
  }
}