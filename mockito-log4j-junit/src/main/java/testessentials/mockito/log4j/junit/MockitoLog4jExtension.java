package testessentials.mockito.log4j.junit;

import org.junit.jupiter.api.extension.*;
import testessentials.log4j.LogAccessor;
import testessentials.mockito.log4j.Log4jMockitoAppender;

import java.util.function.Supplier;

public class MockitoLog4jExtension
    implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

  private static final ExtensionContext.Namespace NAMESPACE =
      ExtensionContext.Namespace.create("testessentials");

  private static final String ID = "appender";

  @Override
  public void beforeEach(ExtensionContext extensionContext) {
    Log4jMockitoAppender log4jMockitoAppender =
        new Log4jMockitoAppender(extensionContext.getRequiredTestInstance());
    log4jMockitoAppender.init();
    extensionContext.getStore(NAMESPACE).put(ID, log4jMockitoAppender);
  }

  @Override
  public void afterEach(ExtensionContext extensionContext) {
    Log4jMockitoAppender log4jMockitoAppender =
        (Log4jMockitoAppender) extensionContext.getStore(NAMESPACE).remove(ID);
    log4jMockitoAppender.reset();
  }

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return parameterContext.getParameter().getType() == Supplier.class;
  }

  @Override
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    Log4jMockitoAppender log4jMockitoAppender =
        (Log4jMockitoAppender) extensionContext.getStore(NAMESPACE).get(ID);
    return (Supplier<LogAccessor>) () -> new LogAccessor(log4jMockitoAppender.getEvents());
  }
}
