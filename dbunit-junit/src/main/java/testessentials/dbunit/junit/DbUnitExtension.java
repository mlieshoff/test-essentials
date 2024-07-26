package testessentials.dbunit.junit;

import java.lang.reflect.Method;
import org.junit.jupiter.api.extension.*;
import testessentials.dbunit.DbUnitIntegrator;

public class DbUnitExtension implements BeforeEachCallback, AfterEachCallback {

  private static final ExtensionContext.Namespace NAMESPACE =
      ExtensionContext.Namespace.create("testessentials.dbunit");

  private static final String ID = "static";

  @Override
  public void beforeEach(ExtensionContext extensionContext) throws Exception {
    Class<?> clazz = extensionContext.getRequiredTestClass();
    Method method = extensionContext.getRequiredTestMethod();
    DbUnitIntegrator dbUnitIntegrator = new DbUnitIntegrator(clazz, method);
    extensionContext.getStore(NAMESPACE).put(ID, dbUnitIntegrator);
    dbUnitIntegrator.beforeTestMethod();
  }

  @Override
  public void afterEach(ExtensionContext extensionContext) throws Exception {
    DbUnitIntegrator dbUnitIntegrator =
        (DbUnitIntegrator) extensionContext.getStore(NAMESPACE).remove(ID);
    dbUnitIntegrator.afterTestMethod();
  }
}
