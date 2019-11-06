package randall.gamecenter.util;

/**
 * 环境工具。
 *
 * @author mrzhqiang
 */
public enum Environments {
  ;

  public static final String DEFAULT_DEBUG_DIRECTORY = "./sample";

  public static boolean isDebug() {
    // 目前只检查 IDEA 中的调试模式
    return Boolean.parseBoolean(
        System.getProperty("intellij.debug.agent", Boolean.FALSE.toString()));
  }

  public static String homework() {
    return System.getProperty("user.dir");
  }

  public static String debugWork() {
    return System.getProperty("debug.dir", DEFAULT_DEBUG_DIRECTORY);
  }
}
