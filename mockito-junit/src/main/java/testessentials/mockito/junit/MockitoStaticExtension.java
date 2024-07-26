package testessentials.mockito.junit;

import org.junit.jupiter.api.extension.*;
import testessentials.mockito.StaticMockery;

public class MockitoStaticExtension
    implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

  private static final ExtensionContext.Namespace NAMESPACE =
      ExtensionContext.Namespace.create("testessentials");

  private static final String ID = "static";

  @Override
  public void beforeEach(ExtensionContext extensionContext) {
    StaticMockery staticMockery = new StaticMockery();
    extensionContext.getStore(NAMESPACE).put(ID, staticMockery);
  }

  @Override
  public void afterEach(ExtensionContext extensionContext) {
    StaticMockery staticMockery = (StaticMockery) extensionContext.getStore(NAMESPACE).remove(ID);
    staticMockery.closeAll();
  }

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return parameterContext.getParameter().getType() == StaticMockery.class;
  }

  @Override
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return extensionContext.getStore(NAMESPACE).get(ID);
  }
}
