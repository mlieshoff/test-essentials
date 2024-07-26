package testessentials.mockito.testng;

import static org.mockito.MockitoAnnotations.openMocks;

public class AutoCloseableHandler {

  private static final ThreadLocal<Context> THREAD_LOCAL = new ThreadLocal<>();

  public static void open(Object testInstance) {
    Context context = THREAD_LOCAL.get();
    if (context != null) {
      throw new IllegalStateException("Already opened.");
    }
    context = new Context();
    context.autoCloseable = openMocks(testInstance);
    THREAD_LOCAL.set(context);
  }

  public static void close() throws Exception {
    Context context = THREAD_LOCAL.get();
    if (context == null) {
      throw new IllegalStateException("Not opened.");
    }
    context.autoCloseable.close();
    THREAD_LOCAL.remove();
  }

  private static class Context {
    private AutoCloseable autoCloseable;
  }
}
