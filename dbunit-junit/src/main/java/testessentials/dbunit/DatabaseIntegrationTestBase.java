package testessentials.dbunit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

public abstract class DatabaseIntegrationTestBase {

  private DbUnitIntegrator dbUnitIntegrator;

  @BeforeEach
  public void beforeTestMethod(TestInfo testInfo) throws Exception {
    dbUnitIntegrator =
        new DbUnitIntegrator(testInfo.getTestClass().get(), testInfo.getTestMethod().get());
    dbUnitIntegrator.beforeTestMethod();
  }

  @AfterEach
  public void afterTestMethod() throws Exception {
    dbUnitIntegrator.afterTestMethod();
  }
}
