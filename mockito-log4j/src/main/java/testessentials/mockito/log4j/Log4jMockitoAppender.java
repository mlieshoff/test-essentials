package testessentials.mockito.log4j;

import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import org.apache.logging.log4j.core.Appender;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import testessentials.log4j.Log4jMockAppender;

public class Log4jMockitoAppender extends Log4jMockAppender {

  public Log4jMockitoAppender(Object testInstance) {
    super(testInstance);
  }

  @Override
  protected Appender createMockAppender() {
    return
            mock(Appender.class);
  }

  @Override
  protected void prepareAppenderWithMockBehaviour(Appender appender) {
    String appenderName =
        Log4jMockitoAppender.class.getSimpleName() + "_" + System.identityHashCode(this);
    lenient().when(appender.getName()).thenReturn(appenderName);
    lenient().when(appender.isStarted()).thenReturn(true);
    lenient().when(appender.isStopped()).thenReturn(false);
    lenient().doAnswer(AdditionalAnswers.answerVoid(this::addLogEvent)).when(appender).append(any());
  }

  @Override
  protected boolean isAnnotatedAsAppendable(Field field) {
    return super.isAnnotatedAsAppendable(field) || field.isAnnotationPresent(InjectMocks.class);
  }
}
