package randall.gamecenter.model;

import io.reactivex.Observable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ini4j.Ini;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import randall.gamecenter.model.config.Config;
import randall.gamecenter.model.profile.Profile;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Getter
@Component
public class ConfigModel {
  private static final String FILENAME = "Config.ini";

  private final Config config;
  private final Profile profile;
  private final IniModel iniModel;

  public Observable<Config> load() {
    Path path = Paths.get(config.getHome().getPath(), FILENAME);
    return iniModel.load(path)
        .doOnNext(config.getHome()::read)
        .doOnNext(config.getDatabase()::read)
        .doOnNext(config.getAccount()::read)
        .doOnNext(config.getCore()::read)
        .doOnNext(config.getLogger()::read)
        .doOnNext(config.getRun()::read)
        .doOnNext(config.getRole()::read)
        .doOnNext(config.getLogin()::read)
        .doOnNext(config.getTop()::read)
        .map(ini -> config)
        .subscribeOn(JavaFxScheduler.platform());
  }

  public Observable<Config> save() {
    Path path = Paths.get(config.getHome().getPath(), FILENAME);
    return iniModel.load(path)
        .doOnNext(config.getHome()::write)
        .doOnNext(config.getDatabase()::write)
        .doOnNext(config.getAccount()::write)
        .doOnNext(config.getCore()::write)
        .doOnNext(config.getLogger()::write)
        .doOnNext(config.getRun()::write)
        .doOnNext(config.getRole()::write)
        .doOnNext(config.getLogin()::write)
        .doOnNext(config.getTop()::write)
        .doOnNext(Ini::store)
        .map(ini -> config)
        .subscribeOn(JavaFxScheduler.platform());
  }
}
