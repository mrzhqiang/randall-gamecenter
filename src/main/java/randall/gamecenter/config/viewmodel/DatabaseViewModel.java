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
import randall.gamecenter.config.model.Config;
import randall.gamecenter.util.IniLoader;

import static randall.common.ServerPath.DB_DIR;
import static randall.common.ServerPath.DB_NAME;
import static randall.common.ServerPath.Database.CONFIG_FILENAME;
import static randall.common.ServerPath.Database.SERVER_INFO_FILENAME;
import static randall.common.ServerPath.Database.USER_NAME_FILTER_FILENAME;
import static randall.common.ServerPath.Database.USER_NAME_FILTER_HEADER;
import static randall.common.ServerPath.IDS_ADDRESS;
import static randall.common.ServerPath.IDS_PORT;
import static randall.common.ServerPath.SERVER_NAME;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class DatabaseViewModel {
  public final IntegerProperty offsetPort = new SimpleIntegerProperty(0);

  private final Status loadDefaultStatus = new Status();
  private final CompositeDisposable disposable = new CompositeDisposable();

  private final Config config;

  @PreDestroy void onDestroy() {
    disposable.clear();
  }

  public void bindX(TextField field) {
    disposable.add(JavaFxObservable.valuesOf(config.database.xProperty())
        .map(Number::intValue)
        .map(String::valueOf)
        .observeOn(JavaFxScheduler.platform())
        .subscribe(field::setText));
  }

  public void bindY(TextField field) {
    disposable.add(JavaFxObservable.valuesOf(config.database.yProperty())
        .map(Number::intValue)
        .map(String::valueOf)
        .observeOn(JavaFxScheduler.platform())
        .subscribe(field::setText));
  }

  public void bindPort(TextField field) {
    disposable.add(JavaFxObservable.valuesOf(config.database.portProperty().add(offsetPort))
        .map(Number::intValue)
        .map(String::valueOf)
        .observeOn(JavaFxScheduler.platform())
        .subscribe(field::setText));
  }

  public void bindServer(TextField field) {
    disposable.add(JavaFxObservable.valuesOf(config.database.serverProperty().add(offsetPort))
        .map(Number::intValue)
        .map(String::valueOf)
        .observeOn(JavaFxScheduler.platform())
        .subscribe(field::setText));
  }

  public void bindEnabled(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(config.database.enabledProperty())
        .observeOn(JavaFxScheduler.platform())
        .subscribe(box::setSelected));
  }

  public void bindLoadDefault(Button button) {
    disposable.add(loadDefaultStatus.observe().subscribe(button::setDisable));
  }

  public void loadDefault() {
    loadDefaultStatus.running();
    config.loadDefault(config.database);
    loadDefaultStatus.finished();
  }

  public void refresh() {
    Path root = Paths.get(config.home.getPath(), config.database.getPath());
    try {
      Explorer.mkdir(root);
      Path data = root.resolve(ServerPath.DATABASE);
      Explorer.mkdir(data);
      Path iniFile = data.resolve(CONFIG_FILENAME);
      Explorer.create(iniFile);
      Ini ini = IniLoader.load(iniFile);
      String section = config.database.getSection();
      ini.put(section, SERVER_NAME, config.home.getName());
      ini.put(section, ServerPath.SERVER_ADDRESS, HostAddress.PRIMARY);
      ini.put(section, ServerPath.SERVER_PORT, config.database.getServer());
      ini.put(section, ServerPath.GATE_ADDRESS, HostAddress.ALL);
      ini.put(section, ServerPath.GATE_PORT, config.database.getPort());
      ini.put(section, IDS_ADDRESS, HostAddress.PRIMARY);
      ini.put(section, IDS_PORT, config.account.getServer());
      ini.put(section, DB_NAME, config.home.getDatabase());
      ini.put(section, DB_DIR, ServerPath.DATA_DIR);
      ini.store();
      Explorer.write(data.resolve(ServerPath.ADDRESS_TABLE_FILENAME), HostAddress.PRIMARY);
      String serverInfo = HostAddress.PRIMARY;
      if (config.run.isEnabled()) {
        serverInfo += String.format(" %s %d", config.home.getHost(), config.run.getPort());
      }
      serverInfo += System.lineSeparator();
      Explorer.write(data.resolve(SERVER_INFO_FILENAME), serverInfo);
      Explorer.write(data.resolve(USER_NAME_FILTER_FILENAME), USER_NAME_FILTER_HEADER);
    } catch (IOException e) {
      Dialogs.error("刷新数据库服务器配置文件出错！", e).show();
    }
  }
}
