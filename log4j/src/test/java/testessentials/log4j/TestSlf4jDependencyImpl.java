package testessentials.log4j;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestSlf4jDependencyImpl implements TestSlf4jDependency {

  public boolean isTestEnabledForLogging() {
    return System.currentTimeMillis() % 2 == 0;
  }
}
