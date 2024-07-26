package testessentials.log4j;

import java.util.List;
import org.apache.logging.log4j.core.LogEvent;

public record LogAccessor(List<LogEvent> events) {}
