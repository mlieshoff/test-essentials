package testessentials.log4j.assertj;

import org.apache.logging.log4j.Level;
import org.assertj.core.api.ListAssert;
import testessentials.log4j.LogAccessor;

import java.util.List;
import java.util.stream.Collectors;

public record LogAccessorAssert(LogAccessor logAccessor) {

  public static LogAccessorAssert assertThatFormattedLogMessagesFrom(LogAccessor logAccessor) {
    return new LogAccessorAssert(logAccessor);
  }

  public LogAssert forTrace() {
    return forLevel(Level.TRACE);
  }

  public LogAssert forLevel(Level level) {
    return new LogAssert(
        logAccessor.events().stream()
            .filter(logEvent -> logEvent.getLevel() == level)
            .map(logEvent -> logEvent.getMessage().getFormattedMessage())
            .collect(Collectors.toList()));
  }

  public static class LogAssert extends ListAssert<String> {
    public LogAssert(List<? extends String> actual) {
      super(actual);
    }
  }

  public LogAssert forDebug() {
    return forLevel(Level.DEBUG);
  }

  public LogAssert forInfo() {
    return forLevel(Level.INFO);
  }

  public LogAssert forError() {
    return forLevel(Level.ERROR);
  }

  public LogAssert forFatal() {
    return forLevel(Level.FATAL);
  }
}
