package randall.gamecenter.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对话框工具。
 *
 * @author mrzhqiang
 */
public enum Dialogs {
  ;

  private static final Logger LOGGER = LoggerFactory.getLogger("randall");

  public static Alert info(String message) {
    return info(message, null);
  }

  public static Alert info(String message, @Nullable String content) {
    Preconditions.checkNotNull(message, "message == null");
    LOGGER.info(message);
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("提示");
    alert.setHeaderText(message);
    if (content != null) {
      alert.setContentText(content);
    }
    return alert;
  }

  public static Alert warn(String message) {
    return warn(message, null);
  }

  public static Alert warn(String message, @Nullable String content) {
    Preconditions.checkNotNull(message, "message == null");
    LOGGER.warn(message);
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle("警告");
    alert.setHeaderText(message);
    if (content != null) {
      alert.setContentText(content);
    }
    return alert;
  }

  public static Alert error(Throwable error) {
    return error(null, error);
  }

  public static Alert error(@Nullable String message, Throwable cause) {
    Preconditions.checkNotNull(cause, "cause == null");
    String errorMsg = Strings.isNullOrEmpty(message) ? "抱歉！程序出现未知错误.." : message;
    LOGGER.error(errorMsg, cause);
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("错误");
    alert.setHeaderText(errorMsg);
    alert.setContentText(StackTraces.of(cause));
    return alert;
  }

  public static Optional<ButtonType> confirm(String message) {
    return confirm(message, null);
  }

  public static Optional<ButtonType> confirm(String message, @Nullable String content) {
    Preconditions.checkNotNull(message, "message == null");
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("请确认");
    alert.setHeaderText(message);
    if (content != null) {
      alert.setContentText(content);
    }
    return alert.showAndWait().filter(ButtonType.OK::equals);
  }
}
