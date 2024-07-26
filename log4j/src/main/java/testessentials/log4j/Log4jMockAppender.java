package testessentials.log4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import testessentials.core.Debug;
import testessentials.core.UnitUnderTest;

@RequiredArgsConstructor
public abstract class Log4jMockAppender {

  private static final Object MUTEX = new Object();

  private final List<LogEvent> events = new ArrayList<>();

  private final Object testInstance;

  private Appender appender;

  private Logger logger;

  private Level defaultLevel;

  public void init() {
    appender = createMockAppender();
    prepareAppenderWithMockBehaviour(appender);
    addAppender();
    storeDefaultLoglevelBeforeTracing();
  }

  protected abstract Appender createMockAppender();

  protected abstract void prepareAppenderWithMockBehaviour(Appender appender);

  private void addAppender() {
    Arrays.stream(FieldUtils.getAllFields(testInstance.getClass()))
        .filter(this::isAnnotatedAsAppendable)
        .forEach(field -> addAppenderFor(field.getType()));
    if (!logger.getAppenders().containsKey(appender.getName())) {
      throw new IllegalStateException(
          "Logger " + logger.getName() + " has no Appender " + appender.getName());
    }
  }

  protected boolean isAnnotatedAsAppendable(Field field) {
    return field.isAnnotationPresent(UnitUnderTest.class);
  }

  private void addAppenderFor(Class<?> classWithLoggerField) {
    logger = (Logger) LogManager.getLogger(classWithLoggerField);
    if (logger != null) {
      logger.addAppender(appender);
    } else {
      throw new IllegalStateException(
          "Could not add appender to: " + classWithLoggerField.getName());
    }
  }

  private void storeDefaultLoglevelBeforeTracing() {
    defaultLevel = logger.getLevel();
    Configurator.setLevel(testInstance.getClass(), Level.TRACE);
  }

  public void reset() {
    if (logger != null) {
      Configurator.setLevel(testInstance.getClass(), defaultLevel);
      logger.removeAppender(appender);
      synchronized (MUTEX) {
        events.clear();
      }
    } else {
      throw new IllegalStateException("Could not remove appender for: " + testInstance);
    }
  }

  protected void addLogEvent(LogEvent logEvent) {
    Debug.log("ADD: %s", this, logEvent.getMessage());
    synchronized (MUTEX) {
      events.add(logEvent.toImmutable());
    }
  }

  public List<LogEvent> getEvents() {
    List<LogEvent> logEvents = new ArrayList<>();
    synchronized (MUTEX) {
      if (!events.isEmpty()) {
        logEvents.addAll(events);
      }
    }
    return logEvents.stream()
        .filter(logEvent -> logEvent.getThreadId() == Thread.currentThread().threadId())
        .toList();
  }
}
