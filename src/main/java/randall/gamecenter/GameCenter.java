package randall.gamecenter;

import java.net.URL;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import randall.gamecenter.util.Dialogs;
import randall.gamecenter.util.Monitor;

public final class GameCenter extends Application {
  private static final String TITLE = "游戏控制器";
  private static final URL FXML = GameCenter.class.getResource("application.fxml");
  private static final URL CSS = GameCenter.class.getResource("application.css");
  private Controller controller;

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle(TITLE);
    Monitor monitor = Monitor.getInstance();
    monitor.record("准备启动程序..");
    try {
      FXMLLoader loader = new FXMLLoader(FXML);
      Scene scene = new Scene(loader.load());
      scene.getStylesheets().add(CSS.toExternalForm());
      primaryStage.setScene(scene);
      controller = loader.getController();
      primaryStage.setOnCloseRequest(event -> {
        if (controller.startState == Share.RUNNING_STATE) {
          Dialogs.confirm("游戏服务器正在运行，是否停止游戏服务器？")
              .ifPresent(buttonType -> controller.onStartGameClicked());
          event.consume();
          return;
        }
        Dialogs.confirm("是否确认关闭控制台？")
            .ifPresent(buttonType -> {
              controller.onDestroy();
              Platform.exit();
            });
      });
      primaryStage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
    monitor.report("程序启动完毕！");
  }

  @Override
  public void stop() {
    controller.onDestroy();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
