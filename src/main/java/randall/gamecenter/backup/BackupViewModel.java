package randall.gamecenter.backup;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import helper.Compressor;
import helper.Explorer;
import helper.javafx.model.Status;
import helper.javafx.ui.Dialogs;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ini4j.Ini;
import org.ini4j.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import randall.gamecenter.backup.model.BackupData;
import randall.gamecenter.backup.model.BackupFunctionStatus;
import randall.gamecenter.backup.model.BackupMode;
import randall.gamecenter.backup.model.BackupState;
import randall.gamecenter.config.model.Config;
import randall.gamecenter.util.IniLoader;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class BackupViewModel {
  public final Status modify = new Status();
  public final Status add = new Status();

  private final Status delete = new Status();
  private final Status start = new Status();
  private final Status save = new Status();

  public final ObjectProperty<BackupData> selected = new SimpleObjectProperty<>();

  private final ObservableList<BackupData> backups = FXCollections.observableArrayList();
  private final ObjectProperty<BackupFunctionStatus> status =
      new SimpleObjectProperty<>(BackupFunctionStatus.STOPPED);

  private final CompositeDisposable disposable = new CompositeDisposable();

  private final Config config;
  private Disposable startTask;

  @PreDestroy void onDestroy() {
    disposable.clear();
  }

  public void bindTable(TableView<BackupData> tableView) {
    disposable.add(JavaFxObservable.valuesOf(tableView.getSelectionModel().selectedItemProperty())
        .doOnNext(backupData -> modify.running())
        .doOnNext(backupData -> delete.running())
        .subscribe(selected::setValue));
    tableView.setItems(backups);
  }

  public void bindDataDirectory(TextField field) {
    disposable.add(JavaFxObservable.valuesOf(selected)
        .observeOn(JavaFxScheduler.platform())
        .map(BackupData::getSource)
        .subscribe(field::setText));
  }

  public void bindBackupDirectory(TextField field) {
    disposable.add(JavaFxObservable.valuesOf(selected)
        .observeOn(JavaFxScheduler.platform())
        .map(BackupData::getDestination)
        .subscribe(field::setText));
  }

  public void bindEnabled(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(selected)
        .observeOn(JavaFxScheduler.platform())
        .map(BackupData::isEnabled)
        .subscribe(box::setSelected));
  }

  public void bindCompressEnabled(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(selected)
        .observeOn(JavaFxScheduler.platform())
        .map(BackupData::isCompressEnabled)
        .subscribe(box::setSelected));
  }

  public void bindTimingMode(RadioButton radio) {
    disposable.add(JavaFxObservable.valuesOf(selected)
        .observeOn(JavaFxScheduler.platform())
        .map(BackupData::getMode)
        .map(BackupMode.TIMING::equals)
        .subscribe(radio::setSelected));
  }

  public void bindDelayMode(RadioButton radio) {
    disposable.add(JavaFxObservable.valuesOf(selected)
        .observeOn(JavaFxScheduler.platform())
        .map(BackupData::getMode)
        .map(BackupMode.DELAY::equals)
        .subscribe(radio::setSelected));
  }

  public void bindTimingHours(Spinner<Integer> spinner) {
    spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
    disposable.add(JavaFxObservable.valuesOf(selected)
        .observeOn(JavaFxScheduler.platform())
        .filter(backupData -> {
          boolean timing = BackupMode.TIMING.equals(backupData.getMode());
          spinner.setDisable(!timing);
          return timing;
        })
        .map(BackupData::getHours)
        .subscribe(spinner.getValueFactory()::setValue));
  }

  public void bindTimingMinutes(Spinner<Integer> spinner) {
    spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
    disposable.add(JavaFxObservable.valuesOf(selected)
        .observeOn(JavaFxScheduler.platform())
        .filter(backupData -> {
          boolean timing = BackupMode.TIMING.equals(backupData.getMode());
          spinner.setDisable(!timing);
          return timing;
        })
        .map(BackupData::getMinutes)
        .subscribe(spinner.getValueFactory()::setValue));
  }

  public void bindDelayHours(Spinner<Integer> spinner) {
    spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99, 0));
    disposable.add(JavaFxObservable.valuesOf(selected)
        .observeOn(JavaFxScheduler.platform())
        .filter(backupData -> {
          boolean delay = BackupMode.DELAY.equals(backupData.getMode());
          spinner.setDisable(!delay);
          return delay;
        })
        .map(BackupData::getHours)
        .subscribe(spinner.getValueFactory()::setValue));
  }

  public void bindDelayMinutes(Spinner<Integer> spinner) {
    spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
    disposable.add(JavaFxObservable.valuesOf(selected)
        .observeOn(JavaFxScheduler.platform())
        .filter(backupData -> {
          boolean delay = BackupMode.DELAY.equals(backupData.getMode());
          spinner.setDisable(!delay);
          return delay;
        })
        .map(BackupData::getMinutes)
        .subscribe(spinner.getValueFactory()::setValue));
  }

  public void bindModify(Button button) {
    disposable.add(modify.observe().subscribe(button::setDisable));
  }

  public void bindDelete(Button button) {
    disposable.add(delete.observe().subscribe(button::setDisable));
  }

  public void bindAdd(Button button) {
    disposable.add(add.observe().subscribe(button::setDisable));
  }

  public void bindSave(Button button) {
    disposable.add(save.observe().subscribe(button::setDisable));
  }

  public void bindStart(Button button) {
    disposable.add(start.observe().subscribe(button::setDisable));
    disposable.add(JavaFxObservable.valuesOf(status)
        .observeOn(JavaFxScheduler.platform())
        .map(BackupFunctionStatus::getLabel)
        .subscribe(button::setText));
  }

  public void bindMessage(Label label) {
    disposable.add(JavaFxObservable.valuesOf(status)
        .observeOn(JavaFxScheduler.platform())
        .subscribe(status -> {
          label.setText(status.getMessage());
          label.setTextFill(status.getColor());
        }));
  }

  public Optional<BackupData> findSource(String source) {
    synchronized (backups) {
      return backups.stream().filter(data -> data.getSource().equals(source)).findAny();
    }
  }

  public void deleteBackup() {
    delete.running();
    BackupData data = selected.getValue();
    if (data != null) {
      backups.remove(data);
      Dialogs.info("删除成功！").show();
    } else {
      Dialogs.info("删除失败！").show();
    }
    delete.finished();
  }

  public void addData(BackupData data) {
    backups.add(data);
  }

  public void saveBackup() {
    save.running();
    Path iniFile = Paths.get(config.home.getPath(), "Backup.ini");
    try {
      Explorer.delete(iniFile);
      Explorer.create(iniFile);
      Ini ini = IniLoader.load(iniFile);
      for (int i = 0; i < backups.size(); i++) {
        BackupData data = backups.get(i);
        ini.put(String.valueOf(i), "Source", data.getSource());
        ini.put(String.valueOf(i), "Save", data.getDestination());
        ini.put(String.valueOf(i), "Hour", data.getHours());
        ini.put(String.valueOf(i), "Min", data.getMinutes());
        ini.put(String.valueOf(i), "BackMode", data.getMode());
        ini.put(String.valueOf(i), "GetBack", data.isEnabled());
        ini.put(String.valueOf(i), "Zip", data.isCompressEnabled());
      }
      ini.store();
    } catch (IOException e) {
      Dialogs.error("保存备份配置失败！", e).show();
    }
    Dialogs.info("保存成功！").show();
    save.finished();
  }

  public void loadBackupList() {
    delete.running();
    modify.running();
    try {
      Path iniFile = Paths.get(config.home.getPath(), "Backup.ini");
      Explorer.create(iniFile);
      Ini ini = IniLoader.load(iniFile);
      Collection<Profile.Section> sections = ini.values();
      for (Profile.Section section : sections) {
        String source = section.get("Source", "");
        if (Strings.isNullOrEmpty(source)) {
          continue;
        }
        String destination = section.get("Save", "");
        if (Strings.isNullOrEmpty(destination)) {
          continue;
        }
        BackupData data = BackupData.create();
        data.setSource(source);
        data.setDestination(destination);
        data.setHours(section.get("Hour", Integer.class, 0));
        data.setMinutes(section.get("Min", Integer.class, 0));
        data.setMode(BackupMode.values()[section.get("BackMode", Integer.class, 0)]);
        data.setEnabled(section.get("GetBack", Boolean.class, true));
        data.setCompressEnabled(section.get("Zip", Boolean.class, true));
        addData(data);
      }
    } catch (Exception e) {
      Dialogs.error("读取备份文件列表出错！", e).show();
    }
  }

  public void startBackup() {
    start.running();
    switch (status.getValue()) {
      case STOPPED:
        status.setValue(BackupFunctionStatus.STARTED);
        attemptStart();
        break;
      case STARTED:
        status.setValue(BackupFunctionStatus.STOPPED);
        attemptStop();
        break;
    }
    start.finished();
  }

  private void attemptStop() {
    if (startTask != null && !startTask.isDisposed()) {
      startTask.dispose();
    }
  }

  private void attemptStart() {
    backups.forEach(data -> data.setStartTimestamp(LocalDateTime.now()));
    if (startTask != null && !startTask.isDisposed()) {
      startTask.dispose();
    }
    startTask = Observable.interval(1, 1, TimeUnit.SECONDS)
        .flatMap(aLong -> Observable.fromIterable(backups).doOnNext(this::execute))
        .subscribe();
  }

  /**
   * todo refactor here
   */
  private void execute(BackupData data) {
    if (!data.isEnabled()) {
      return;
    }
    switch (data.getState()) {
      case DEFAULT:
        LocalTime target = data.getStartTimestamp().toLocalTime();
        target = data.getMode().compute(target, data.getHours(), data.getMinutes());
        if (LocalTime.now().isAfter(target)) {
          data.setStartTimestamp(LocalDateTime.now());
          data.setState(BackupState.WAITING);
          data.getBackupFiles().clear();
          if (!data.getSource().endsWith(File.separator)) {
            data.setSource(data.getSource() + File.separator);
          }
          if (!data.getDestination().endsWith(File.separator)) {
            data.setDestination(data.getDestination() + File.separator);
          }
          if (data.isCompressEnabled()) {
            if (data.getErrorCount() < 2) {
              Path destPath = Paths.get(data.getDestination());
              Explorer.mkdir(destPath);
              Path zipFile = destPath.resolve(renameZip(data.getSource()));
              try {
                Compressor.zipCompress(data.getSource(), zipFile.toString());
              } catch (Exception e) {
                log.error("ZIP 压缩 " + data.getSource() + " 目录出错！", e);
                data.setErrorCount(data.getErrorCount() + 1);
              }
            }
            data.setState(BackupState.FINISHED);
            return;
          }

          searchFile(data, data.getSource());
          String newDest =
              String.format("%s%s%s", data.getDestination(), formatDate(), File.separator);
          for (String filename : data.getBackupFiles()) {
            if (data.isStopSearchEnabled()) {
              return;
            }
            String newFilename = filename.replace(data.getSource(), newDest);
            Path path = Paths.get(newFilename);
            Explorer.mkdir(path.getParent());
            try {
              Files.copy(Paths.get(filename), path, REPLACE_EXISTING, COPY_ATTRIBUTES);
            } catch (IOException e) {
              log.error("备份时出错，无法复制文件！", e);
            }
          }
        }
        data.setState(BackupState.FINISHED);
        break;
      case WAITING:
        break;
      case FINISHED:
        if (BackupMode.TIMING.equals(data.getMode())) {
          if (!LocalDate.now().equals(data.getToday())) {
            data.setToday(data.getStartTimestamp().toLocalDate());
            data.setState(BackupState.DEFAULT);
          }
        } else if (BackupMode.DELAY.equals(data.getMode())) {
          data.setState(BackupState.DEFAULT);
        }
        data.setErrorCount(0);
        break;
    }
  }

  private String renameZip(String source) {
    return String.format("%s-%s.zip", lastDirName(source), formatDate());
  }

  private String formatDate() {
    return DateTimeFormatter.ofPattern("yyyy-MM-dd.HH-mm").format(LocalDateTime.now());
  }

  private String lastDirName(String directory) {
    return Paths.get(directory).toFile().getName();
  }

  private void searchFile(BackupData data, String directory) {
    Preconditions.checkNotNull(directory, "directory == null");
    if (!directory.endsWith(File.separator)) {
      directory += File.separator;
    }
    File dirFile = new File(directory);
    for (File file : Objects.requireNonNull(dirFile.listFiles())) {
      if (file.isDirectory()) {
        searchFile(data, file.getPath());
      } else {
        data.getBackupFiles().add(file.getPath());
      }
      if (data.isStopSearchEnabled()) {
        return;
      }
    }
  }
}
