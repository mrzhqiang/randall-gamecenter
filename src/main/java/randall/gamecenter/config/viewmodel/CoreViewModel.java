package randall.gamecenter.config.viewmodel;

import helper.Explorer;
import helper.javafx.model.Status;
import helper.javafx.ui.Dialogs;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ini4j.Ini;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import randall.common.HostAddress;
import randall.common.ServerPath;
import randall.common.ServerPath.Core;
import randall.gamecenter.config.model.Config;
import randall.gamecenter.util.IniLoader;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class CoreViewModel {
  public final IntegerProperty offsetPort = new SimpleIntegerProperty(0);

  private final Status loadDefaultStatus = new Status();
  private final CompositeDisposable disposable = new CompositeDisposable();

  private final Config config;

  @PreDestroy void onDestroy() {
    disposable.clear();
  }

  public void bindX(TextField field) {
    disposable.add(JavaFxObservable.valuesOf(config.core.xProperty())
        .map(Number::intValue)
        .map(String::valueOf)
        .observeOn(JavaFxScheduler.platform())
        .subscribe(field::setText));
  }

  public void bindY(TextField field) {
    disposable.add(JavaFxObservable.valuesOf(config.core.yProperty())
        .map(Number::intValue)
        .map(String::valueOf)
        .observeOn(JavaFxScheduler.platform())
        .subscribe(field::setText));
  }

  public void bindPort(TextField field) {
    disposable.add(JavaFxObservable.valuesOf(config.core.portProperty().add(offsetPort))
        .map(Number::intValue)
        .map(String::valueOf)
        .observeOn(JavaFxScheduler.platform())
        .subscribe(field::setText));
  }

  public void bindServer(TextField field) {
    disposable.add(JavaFxObservable.valuesOf(config.core.serverProperty().add(offsetPort))
        .map(Number::intValue)
        .map(String::valueOf)
        .observeOn(JavaFxScheduler.platform())
        .subscribe(field::setText));
  }

  public void bindEnabled(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(config.core.enabledProperty())
        .observeOn(JavaFxScheduler.platform())
        .subscribe(box::setSelected));
  }

  public void bindLoadDefault(Button button) {
    disposable.add(loadDefaultStatus.observe().subscribe(button::setDisable));
  }

  public void loadDefault() {
    loadDefaultStatus.running();
    config.loadDefault(config.core);
    loadDefaultStatus.finished();
  }

  public void refresh() {
    Path root = Paths.get(config.home.getPath(), config.core.getPath());
    try {
      Explorer.mkdir(root);
      Path data = root.resolve(ServerPath.CORE);
      Explorer.mkdir(data);
      Path iniFile = data.resolve(Core.CONFIG_FILENAME);
      Explorer.create(iniFile);
      Ini ini = IniLoader.load(iniFile);
      String section = config.core.getSection();
      ini.put(section, ServerPath.SERVER_NAME, config.home.getName());
      ini.put(section, ServerPath.DB_NAME, config.home.getDatabase());
      ini.put(section, ServerPath.GATE_ADDRESS, HostAddress.ALL);
      ini.put(section, ServerPath.GATE_PORT, config.core.getPort());
      ini.put(section, ServerPath.DB_ADDRESS, HostAddress.PRIMARY);
      ini.put(section, ServerPath.DB_PORT, config.database.getServer());
      ini.put(section, ServerPath.IDS_ADDRESS, HostAddress.PRIMARY);
      ini.put(section, ServerPath.IDS_PORT, config.account.getServer());
      ini.put(section, Core.MSG_SERVER_ADDRESS, HostAddress.ALL);
      ini.put(section, Core.MSG_SERVER_PORT, config.core.getServer());
      ini.put(section, Core.LOGGER_SERVER_ADDRESS, HostAddress.PRIMARY);
      ini.put(section, Core.LOGGER_SERVER_PORT, config.logger.getPort());
      ini.put(section, ServerPath.CLOSE_WUXING, config.home.isWuxing());

      ini.put(ServerPath.SHARE, Core.GUILD_DIR, Core.GUILD_DIR_VALUE);
      ini.put(ServerPath.SHARE, Core.GUILD_FILE, Core.GUILD_FILE_VALUE);
      ini.put(ServerPath.SHARE, Core.CONNECT_LOGGER_DIR, Core.CONNECT_LOGGER_DIR_VALUE);
      ini.put(ServerPath.SHARE, Core.CASTLE_DIR, Core.CASTLE_DIR_VALUE);
      ini.put(ServerPath.SHARE, Core.CASTLE_FILE, Core.CASTLE_FILE_VALUE);
      ini.put(ServerPath.SHARE, Core.GAME_DATA_DIR, Core.GAME_DATA_DIR_VALUE);
      ini.put(ServerPath.SHARE, Core.ENVIR_DIR, Core.ENVIR_DIR_VALUE);
      ini.put(ServerPath.SHARE, Core.MAP_DIR, Core.MAP_DIR_VALUE);
      ini.put(ServerPath.SHARE, Core.NOTICE_DIR, Core.NOTICE_DIR_VALUE);
      ini.put(ServerPath.SHARE, Core.LOGGER_DIR, Core.LOGGER_DIR_VALUE);
      ini.put(ServerPath.SHARE, Core.EMAIL_DIR, Core.EMAIL_DIR_VALUE);
      ini.store();

      Explorer.mkdir(data.resolve(Core.GUILD_BASE));
      Explorer.mkdir(data.resolve(Core.GUILD_DIR_VALUE));
      Explorer.mkdir(data.resolve(Core.CONNECT_LOGGER_DIR_VALUE));
      Explorer.mkdir(data.resolve(Core.CASTLE_DIR_VALUE));
      Explorer.mkdir(data.resolve(Core.ENVIR_DIR_VALUE));
      Explorer.mkdir(data.resolve(Core.MAP_DIR_VALUE));
      Explorer.mkdir(data.resolve(Core.NOTICE_DIR_VALUE));
      Explorer.mkdir(data.resolve(Core.LOGGER_DIR_VALUE));
      Explorer.mkdir(data.resolve(Core.EMAIL_DIR_VALUE));
      Explorer.write(data.resolve(ServerPath.SERVER_TABLE_FILENAME), HostAddress.PRIMARY);
    } catch (IOException e) {
      Dialogs.error("刷新核心服务器配置出错！", e).show();
    }
  }
}
