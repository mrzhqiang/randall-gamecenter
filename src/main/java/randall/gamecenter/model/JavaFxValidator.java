package randall.gamecenter.model;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import helper.Networks;
import helper.javafx.ui.Dialogs;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import javafx.scene.control.TextField;

public enum JavaFxValidator {
  ;

  public static Optional<String> verifyName(TextField field) {
    String name = field.getText().trim();
    if (Strings.isNullOrEmpty(name)) {
      Dialogs.warn("[名称] 输入为空，请检查！").show();
      field.requestFocus();
      return Optional.empty();
    }
    return Optional.of(name);
  }

  public static Optional<String> verifyDirectory(TextField field) {
    String directory = field.getText().trim();
    if (Strings.isNullOrEmpty(directory)) {
      Dialogs.warn("[目录] 输入为空，请检查！").show();
      field.requestFocus();
      return Optional.empty();
    }
    if (!directory.endsWith(File.separator)) {
      directory += File.separator;
    }
    if (Files.notExists(Paths.get(directory))) {
      Dialogs.warn("[目录] 不存在，请检查！").show();
      field.requestFocus();
      return Optional.empty();
    }
    return Optional.of(directory);
  }

  public static Optional<String> verifyAddress(TextField field) {
    String address = field.getText().trim();
    if (Strings.isNullOrEmpty(address)) {
      Dialogs.warn("[地址] 输入为空，请检查！").show();
      field.requestFocus();
      return Optional.empty();
    }
    if (!Networks.isAddressV4(address)) {
      Dialogs.warn("[地址] 输入不合法，请检查！").show();
      field.requestFocus();
      return Optional.empty();
    }
    return Optional.of(address);
  }

  public static Optional<Integer> verifyPosition(TextField field) {
    String text = field.getText().trim();
    if (Strings.isNullOrEmpty(text)) {
      Dialogs.warn("[坐标] 输入为空，请检查！").show();
      field.requestFocus();
      return Optional.empty();
    }
    if (!CharMatcher.inRange('0', '9').matchesAllOf(text)) {
      Dialogs.warn("[坐标] 输入无效，请检查！").show();
      field.requestFocus();
      return Optional.empty();
    }
    return Optional.of(Integer.parseInt(text));
  }

  public static Optional<Integer> verifyPort(TextField field) {
    String text = field.getText().trim();
    if (Strings.isNullOrEmpty(text)) {
      Dialogs.warn("[端口] 输入为空，请检查！").show();
      field.requestFocus();
      return Optional.empty();
    }
    if (!CharMatcher.inRange('0', '9').matchesAllOf(text)) {
      Dialogs.warn("[端口] 不是有效数字，请检查！").show();
      field.requestFocus();
      return Optional.empty();
    }
    int port = Integer.parseInt(text);
    if (!Networks.isPort(port)) {
      Dialogs.warn("[端口] 输入不合法，请检查！").show();
      field.requestFocus();
      return Optional.empty();
    }
    return Optional.of(port);
  }
}
