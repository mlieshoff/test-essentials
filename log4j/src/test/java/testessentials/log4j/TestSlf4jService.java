package testessentials.log4j;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TestSlf4jService {
  
  private final TestSlf4jDependency testSlf4jDependency;

  public void fooWithTraceLogging() {
    if (testSlf4jDependency.isTestEnabledForLogging()) {
      log.trace("foo");
    }
  }

  public void fooWithDebugLogging() {
    if (testSlf4jDependency.isTestEnabledForLogging()) {
      log.debug("foo");
    }
  }

  public void fooWithInfoLogging() {
    if (testSlf4jDependency.isTestEnabledForLogging()) {
      log.info("foo");
    }
  }

  public void fooWithErrorLogging(String message) {
    if (testSlf4jDependency.isTestEnabledForLogging()) {
      log.error(message);
    }
  }

}
