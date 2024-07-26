package testessentials.dbunit;

import org.junit.jupiter.api.TestInfo;
import org.testng.annotations.BeforeMethod;

public abstract class DatabaseIntegrationTestBase {

  private DbUnitIntegrator dbUnitIntegrator;

  @BeforeMethod
  public void beforeTestMethod(TestInfo testInfo) throws Exception {
    dbUnitIntegrator =
        new DbUnitIntegrator(testInfo.getTestClass().get(), testInfo.getTestMethod().get());
    dbUnitIntegrator.beforeTestMethod();
  }

  @BeforeMethod
  public void afterTestMethod() throws Exception {
    dbUnitIntegrator.afterTestMethod();
  }
}
