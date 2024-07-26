package testessentials.mockito.testng;

import static org.assertj.core.api.Assertions.assertThat;

import org.mockito.MockedStatic;
import org.testng.ITestContext;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import testessentials.core.Util;
import testessentials.mockito.testng.listener.StaticMockListener;

@Listeners(StaticMockListener.class)
public class UtilTest {

  @Test(invocationCount = 10, threadPoolSize = 2)
  void staticUtilityMethod_whenWithFalse_thenReturnFalse(ITestContext testContext) {
    utilStaticUtilityMethodReturns(false);

    boolean actual = Util.staticUtilityMethod();

    assertThat(actual).isFalse();
  }

  private void utilStaticUtilityMethodReturns(boolean value) {
    MockedStatic<Util> utilMockedStatic = StaticMockListener.getStaticMockery().create(Util.class);
    utilMockedStatic.when(Util::staticUtilityMethod).thenReturn(value);
  }

  @Test(invocationCount = 10, threadPoolSize = 2)
  void staticUtilityMethod_whenWithTrue_thenReturnTrue(ITestContext testContext) {
    utilStaticUtilityMethodReturns(true);

    boolean actual = Util.staticUtilityMethod();

    assertThat(actual).isTrue();
  }
}
