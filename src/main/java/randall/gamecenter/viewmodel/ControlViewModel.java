package randall.gamecenter.viewmodel;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import randall.common.ui.Dialogs;
import randall.gamecenter.model.ConfigModel;
import randall.gamecenter.model.StartMode;
import randall.gamecenter.model.StartState;

import static randall.gamecenter.model.StartState.CANCEL_START;
import static randall.gamecenter.model.StartState.CANCEL_STOP;
import static randall.gamecenter.model.StartState.RUNNING;
import static randall.gamecenter.model.StartState.STARTING;
import static randall.gamecenter.model.StartState.STOPPED;
import static randall.gamecenter.model.StartState.STOPPING;

@Slf4j(topic = "randall")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class ControlViewModel {
  private final BooleanProperty databaseEnabled = new SimpleBooleanProperty(false);
  private final BooleanProperty accountEnabled = new SimpleBooleanProperty(false);
  private final BooleanProperty loggerEnabled = new SimpleBooleanProperty(false);
  private final BooleanProperty coreEnabled = new SimpleBooleanProperty(false);
  private final BooleanProperty runEnabled = new SimpleBooleanProperty(false);
  private final BooleanProperty roleEnabled = new SimpleBooleanProperty(false);
  private final BooleanProperty loginEnabled = new SimpleBooleanProperty(false);
  private final BooleanProperty topEnabled = new SimpleBooleanProperty(false);
  private final ObjectProperty<StartMode> startMode = new SimpleObjectProperty<>(StartMode.NORMAL);
  private final ObjectProperty<Integer> hours = new SimpleObjectProperty<>(0);
  private final BooleanProperty hoursDisable = new SimpleBooleanProperty(true);
  private final ObjectProperty<Integer> minutes = new SimpleObjectProperty<>(0);
  private final BooleanProperty minutesDisable = new SimpleBooleanProperty(true);
  private final ObjectProperty<StartState> startState = new SimpleObjectProperty<>(STOPPED);
  private final BooleanProperty startDisable = new SimpleBooleanProperty(true);

  private final CompositeDisposable disposable = new CompositeDisposable();

  private long runTime = 0;
  private final ConfigModel configModel;

  public void bindDatabase(CheckBox checkBox) {
    checkBox.selectedProperty().bindBidirectional(databaseEnabled);
  }

  public void bindAccount(CheckBox checkBox) {
    checkBox.selectedProperty().bindBidirectional(accountEnabled);
  }

  public void bindLogger(CheckBox checkBox) {
    checkBox.selectedProperty().bindBidirectional(loggerEnabled);
  }

  public void bindCore(CheckBox checkBox) {
    checkBox.selectedProperty().bindBidirectional(coreEnabled);
  }

  public void bindRun(CheckBox checkBox) {
    checkBox.selectedProperty().bindBidirectional(runEnabled);
  }

  public void bindRole(CheckBox checkBox) {
    checkBox.selectedProperty().bindBidirectional(roleEnabled);
  }

  public void bindLogin(CheckBox checkBox) {
    checkBox.selectedProperty().bindBidirectional(loginEnabled);
  }

  public void bindTop(CheckBox checkBox) {
    checkBox.selectedProperty().bindBidirectional(topEnabled);
  }

  public void bindStartMode(ComboBox<StartMode> comboBox) {
    comboBox.setItems(FXCollections.observableArrayList(StartMode.values()));
    comboBox.valueProperty().bindBidirectional(startMode);
    disposable.add(JavaFxObservable.valuesOf(startMode).subscribe(this::checkStartMode));
  }

  private void checkStartMode(StartMode startMode) {
    hoursDisable.setValue(StartMode.NORMAL.equals(startMode));
    minutesDisable.setValue(StartMode.NORMAL.equals(startMode));
  }

  public void bindHours(Spinner<Integer> spinner) {
    spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
    spinner.getValueFactory().valueProperty().bindBidirectional(hours);
    spinner.disableProperty().bindBidirectional(hoursDisable);
  }

  public void bindMinutes(Spinner<Integer> spinner) {
    spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
    spinner.getValueFactory().valueProperty().bindBidirectional(minutes);
    spinner.disableProperty().bindBidirectional(minutesDisable);
  }

  public void bindStartGame(Button button) {
    disposable.add(JavaFxObservable.valuesOf(startState)
        .map(StartState::toString)
        .subscribe(button::setText));
    button.disableProperty().bindBidirectional(startDisable);
  }

  private long computeRuntime(StartMode mode) {
    if (StartMode.DELAY.equals(mode)) {
      return Duration.ofHours(hours.get()).plus(Duration.ofMinutes(minutes.get())).toMillis();
    }
    if (StartMode.TIMING.equals(mode)) {
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime dateTime =
          LocalDateTime.of(now.toLocalDate(), LocalTime.of(hours.get(), minutes.get()));
      // 如果指定时间在现在之前，那么就认为是第二天的时刻，所以时间要加一天
      if (dateTime.isBefore(now)) {
        dateTime = dateTime.plusDays(1);
      }
      // System.currentTimeMillis() 方法获取的本来就是 UTC 时间戳
      return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
    return 0;
  }

  public void onStartGame() {
    String message = String.format("是否确认%s？", startState.get());
    switch (startState.get()) {
      case STOPPED:
      case CANCEL_START:
        Dialogs.confirm(message).ifPresent(buttonType -> startState.set(STARTING));
        break;
      case STARTING:
        Dialogs.confirm(message).ifPresent(buttonType -> startState.set(CANCEL_START));
        break;
      case RUNNING:
      case CANCEL_STOP:
        Dialogs.confirm(message).ifPresent(buttonType -> startState.set(STOPPING));
        break;
      case STOPPING:
        Dialogs.confirm(message).ifPresent(buttonType -> startState.set(CANCEL_STOP));
        break;
    }
  }

  private void cancelStopGame() {
    stopGameTimer.cancel();
    stopGameTimer = new Timer();
    startState = RUNNING;
    startGameButton.setText(share.textStopGame);
  }

  private void stopGame() {
    startGameButton.setText(share.textCancelStopGame);
    mainOutMessage("正在开始停止服务器...");
    // todo cancel task and do not new Timer
    checkRunTimer.cancel();
    checkRunTimer = new Timer();
    stopGameTimer.schedule(new StopGameTask(), 1000, 1000);
    gateStopped = false;
    startState = STOPPING;
  }

  private void cancelStartGame() {
    startGameTimer.cancel();
    startGameTimer = new Timer();
    startState = RUNNING;
    startGameButton.setText(share.textStopGame);
  }
}
