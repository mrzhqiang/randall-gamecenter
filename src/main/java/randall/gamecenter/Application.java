package randall.gamecenter;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import helper.javafx.ui.Dialogs;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class Application extends AbstractJavaFxApplicationSupport {
  private static final String TITLE = "游戏控制器";

  public static void main(String[] args) {
    log.info("准备启动程序.");
    launch(Application.class, ApplicationView.class, args);
  }

  @Override public void beforeShowingSplash(Stage splashStage) {
    super.beforeShowingSplash(splashStage);
    log.info("准备显示闪屏页面.");
  }

  @Override public void beforeInitialView(Stage stage, ConfigurableApplicationContext ctx) {
    log.info("准备初始化视图.");
    stage.setTitle(TITLE);
    stage.setOnCloseRequest(event ->
        Dialogs.confirm("是否确认关闭控制台？")
            .ifPresent(buttonType -> Platform.exit()));
  }

  @Override public void stop() {
    Schedulers.shutdown();
  }
}
