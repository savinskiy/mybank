package com.github.savinskiy.core.configuration;

import com.google.inject.Guice;
import com.google.inject.Injector;

public final class DIContainer {

  private static final Injector injector;

  static {
    injector = Guice.createInjector(new DbModule(), new AppModule());
  }

  public static Injector getInjector() {
    return injector;
  }

  private DIContainer() {
  }
}