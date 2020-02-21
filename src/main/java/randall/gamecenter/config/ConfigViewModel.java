package randall.gamecenter.config;

import helper.javafx.model.Status;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import randall.gamecenter.config.viewmodel.AccountViewModel;
import randall.gamecenter.config.model.Config;
import randall.gamecenter.config.viewmodel.CoreViewModel;
import randall.gamecenter.config.viewmodel.DatabaseViewModel;
import randall.gamecenter.config.viewmodel.HomeViewModel;
import randall.gamecenter.config.viewmodel.LoggerViewModel;
import randall.gamecenter.config.viewmodel.LoginViewModel;
import randall.gamecenter.config.viewmodel.RoleViewModel;
import randall.gamecenter.config.viewmodel.RunViewModel;
import randall.gamecenter.config.viewmodel.TopViewModel;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class ConfigViewModel {
  public final HomeViewModel homeVM;
  public final DatabaseViewModel databaseVM;
  public final AccountViewModel accountVM;
  public final CoreViewModel coreVM;
  public final LoggerViewModel loggerVM;
  public final RunViewModel runVM;
  public final RoleViewModel roleVM;
  public final LoginViewModel loginVM;
  public final TopViewModel topVM;

  private final Status reloadStatus = new Status();
  private final Status refreshStatus = new Status();
  private final Status saveStatus = new Status();
  private final CompositeDisposable disposable = new CompositeDisposable();

  private final Config config;

  @PreDestroy void onDestroy() {
    disposable.clear();
  }

  public void bindReload(Button button) {
    disposable.add(reloadStatus.observe().subscribe(button::setDisable));
  }

  public void bindPortOffset(Spinner<Integer> spinner) {
    spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99, 0));
    disposable.add(JavaFxObservable.valuesOf(spinner.getValueFactory().valueProperty())
        .observeOn(JavaFxScheduler.platform())
        .subscribe(this::allPortOffset));
  }

  private void allPortOffset(Integer integer) {
    databaseVM.offsetPort.setValue(integer);
    accountVM.offsetPort.setValue(integer);
    coreVM.offsetPort.setValue(integer);
    loggerVM.offsetPort.setValue(integer);
    runVM.offsetPort.setValue(integer);
    roleVM.offsetPort.setValue(integer);
    loginVM.offsetPort.setValue(integer);
  }

  public void reloadAll() {
    reloadStatus.running();
    config.loadAll();
    reloadStatus.finished();
  }

  public void bindRefresh(Button button) {
    disposable.add(refreshStatus.observe().subscribe(button::setDisable));
  }

  public void bindSave(Button button) {
    disposable.add(saveStatus.observe().subscribe(button::setDisable));
  }

  public void refresh() {
    databaseVM.refresh();
    accountVM.refresh();
    coreVM.refresh();
    loggerVM.refresh();
    runVM.refresh();
    roleVM.refresh();
    loginVM.refresh();
    topVM.refresh();
  }

  public void save() {
    config.saveAll();
  }

  public void loadData() {
    config.init();
  }
}
