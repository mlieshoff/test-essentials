package testessentials.mockito.log4j.junit;

import static org.mockito.Mockito.when;
import static testessentials.log4j.assertj.LogAccessorAssert.assertThatFormattedLogMessagesFrom;

import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import testessentials.log4j.*;

@ExtendWith({MockitoExtension.class, MockitoLog4jExtension.class})
class SampleTest {

  @InjectMocks private TestSlf4jService unitUnderTest;

  @Mock private TestSlf4jDependency testSlf4jDependency;

  @Test
  void fooWithErrorLogging_whenDeviceEnabledForLogging_thenLogToError(
      Supplier<LogAccessor> logAccessorSupplier) {
    String message = createMessage("error");
    isTestEnabledForLoggingReturns(true);

    unitUnderTest.fooWithErrorLogging(message);

    assertThatFormattedLogMessagesFrom(logAccessorSupplier.get()).forError().containsOnly(message);
  }

  private String createMessage(String suffix) {
    return Thread.currentThread().threadId() + "_" + System.currentTimeMillis() + "_" + suffix;
  }

  private void isTestEnabledForLoggingReturns(boolean result) {
    when(testSlf4jDependency.isTestEnabledForLogging()).thenReturn(result);
  }

  @Test
  void fooWithErrorLogging_whenDeviceNotEnabledForLogging_thenLogNothingToError(
      Supplier<LogAccessor> logAccessorSupplier) {
    String message = createMessage("empty");
    isTestEnabledForLoggingReturns(false);

    unitUnderTest.fooWithErrorLogging(message);

    assertThatFormattedLogMessagesFrom(logAccessorSupplier.get()).forError().isEmpty();
  }
}
