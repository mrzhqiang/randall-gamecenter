package randall.gamecenter;

import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import randall.gamecenter.util.Monitor;

public final class App extends Application {
  private static final String TITLE = "游戏控制器";
  private static final URL FXML = App.class.getResource("application.fxml");
  private static final URL CSS = App.class.getResource("application.css");
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
