package testessentials.core;

public class Debug {

  private static final String FORMAT = "[%s] [%s]: %s (%s): - %s";

  public static boolean enabled = false;

  public static void log(String message, Object caller, Object... params) {
    if (enabled) {
      System.out.printf(
          FORMAT + "%n",
          Thread.currentThread().getName(),
          Thread.currentThread().threadId(),
          System.identityHashCode(caller),
          caller.getClass().getSimpleName(),
          String.format(message, params));
    }
  }
}
