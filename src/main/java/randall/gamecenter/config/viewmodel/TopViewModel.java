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

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class TopViewModel {
  private final Status loadDefaultStatus = new Status();
  private final CompositeDisposable disposable = new CompositeDisposable();

  private final Config config;

  @PreDestroy void onDestroy() {
    disposable.clear();
  }

  public void bindX(TextField field) {
    disposable.add(JavaFxObservable.valuesOf(config.top.xProperty())
        .map(Number::intValue)
        .map(String::valueOf)
        .observeOn(JavaFxScheduler.platform())
        .subscribe(field::setText));
  }

  public void bindY(TextField field) {
    disposable.add(JavaFxObservable.valuesOf(config.top.yProperty())
        .map(Number::intValue)
        .map(String::valueOf)
        .observeOn(JavaFxScheduler.platform())
        .subscribe(field::setText));
  }

  public void bindEnabled(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(config.top.enabledProperty())
        .observeOn(JavaFxScheduler.platform())
        .subscribe(box::setSelected));
  }

  public void bindLoadDefault(Button button) {
    disposable.add(loadDefaultStatus.observe().subscribe(button::setDisable));
  }

  public void loadDefault() {
    loadDefaultStatus.running();
    config.loadDefault(config.top);
    loadDefaultStatus.finished();
  }

  public void refresh() {
    Path root = Paths.get(config.home.getPath(), config.top.getPath());
    try {
      Explorer.mkdir(root);
      Path data = root.resolve(ServerPath.TOP);
      Explorer.mkdir(data);
      Path iniFile = data.resolve(ServerPath.Top.CONFIG_FILENAME);
      Explorer.create(iniFile);
      Ini ini = IniLoader.load(iniFile);
      String section = config.top.getSection();
      ini.put(section, ServerPath.TITLE, config.home.getName());
      ini.put(section, ServerPath.SERVER_ADDRESS, HostAddress.PRIMARY);
      ini.put(section, ServerPath.SERVER_PORT, config.database.getPort());
      ini.store();
    } catch (IOException e) {
      Dialogs.error("刷新排行榜插件配置文件出错！", e).show();
    }
  }
}
