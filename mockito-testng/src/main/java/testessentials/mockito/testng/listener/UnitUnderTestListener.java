package testessentials.mockito.testng.listener;

import org.testng.ITestResult;
import testessentials.core.IFixture;

public class UnitUnderTestListener extends TestListenerAdapter {

  @Override
  public void onBefore(ITestResult testResult) {
    IFixture fixture = FixtureListener.getFixture();
    if (fixture == null) {
      throw new IllegalStateException("there must be a fixture on before already!");
    } else {
      fixture.createUnitUnderTest();
    }
  }
}
