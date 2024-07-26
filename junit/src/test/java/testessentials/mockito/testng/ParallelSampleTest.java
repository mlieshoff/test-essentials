package testessentials.mockito.testng;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.testng.annotations.*;
import testessentials.core.TestDependency;
import testessentials.core.TestService;
import testessentials.core.FixtureSupplier;
import testessentials.core.IFixture;
import testessentials.core.UnitUnderTest;
import testessentials.mockito.testng.listener.AutoCloseableListener;
import testessentials.mockito.testng.listener.FixtureListener;
import testessentials.mockito.testng.listener.UnitUnderTestListener;

@Listeners({FixtureListener.class, AutoCloseableListener.class, UnitUnderTestListener.class})
public class ParallelSampleTest {

  public static class Fixture implements IFixture {
    @UnitUnderTest private TestService unitUnderTest;

    @Mock TestDependency testDependency;

    @Override
    public void createUnitUnderTest() {
      unitUnderTest = new TestService(testDependency);
    }
  }

  @FixtureSupplier
  public Fixture createFixture() {
    return new Fixture();
  }

  @Test(invocationCount = 10, threadPoolSize = 2)
  void foo_whenFunctionEnabled_thenReturnTrue() {
    when(FixtureListener.getFixture(Fixture.class).testDependency.isFunctionEnabled())
        .thenReturn(true);

    boolean actual = FixtureListener.getFixture(Fixture.class).unitUnderTest.foo();

    assertThat(actual).isTrue();
  }

  @Test(invocationCount = 10, threadPoolSize = 2)
  void foo_whenFunctionNotEnabled_thenReturnFalse() {
    when(FixtureListener.getFixture(Fixture.class).testDependency.isFunctionEnabled())
        .thenReturn(false);

    boolean actual = FixtureListener.getFixture(Fixture.class).unitUnderTest.foo();

    assertThat(actual).isFalse();
  }
}
