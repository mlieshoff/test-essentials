package testessentials.mockito.testng.listener;

import org.testng.ITestResult;
import testessentials.mockito.StaticMockery;

public class StaticMockListener extends TestListenerAdapter {

  private static final ThreadLocal<StaticMockery> THREAD_LOCAL = new ThreadLocal<>();

  @Override
  public void onBefore(ITestResult testResult) {
    StaticMockery staticMockery = getStaticMockery();
    if (staticMockery == null) {
      staticMockery = new StaticMockery();
      THREAD_LOCAL.set(staticMockery);
    } else {
      throw new IllegalStateException("The static mockery should NOT be present on before call!");
    }
  }

  @Override
  public void onAfter(ITestResult testResult) {
    StaticMockery staticMockery = getStaticMockery();
    if (staticMockery == null) {
      throw new IllegalStateException("The static mockery should be present on after call!");
    } else {
      try {
        staticMockery.closeAll();
      } finally {
        THREAD_LOCAL.remove();
      }
    }
  }

  public static StaticMockery getStaticMockery() {
    return THREAD_LOCAL.get();
  }
}
