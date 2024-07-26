package testessentials.mockito.testng.listener;

import org.testng.ITestResult;
import testessentials.mockito.testng.AutoCloseableHandler;

public class AutoCloseableListener extends TestListenerAdapter {

  @Override
  protected void onBefore(ITestResult testResult) {
    AutoCloseableHandler.open(InstanceFinder.find(testResult));
  }

  @Override
  protected void onAfter(ITestResult testResult) {
    try {
      AutoCloseableHandler.close();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
