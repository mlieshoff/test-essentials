package testessentials.mockito.testng;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import testessentials.core.TestDependency;
import testessentials.core.TestService;
import testessentials.core.UnitUnderTest;
import testessentials.mockito.testng.listener.AutoCloseableListener;

@Listeners({AutoCloseableListener.class})
public class SampleTest {

  @UnitUnderTest private TestService unitUnderTest;

  @Mock private TestDependency testDependency;

  @BeforeMethod
  void sampleTestSetUp() {
    unitUnderTest = new TestService(testDependency);
  }

  @Test
  void foo_whenFunctionEnabled_thenReturnTrue() {
    when(testDependency.isFunctionEnabled()).thenReturn(true);

    boolean actual = unitUnderTest.foo();

    assertThat(actual).isTrue();
  }

  @Test
  void foo_whenFunctionNotEnabled_thenReturnFalse() {
    when(testDependency.isFunctionEnabled()).thenReturn(false);

    boolean actual = unitUnderTest.foo();

    assertThat(actual).isFalse();
  }
}
