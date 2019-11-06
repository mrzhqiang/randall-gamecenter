package randall.gamecenter.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 堆栈工具。
 *
 * @author mrzhqiang
 */
public enum StackTraces {
  ;

  /**
   * 当前堆栈信息。
   */
  public static String ofCurrent() {
    StringBuilder builder = new StringBuilder();
    for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
      if (builder.length() > 0) {
        builder.append("\r\n");
      }
      builder.append(element.toString());
    }
    return builder.toString();
  }

  /**
   * 异常堆栈信息。
   */
  public static String of(Throwable e) {
    if (!Environments.isDebug()) {
      return e != null ? e.getMessage() : "";
    }
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    return sw.toString();
  }
}
