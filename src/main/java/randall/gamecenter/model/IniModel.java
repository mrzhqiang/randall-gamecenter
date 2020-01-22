package randall.gamecenter.model;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.ini4j.Ini;
import org.ini4j.Wini;
import org.springframework.stereotype.Component;
import randall.common.util.IOHelper;

@Slf4j(topic = "randall")
@Component
public class IniModel {

  public Observable<Ini> load(Path filePath) {
    return Observable.just(filePath)
        .subscribeOn(Schedulers.io())
        .doOnNext(this::notExistsAndCreate)
        .map(Path::toFile)
        .map(Wini::new);
  }

  private void notExistsAndCreate(Path path) {
    if (Files.notExists(path)) {
      IOHelper.mkdir(path.getParent());
      IOHelper.create(path);
    }
  }
}
