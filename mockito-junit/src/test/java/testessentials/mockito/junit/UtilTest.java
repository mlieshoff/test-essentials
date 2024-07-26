package testessentials.mockito.junit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import testessentials.core.Util;
import testessentials.mockito.StaticMockery;

@ExtendWith({MockitoExtension.class, MockitoStaticExtension.class})
class UtilTest {

  @Test
  void staticUtilityMethod_whenWithFalse_thenReturnFalse(StaticMockery staticMockery) {
    utilStaticUtilityMethodReturns(staticMockery, false);

    boolean actual = Util.staticUtilityMethod();

    assertThat(actual).isFalse();
  }

  private void utilStaticUtilityMethodReturns(StaticMockery staticMockery, boolean value) {
    MockedStatic<Util> utilMockedStatic = staticMockery.create(Util.class);
    utilMockedStatic.when(Util::staticUtilityMethod).thenReturn(value);
  }

  @Test
  void staticUtilityMethod_whenWithTrue_thenReturnTrue(StaticMockery staticMockery) {
    utilStaticUtilityMethodReturns(staticMockery, true);

    boolean actual = Util.staticUtilityMethod();

    assertThat(actual).isTrue();
  }
}
