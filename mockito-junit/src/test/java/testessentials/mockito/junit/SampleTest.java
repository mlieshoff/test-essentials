package testessentials.mockito.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import testessentials.core.TestDependency;
import testessentials.core.TestService;

@ExtendWith({MockitoExtension.class})
public class SampleTest {

  @InjectMocks private TestService unitUnderTest;

  @Mock private TestDependency testDependency;

  @Test
  void foo_whenFunctionIsEnabled_thenReturnTrue() {
    isFunctionEnabledReturns(true);

    boolean actual = unitUnderTest.foo();

    assertThat(actual).isTrue();
  }

  private void isFunctionEnabledReturns(boolean result) {
    when(testDependency.isFunctionEnabled()).thenReturn(result);
  }

  @Test
  void foo_whenFunctionIsNotEnabled_thenReturnFalse() {
    isFunctionEnabledReturns(false);

    boolean actual = unitUnderTest.foo();

    assertThat(actual).isFalse();
  }
}
