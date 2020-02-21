package randall.gamecenter.viewmodel.config;

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
import randall.common.Limit;
import randall.common.ServerPath;
import randall.gamecenter.model.Config;
import randall.gamecenter.model.config.IniLoader;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class AccountViewModel {
  public final IntegerProperty offsetPort = new SimpleIntegerProperty(0);

  private final Status loadDefaultStatus = new Status();
  private final CompositeDisposable disposable = new CompositeDisposable();

  private final Config config;

  @PreDestroy void onDestroy() {
    disposable.clear();
  }

  public void bindX(TextField field) {
    disposable.add(JavaFxObservable.valuesOf(config.account.xProperty())
        .map(Number::intValue)
        .map(String::valueOf)
        .observeOn(JavaFxScheduler.platform())
        .subscribe(field::setText));
  }

  public void bindY(TextField field) {
    disposable.add(JavaFxObservable.valuesOf(config.account.yProperty())
        .map(Number::intValue)
        .map(String::valueOf)
        .observeOn(JavaFxScheduler.platform())
        .subscribe(field::setText));
  }

  public void bindPort(TextField field) {
    disposable.add(JavaFxObservable.valuesOf(config.account.portProperty().add(offsetPort))
        .map(Number::intValue)
        .map(String::valueOf)
        .observeOn(JavaFxScheduler.platform())
        .subscribe(field::setText));
  }

  public void bindServer(TextField field) {
    disposable.add(JavaFxObservable.valuesOf(config.account.serverProperty().add(offsetPort))
        .map(Number::intValue)
        .map(String::valueOf)
        .observeOn(JavaFxScheduler.platform())
        .subscribe(field::setText));
  }

  public void bindMonitor(TextField field) {
    disposable.add(JavaFxObservable.valuesOf(config.account.monitorProperty().add(offsetPort))
        .map(Number::intValue)
        .map(String::valueOf)
        .observeOn(JavaFxScheduler.platform())
        .subscribe(field::setText));
  }

  public void bindEnabled(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(config.account.enabledProperty())
        .observeOn(JavaFxScheduler.platform())
        .subscribe(box::setSelected));
  }

  public void bindLoadDefault(Button button) {
    disposable.add(loadDefaultStatus.observe().subscribe(button::setDisable));
  }

  public void loadDefault() {
    loadDefaultStatus.running();
    config.loadDefault(config.account);
    loadDefaultStatus.finished();
  }

  public void refresh() {
    Path root = Paths.get(config.home.getPath(), config.account.getPath());
    try {
      Explorer.mkdir(root);
      Path data = root.resolve(ServerPath.ACCOUNT);
      Explorer.mkdir(data);
      Path iniFile = data.resolve(ServerPath.Account.CONFIG_FILENAME);
      Explorer.create(iniFile);
      Ini ini = IniLoader.load(iniFile);
      String section = config.account.getSection();
      ini.put(section, ServerPath.SERVER_ADDRESS, HostAddress.ALL);
      ini.put(section, ServerPath.SERVER_PORT, config.account.getServer());
      ini.put(section, ServerPath.GATE_ADDRESS, HostAddress.ALL);
      ini.put(section, ServerPath.GATE_PORT, config.account.getPort());
      ini.put(section, ServerPath.MONITOR_ADDRESS, HostAddress.ALL);
      ini.put(section, ServerPath.MONITOR_PORT, config.account.getMonitor());
      ini.put(section, ServerPath.CLOSE_WUXING, config.home.isWuxing());
      ini.put(section, ServerPath.Account.ID_DIR, ServerPath.DATA_DIR);
      ini.put(section, ServerPath.Account.LOGGER_DIR, ServerPath.LOGGER_DIR);
      ini.store();
      Explorer.write(data.resolve(ServerPath.Account.SERVER_ADDRESS_FILENAME), HostAddress.PRIMARY);
      String name = config.home.getName();
      String content = String.format("%s %s %d", name, name, Limit.MAX_ONLINE_USER);
      Explorer.write(data.resolve(ServerPath.Account.USER_LIMIT_FILENAME), content);
      String addressTable = HostAddress.PRIMARY;
      if (config.role.isEnabled()) {
        addressTable += String.format(" %s %d", config.home.getHost(), config.role.getPort());
      }
      addressTable += System.lineSeparator();
      Explorer.write(data.resolve(ServerPath.ADDRESS_TABLE_FILENAME), addressTable);
      Explorer.mkdir(data.resolve(ServerPath.LOGGER_DIR));
      Explorer.mkdir(data.resolve(ServerPath.DATA_DIR));
    } catch (IOException e) {
      Dialogs.error("刷新登陆服务器配置文件出错！", e).show();
    }
  }
}
