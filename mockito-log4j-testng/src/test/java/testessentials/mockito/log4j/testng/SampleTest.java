package testessentials.mockito.log4j.testng;

import static org.mockito.Mockito.when;
import static testessentials.log4j.assertj.LogAccessorAssert.assertThatFormattedLogMessagesFrom;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import testessentials.core.UnitUnderTest;
import testessentials.log4j.TestSlf4jDependency;
import testessentials.log4j.TestSlf4jService;
import testessentials.mockito.testng.listener.AutoCloseableListener;

@Listeners({AutoCloseableListener.class, MockitoLog4jListener.class})
public class SampleTest {

  @UnitUnderTest private TestSlf4jService unitUnderTest;

  @Mock private TestSlf4jDependency testSlf4jDependency;

  @BeforeMethod
  void setUp() {
    unitUnderTest = new TestSlf4jService(testSlf4jDependency);
  }

  @Test
  void fooWithErrorLogging_whenDeviceEnabledForLogging_thenLogToError() {
    String message =
        Thread.currentThread().threadId() + "_" + System.currentTimeMillis() + "_error";
    when(testSlf4jDependency.isTestEnabledForLogging()).thenReturn(true);

    unitUnderTest.fooWithErrorLogging(message);

    assertThatFormattedLogMessagesFrom(MockitoLog4jListener.getAccessor())
        .forError()
        .containsOnly(message);
  }

  @Test
  void fooWithErrorLogging_whenDeviceNotEnabledForLogging_thenLogNothingToError() {
    String message =
        Thread.currentThread().threadId() + "_" + System.currentTimeMillis() + "_empty";
    when(testSlf4jDependency.isTestEnabledForLogging()).thenReturn(false);

    unitUnderTest.fooWithErrorLogging(message);

    assertThatFormattedLogMessagesFrom(MockitoLog4jListener.getAccessor()).forError().isEmpty();
  }
}
