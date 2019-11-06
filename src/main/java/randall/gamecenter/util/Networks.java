package randall.gamecenter.util;

import com.google.common.base.Preconditions;
import java.util.regex.Pattern;

/**
 * 网络工具。
 *
 * @author mrzhqiang
 */
public enum Networks {
  ;

  public static final int MAX_PORT = 65535;
  public static final int MIN_PORT = 0;

  public static boolean isPort(int port) {
    return port >= MIN_PORT && port <= MAX_PORT;
  }

  private static final String REGEX_V4_ADDRESS = "([0-9]{1,3}(\\.[0-9]{1,3}){3})";
  private static final String REGEX_V6_ADDRESS = "[a-f0-9]{1,4}(:[a-f0-9]{1,4}){7}";

  public static boolean isAddressV4(String address) {
    Preconditions.checkNotNull(address, "address == null");
    if (!Pattern.matches(REGEX_V4_ADDRESS, address)) {
      return false;
    }
    String[] split = address.split("\\.");
    for (String value : split) {
      try {
        int i = Integer.parseInt(value);
        if (i < 0 || i > 255) {
          return false;
        }
      } catch (Exception ignore) {
        return false;
      }
    }
    return true;
  }

  public static boolean isAddressV6(String address) {
    Preconditions.checkNotNull(address, "address == null");
    // todo 更严格的 IPv6 检查
    return Pattern.matches(REGEX_V6_ADDRESS, address);
  }
}
