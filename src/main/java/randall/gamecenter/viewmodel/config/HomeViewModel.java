package randall.gamecenter.viewmodel.config;

import helper.javafx.model.Status;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import randall.gamecenter.model.Config;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class HomeViewModel {
  private final Status loadDefaultStatus = new Status();
  private final CompositeDisposable disposable = new CompositeDisposable();

  private final Config config;

  @PreDestroy void onDestroy() {
    disposable.clear();
  }

  public void bindPath(TextField field) {
    disposable.add(JavaFxObservable.valuesOf(config.home.pathProperty())
        .observeOn(JavaFxScheduler.platform())
        .subscribe(field::setText));
  }

  public void bindName(TextField field) {
    disposable.add(JavaFxObservable.valuesOf(config.home.nameProperty())
        .observeOn(JavaFxScheduler.platform())
        .subscribe(field::setText));
  }

  public void bindDatabase(TextField field) {
    disposable.add(JavaFxObservable.valuesOf(config.home.databaseProperty())
        .observeOn(JavaFxScheduler.platform())
        .subscribe(field::setText));
  }

  public void bindHost(TextField field) {
    disposable.add(JavaFxObservable.valuesOf(config.home.hostProperty())
        .observeOn(JavaFxScheduler.platform())
        .subscribe(field::setText));
  }

  public void bindBackup(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(config.home.backupProperty())
        .observeOn(JavaFxScheduler.platform())
        .subscribe(box::setSelected));
  }

  public void bindWuxing(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(config.home.wuxingProperty())
        .observeOn(JavaFxScheduler.platform())
        .subscribe(box::setSelected));
  }

  public void bindLoadDefault(Button button) {
    disposable.add(loadDefaultStatus.observe().subscribe(button::setDisable));
  }

  public void loadDefault() {
    loadDefaultStatus.running();
    config.loadDefault(config.home);
    loadDefaultStatus.finished();
  }
}
