package testessentials.dbunit.testng;

import java.lang.reflect.Method;
import org.junit.jupiter.api.extension.*;
import org.testng.IClass;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import testessentials.dbunit.DbUnitIntegrator;
import testessentials.mockito.testng.listener.TestListenerAdapter;

public class DbUnitListener extends TestListenerAdapter {

  private static final ThreadLocal<DbUnitIntegrator> THREAD_LOCAL = new ThreadLocal<>();

  @Override
  protected void onBefore(ITestResult testResult) {
    IClass clazz = testResult.getTestClass();
    ITestNGMethod method = testResult.getMethod();
    DbUnitIntegrator dbUnitIntegrator =
        new DbUnitIntegrator(clazz.getRealClass(), method.getConstructorOrMethod().getMethod());
    THREAD_LOCAL.set(dbUnitIntegrator);
    try {
      dbUnitIntegrator.beforeTestMethod();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  protected void onAfter(ITestResult result) {
    DbUnitIntegrator dbUnitIntegrator = THREAD_LOCAL.get();
    try {
      dbUnitIntegrator.afterTestMethod();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
