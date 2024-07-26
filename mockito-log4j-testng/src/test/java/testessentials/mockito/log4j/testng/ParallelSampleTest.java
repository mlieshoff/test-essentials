package testessentials.mockito.log4j.testng;

import static org.mockito.Mockito.when;
import static testessentials.log4j.assertj.LogAccessorAssert.assertThatFormattedLogMessagesFrom;

import org.mockito.Mock;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import testessentials.core.FixtureSupplier;
import testessentials.core.IFixture;
import testessentials.core.UnitUnderTest;
import testessentials.log4j.TestSlf4jDependency;
import testessentials.log4j.TestSlf4jService;
import testessentials.mockito.testng.listener.AutoCloseableListener;
import testessentials.mockito.testng.listener.FixtureListener;
import testessentials.mockito.testng.listener.UnitUnderTestListener;

@Listeners({
  FixtureListener.class,
  AutoCloseableListener.class,
  UnitUnderTestListener.class,
  MockitoLog4jListener.class
})
public class ParallelSampleTest {

  static class Fixture implements IFixture {
    @UnitUnderTest private TestSlf4jService unitUnderTest;

    @Mock private TestSlf4jDependency testSlf4jDependency;

    @Override
    public void createUnitUnderTest() {
      unitUnderTest = new TestSlf4jService(testSlf4jDependency);
    }
  }

  @FixtureSupplier
  public Fixture createFixture() {
    return new Fixture();
  }

  @Test(invocationCount = 2, threadPoolSize = 2)
  void fooWithErrorLogging_whenDeviceEnabledForLogging_thenLogToError() {
    String message = Thread.currentThread().threadId() + "_" + System.currentTimeMillis() + "_errorParallel";
    when(FixtureListener.getFixture(Fixture.class).testSlf4jDependency.isTestEnabledForLogging())
        .thenReturn(true);

    FixtureListener.getFixture(Fixture.class).unitUnderTest.fooWithErrorLogging(message);

    assertThatFormattedLogMessagesFrom(MockitoLog4jListener.getAccessor())
        .forError()
        .containsOnly(message);
  }

  @Test(invocationCount = 2, threadPoolSize = 2)
  void fooWithErrorLogging_whenDeviceNotEnabledForLogging_thenLogNothingToError() {
    String message = Thread.currentThread().threadId() + "_" + System.currentTimeMillis() + "_emptyParallel";
    when(FixtureListener.getFixture(Fixture.class).testSlf4jDependency.isTestEnabledForLogging())
        .thenReturn(false);

    FixtureListener.getFixture(Fixture.class).unitUnderTest.fooWithErrorLogging(message);

    assertThatFormattedLogMessagesFrom(MockitoLog4jListener.getAccessor()).forError().isEmpty();
  }
}
