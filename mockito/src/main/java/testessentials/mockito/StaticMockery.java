package testessentials.mockito;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.ScopedMock;

public class StaticMockery {

  private final Map<Class<?>, MockedStatic<?>> mockedStatics = new ConcurrentHashMap<>();

  public <T> MockedStatic<T> create(Class<?> clazz) {
    return (MockedStatic<T>)
        mockedStatics.computeIfAbsent(clazz, aClass -> Mockito.mockStatic(clazz));
  }

  public void closeAll() {
    mockedStatics.values().forEach(ScopedMock::close);
    mockedStatics.clear();
  }
}
