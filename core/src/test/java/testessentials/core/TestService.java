package testessentials.core;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestService {

  private final TestDependency testDependency;

  public boolean foo() {
    return testDependency.isFunctionEnabled();
  }
}
