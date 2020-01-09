package randall.gamecenter;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import randall.common.ui.Dialogs;

@SpringBootApplication
public class GameCenterApplication extends AbstractJavaFxApplicationSupport {
  private static final String TITLE = "游戏控制器";

  public static void main(String[] args) {
    launch(GameCenterApplication.class, GameCenterView.class, args);
  }

  @Override public void beforeInitialView(Stage stage, ConfigurableApplicationContext ctx) {
    GameCenterView view = ctx.getBean(GameCenterView.class);
    GameCenterController controller = (GameCenterController) view.getPresenter();
    stage.setOnCloseRequest(event -> {
      if (controller.startState == Share.RUNNING_STATE) {
        Dialogs.confirm("游戏服务器正在运行，是否停止游戏服务器？")
            .ifPresent(buttonType -> controller.onStartGameClicked());
        event.consume();
        return;
      }
      Dialogs.confirm("是否确认关闭控制台？")
          .ifPresent(buttonType -> Platform.exit());
    });
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    super.start(primaryStage);
    setTitle(TITLE);
  }
}
