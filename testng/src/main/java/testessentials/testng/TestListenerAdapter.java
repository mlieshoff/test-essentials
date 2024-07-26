package testessentials.mockito.testng.listener;

import lombok.RequiredArgsConstructor;
import org.testng.*;
import org.testng.annotations.Listeners;

import java.util.Arrays;

@RequiredArgsConstructor
public abstract class TestListenerAdapter
    implements IAlterSuiteListener,
        IConfigurationListener,
        IExecutionListener,
        IInvokedMethodListener,
        ISuiteListener,
        ITestListener {

  private final ThreadLocal<Status> threadLocal = new ThreadLocal<>();

  private static class Status {
    private boolean firstMethodCalled;
  }

  private Status getStatus() {
    Status status = threadLocal.get();
    if (status == null) {
      status = new Status();
      threadLocal.set(status);
    }
    return status;
  }

  private void removeStatus() {
    threadLocal.remove();
  }

  protected void doWhenListenerIsSupported(
      ITestResult testResult, SupportedAction supportedAction) {
    if (supports(testResult)) {
      supportedAction.execute(testResult);
    }
  }

  private boolean supports(ITestResult testResult) {
    Class<?> clazz = testResult.getTestClass().getRealClass();
    Listeners listeners = clazz.getAnnotation(Listeners.class);
    if (listeners == null) {
      return false;
    }
    return Arrays.asList(clazz.getAnnotation(Listeners.class).value()).contains(getClass());
  }

  public interface SupportedAction {
    void execute(ITestResult testResult);
  }

  @Override
  public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
    doWhenListenerIsSupported(
        testResult,
        ignore -> {
          Status status = getStatus();
          if (!status.firstMethodCalled) {
            onBefore(testResult);
            status.firstMethodCalled = true;
          }
        });
  }

  protected void onBefore(ITestResult testResult) {
    //
  }

  @Override
  public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
    doWhenListenerIsSupported(
        testResult,
        ignore -> {
          if (method.isTestMethod()) {
            onAfter(testResult);
            removeStatus();
          }
        });
  }

  protected void onAfter(ITestResult result) {
    //
  }
}
