package com.github.mrzhqiang.randall.gamecenter.dialog;

import com.github.mrzhqiang.randall.gamecenter.util.Throwables;
import com.google.common.base.Preconditions;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * @author mrzhqiang
 */
public enum AlertDialog {
    ;

    public static void showInfo(String message) {
        showInfo(message, null);
    }

    public static void showInfo(String message, @Nullable String content) {
        Preconditions.checkNotNull(message, "message == null");
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("提示");
            alert.setHeaderText(message);
            alert.setContentText(content);
            alert.show();
        });
    }

    public static void showWarn(String message) {
        showWarn(message, null);
    }

    public static void showWarn(String message, @Nullable String content) {
        Preconditions.checkNotNull(message, "message == null");
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("警告");
            alert.setHeaderText(message);
            alert.setContentText(content);
            alert.show();
        });
    }

    public static void showError(Throwable error) {
        Preconditions.checkNotNull(error, "error == null");
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("异常");
            alert.setHeaderText("抱歉！运行程序出现错误。");
            alert.setContentText(Throwables.print(error));
            alert.show();
        });
    }

    public static Optional<ButtonType> waitConfirm(String message) {
        return waitConfirm(message, null);
    }

    public static Optional<ButtonType> waitConfirm(String message, @Nullable String content) {
        Preconditions.checkNotNull(message, "message == null");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("请确认");
        alert.setHeaderText(message);
        alert.setContentText(content);
        return alert.showAndWait().filter(ButtonType.OK::equals);
    }
}
