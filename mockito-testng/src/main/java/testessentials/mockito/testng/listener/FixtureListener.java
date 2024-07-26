package testessentials.mockito.testng.listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import org.testng.ITestResult;
import testessentials.core.FixtureSupplier;
import testessentials.core.IFixture;

public class FixtureListener extends TestListenerAdapter {

  private static final ThreadLocal<IFixture> THREAD_LOCAL = new ThreadLocal<>();

  @Override
  public void onBefore(ITestResult testResult) {
    Object instance = testResult.getInstance();
    IFixture fixture = getFixture();
    if (fixture == null) {
      Optional<Method> optionalMethod =
          Arrays.stream(instance.getClass().getDeclaredMethods())
              .filter(m -> m.isAnnotationPresent(FixtureSupplier.class))
              .findFirst();
      if (optionalMethod.isPresent()) {
        Method method = optionalMethod.get();
        try {
          fixture = (IFixture) method.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new IllegalStateException(e);
        }
        THREAD_LOCAL.set(fixture);
      } else {
        throw new IllegalStateException(
            "You should not use a FixtureListener without a method annotated with @FixtureSupplier!");
      }
    } else {
      throw new IllegalStateException("There MUST NOT BE a fixture on before already!");
    }
  }

  @Override
  public void onAfter(ITestResult testResult) {
    THREAD_LOCAL.remove();
  }

  public static <T extends IFixture> T getFixture(Class<T> ignored) {
    return (T) THREAD_LOCAL.get();
  }

  public static IFixture getFixture() {
    return getFixture(IFixture.class);
  }
}
