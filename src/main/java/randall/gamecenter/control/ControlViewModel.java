package randall.gamecenter.control;

import helper.javafx.model.Console;
import helper.javafx.model.Status;
import helper.javafx.ui.Dialogs;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import randall.gamecenter.control.viewmodel.ProgramViewModel;
import randall.gamecenter.control.model.ServerState;
import randall.gamecenter.control.viewmodel.StartModeViewModel;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class ControlViewModel {
  public final ProgramViewModel programVM;
  public final StartModeViewModel startModeVM;

  private final Console console = new Console();
  private final Status startStatus = new Status();
  private final ObjectProperty<ServerState> serverState = new SimpleObjectProperty<>();

  private final CompositeDisposable disposable = new CompositeDisposable();

  private Disposable startTask;
  private Disposable stopTask;
  private Disposable runningTask;

  @PreDestroy void onDestroy() {
    disposable.clear();
    if (startTask != null) {
      startTask.dispose();
    }
    if (stopTask != null) {
      stopTask.dispose();
    }
    if (runningTask != null) {
      runningTask.dispose();
    }
  }

  public void bindConsole(TextArea area) {
    disposable.add(console.observe().subscribe(area::appendText));
    area.clear();
  }

  public void bindStart(Button button) {
    disposable.add(startStatus.observe().subscribe(button::setDisable));
    disposable.add(JavaFxObservable.valuesOf(serverState)
        .map(ServerState::text)
        .observeOn(JavaFxScheduler.platform())
        .subscribe(button::setText));
  }

  public void startServer() {
    startStatus.running();
    ServerState state = serverState.get();
    Optional<ButtonType> confirm = Dialogs.confirm(String.format("是否%s？", state.text()));
    switch (state) {
      case STOPPED:
      case CANCEL_START:
        confirm.ifPresent(buttonType -> attemptStart());
        break;
      case STARTING:
        confirm.ifPresent(buttonType -> cancelStart());
        break;
      case RUNNING:
      case CANCEL_STOP:
        confirm.ifPresent(buttonType -> attemptStop());
        break;
      case STOPPING:
        confirm.ifPresent(buttonType -> cancelStop());
        break;
    }
  }

  public boolean stopped() {
    return ServerState.STOPPED.equals(serverState.get());
  }

  private void cancelStop() {
    serverState.setValue(ServerState.CANCEL_STOP);
    startTask.dispose();
    startStatus.finished();
  }

  private void attemptStop() {
    if (stopTask != null && !stopTask.isDisposed()) {
      stopTask.dispose();
    }
    serverState.setValue(ServerState.STOPPING);
    stopTask = Observable.interval(1, 1, TimeUnit.SECONDS)
        .flatMap(aLong -> programVM.stop().doOnNext(console::log))
        .filter(s -> programVM.allStopped())
        .doOnNext(s -> serverState.setValue(ServerState.STOPPED))
        .observeOn(JavaFxScheduler.platform())
        .subscribe(s -> stopTask.dispose(), Dialogs::error);
    startStatus.finished();
  }

  private void cancelStart() {
    serverState.setValue(ServerState.CANCEL_START);
    startTask.dispose();
    startStatus.finished();
  }

  private void attemptStart() {
    programVM.update();
    if (startTask != null && !startTask.isDisposed()) {
      startTask.dispose();
    }
    serverState.setValue(ServerState.STARTING);
    startModeVM.computeStatTime();
    startTask = Observable.interval(1, 1, TimeUnit.SECONDS)
        .filter(aLong -> startModeVM.timeUp())
        .flatMap(aLong -> programVM.start().doOnNext(console::log))
        .filter(s -> programVM.allStarted())
        .doOnNext(s -> serverState.setValue(ServerState.RUNNING))
        .observeOn(JavaFxScheduler.platform())
        .subscribe(s -> startTask.dispose(), Dialogs::error);
    startStatus.finished();
  }

  @Scheduled(fixedDelay = 1000) void checkTask() {
    if (ServerState.RUNNING.equals(serverState.get())) {
      if (runningTask != null && !runningTask.isDisposed()) {
        return;
      }
      // 执行很快，所以要持有这个任务，直到它完成（状态变成 isDisposed）
      runningTask = programVM.check().subscribe(console::log, Dialogs::error);
    }
  }
}
