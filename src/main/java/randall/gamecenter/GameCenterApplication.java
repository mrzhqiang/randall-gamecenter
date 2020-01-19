package randall.gamecenter;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import randall.common.ui.Dialogs;
import randall.gamecenter.controller.GameCenterController;
import randall.gamecenter.model.Share;

@Slf4j(topic = "randall")
@EnableScheduling
@SpringBootApplication
public class GameCenterApplication extends AbstractJavaFxApplicationSupport {
  private static final String TITLE = "游戏控制器";

  public static void main(String[] args) {
    log.info("准备启动程序.");
    launch(GameCenterApplication.class, GameCenterView.class, args);
  }

  @Override public void beforeShowingSplash(Stage splashStage) {
    super.beforeShowingSplash(splashStage);
    log.info("准备显示闪屏页面.");
  }

  @Override public void beforeInitialView(Stage stage, ConfigurableApplicationContext ctx) {
    log.info("准备初始化视图.");
    stage.setTitle(TITLE);
    stage.setOnCloseRequest(event -> {
      // 一定要在这里进行 Bean 的获取，因为在初始化之前，Bean 不存在
      GameCenterView view = ctx.getBean(GameCenterView.class);
      GameCenterController controller = (GameCenterController) view.getPresenter();
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
}
