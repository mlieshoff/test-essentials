package testessentials.mockito.log4j.testng;

import static java.util.Objects.requireNonNull;

import org.testng.ITestResult;
import testessentials.log4j.LogAccessor;
import testessentials.mockito.log4j.Log4jMockitoAppender;
import testessentials.mockito.testng.listener.InstanceFinder;

public class MockitoLog4jListener extends TestListenerAdapter {

  private static final ThreadLocal<Log4jMockitoAppender> THREAD_LOCAL = new ThreadLocal<>();

  @Override
  protected void onBefore(ITestResult testResult) {
    Object instance = InstanceFinder.find(testResult);
    Log4jMockitoAppender log4jMockitoAppender = new Log4jMockitoAppender(instance);
    log4jMockitoAppender.init();
    THREAD_LOCAL.set(log4jMockitoAppender);
  }

  @Override
  protected void onAfter(ITestResult testResult) {
    try {
      Log4jMockitoAppender log4jMockitoAppender = THREAD_LOCAL.get();
      requireNonNull(log4jMockitoAppender);
      log4jMockitoAppender.reset();
    } finally {
      THREAD_LOCAL.remove();
    }
  }

  public static LogAccessor getAccessor() {
    Log4jMockitoAppender log4jMockitoAppender = THREAD_LOCAL.get();
    return new LogAccessor(log4jMockitoAppender.getEvents());
  }
}
