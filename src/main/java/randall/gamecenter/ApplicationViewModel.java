package randall.gamecenter;

import com.google.common.base.Strings;
import de.felixroske.jfxsupport.FXMLController;
import helper.Explorer;
import helper.javafx.ui.Dialogs;
import io.reactivex.disposables.CompositeDisposable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ini4j.Ini;
import org.ini4j.Profile;
import org.ini4j.Wini;
import org.springframework.beans.factory.annotation.Autowired;
import randall.gamecenter.model.BackupManager;
import randall.gamecenter.model.Config;
import randall.gamecenter.model.JavaFxValidator;
import randall.gamecenter.model.StartMode;
import randall.gamecenter.viewmodel.ConfigViewModel;
import randall.gamecenter.viewmodel.ControlViewModel;


/**
 * 控制器。
 *
 * @author mrzhqiang
 */
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FXMLController
public final class ApplicationViewModel {
  /* 控制面板 */
  @FXML TabPane mainTabPane;
  @FXML CheckBox databaseCheckBox;
  @FXML CheckBox accountCheckBox;
  @FXML CheckBox coreCheckBox;
  @FXML CheckBox loggerCheckBox;
  @FXML CheckBox runCheckBox;
  @FXML CheckBox roleCheckBox;
  @FXML CheckBox loginCheckBox;
  @FXML CheckBox topCheckBox;
  @FXML ComboBox<StartMode> startModeComboBox;
  @FXML Spinner<Integer> hoursSpinner;
  @FXML Spinner<Integer> minutesSpinner;
  @FXML TextArea consoleTextArea;
  @FXML Button startServerButton;
  /* 配置向导 */
  @FXML TabPane configTabPane;
  @FXML TextField homePathTextField;
  @FXML TextField homeNameTextField;
  @FXML TextField homeDatabaseTextField;
  @FXML TextField homeHostTextField;
  @FXML Spinner<Integer> portOffsetSpinner;
  @FXML CheckBox closeWuxingCheckBox;
  @FXML Button defaultHomeButton;
  @FXML Button reloadAllButton;
  @FXML CheckBox loginEnabledCheckBox;
  @FXML TextField loginXTextField;
  @FXML TextField loginYTextField;
  @FXML TextField loginPortTextField;
  @FXML Button defaultLoginButton;
  @FXML CheckBox roleEnabledCheckBox;
  @FXML TextField roleXTextField;
  @FXML TextField roleYTextField;
  @FXML TextField rolePortTextField;
  @FXML Button defaultRoleButton;
  @FXML CheckBox runEnabledCheckBox;
  @FXML TextField runXTextField;
  @FXML TextField runYTextField;
  @FXML TextField runPortTextField;
  @FXML Button defaultRunButton;
  @FXML CheckBox accountEnabledCheckBox;
  @FXML TextField accountXTextField;
  @FXML TextField accountYTextField;
  @FXML TextField accountPortTextField;
  @FXML TextField accountServerPortTextField;
  @FXML TextField accountMonitorPortTextField;
  @FXML Button defaultAccountButton;
  @FXML CheckBox databaseEnabledCheckBox;
  @FXML TextField databaseXTextField;
  @FXML TextField databaseYTextField;
  @FXML TextField databasePortTextField;
  @FXML TextField databaseServerPortTextField;
  @FXML Button defaultDatabaseButton;
  @FXML CheckBox loggerEnabledCheckBox;
  @FXML TextField loggerXTextField;
  @FXML TextField loggerYTextField;
  @FXML TextField loggerPortTextField;
  @FXML Button defaultLoggerButton;
  @FXML CheckBox coreEnabledCheckBox;
  @FXML TextField coreXTextField;
  @FXML TextField coreYTextField;
  @FXML TextField corePortTextField;
  @FXML TextField coreServerPortTextField;
  @FXML Button defaultCoreButton;
  @FXML CheckBox topEnabledCheckBox;
  @FXML TextField topXTextField;
  @FXML TextField topYTextField;
  @FXML Button defaultTopButton;
  @FXML Button refreshConfigButton;
  @FXML Button saveConfigButton;
  /* 数据备份 */
  @FXML TableView<BackupManager.BackupObject> dataBackupTable;
  @FXML TableColumn<BackupManager.BackupObject, String> dataDirectoryColumn;
  @FXML TableColumn<BackupManager.BackupObject, String> backupDirectoryColumn;
  @FXML RadioButton dayBackupModeRadio;
  @FXML RadioButton intervalBackupModeRadio;
  @FXML ToggleGroup backupModeToggleGroup;
  @FXML Spinner<Integer> dayModeHoursSpinner;
  @FXML Spinner<Integer> intervalModeHoursSpinner;
  @FXML Spinner<Integer> dayModeMinutesSpinner;
  @FXML Spinner<Integer> intervalModeMinutesSpinner;
  @FXML CheckBox backupFunctionCheckBox;
  @FXML CheckBox compressFunctionCheckBox;
  @FXML CheckBox autoRunBackupCheckBox;
  @FXML TextField dataDirectoryTextField;
  @FXML TextField backupDirectoryTextField;
  @FXML Button modifyBackupButton;
  @FXML Button deleteBackupButton;
  @FXML Button addBackupButton;
  @FXML Button saveBackupButton;
  @FXML Button startBackupButton;
  @FXML Label backupMessageLabel;
  /* 开区数据清理 */
  @FXML CheckBox deleteRoleDataCheckBox;
  @FXML CheckBox deleteNPCMakeDataCheckBox;
  @FXML CheckBox deleteAccountDataCheckBox;
  @FXML CheckBox deleteEMailDataCheckBox;
  @FXML CheckBox deleteGuildDataCheckBox;
  @FXML CheckBox deleteAccountLoggerCheckBox;
  @FXML CheckBox clearShabakeDataCheckBox;
  @FXML CheckBox deleteM2ServerLoggerCheckBox;
  @FXML CheckBox clearGlobalVariateCheckBox;
  @FXML CheckBox deleteGameLoggerCheckBox;
  @FXML CheckBox resetItemIDCountCheckBox;
  @FXML CheckBox clearRoleRelationDataCheckBox;
  @FXML Button startClearDataButton;

  private final CompositeDisposable disposable = new CompositeDisposable();

  private final Config config;
  private final ControlViewModel controlVM;
  private final ConfigViewModel configVM;

  @FXML void initialize() {
    initControl();
    initConfig();
    initBackup();
    mainTabPane.getSelectionModel().selectFirst();
    configTabPane.getSelectionModel().selectFirst();
    config.init();
    loadBackupList();
    onStartBackupClicked();
  }

  @PreDestroy void onDestroy() {
    disposable.clear();
  }

  private void initControl() {
    controlVM.programVM.bindDatabase(databaseCheckBox);
    controlVM.programVM.bindAccount(accountCheckBox);
    controlVM.programVM.bindCore(coreCheckBox);
    controlVM.programVM.bindLogger(loggerCheckBox);
    controlVM.programVM.bindRun(runCheckBox);
    controlVM.programVM.bindRole(roleCheckBox);
    controlVM.programVM.bindLogin(loginCheckBox);
    controlVM.programVM.bindTop(topCheckBox);
    controlVM.startModeVM.bindStartMode(startModeComboBox);
    controlVM.startModeVM.bindHours(hoursSpinner);
    controlVM.startModeVM.bindMinutes(minutesSpinner);
    controlVM.bindConsole(consoleTextArea);
    controlVM.bindStart(startServerButton);
    startModeComboBox.setValue(StartMode.NORMAL);
    consoleTextArea.clear();
  }

  private void initConfig() {
    configVM.homeVM.bindPath(homePathTextField);
    configVM.homeVM.bindName(homeNameTextField);
    configVM.homeVM.bindDatabase(homeDatabaseTextField);
    configVM.homeVM.bindHost(homeHostTextField);
    configVM.homeVM.bindWuxing(closeWuxingCheckBox);
    configVM.bindPortOffset(portOffsetSpinner);
    configVM.homeVM.bindLoadDefault(defaultHomeButton);
    configVM.bindReload(reloadAllButton);
    configVM.databaseVM.bindX(databaseXTextField);
    configVM.databaseVM.bindY(databaseYTextField);
    configVM.databaseVM.bindPort(databasePortTextField);
    configVM.databaseVM.bindServer(databaseServerPortTextField);
    configVM.databaseVM.bindEnabled(databaseEnabledCheckBox);
    configVM.databaseVM.bindLoadDefault(defaultDatabaseButton);
    configVM.accountVM.bindX(accountXTextField);
    configVM.accountVM.bindY(accountYTextField);
    configVM.accountVM.bindPort(accountPortTextField);
    configVM.accountVM.bindServer(accountServerPortTextField);
    configVM.accountVM.bindMonitor(accountMonitorPortTextField);
    configVM.accountVM.bindEnabled(accountEnabledCheckBox);
    configVM.accountVM.bindLoadDefault(defaultAccountButton);
    configVM.coreVM.bindX(coreXTextField);
    configVM.coreVM.bindY(coreYTextField);
    configVM.coreVM.bindPort(corePortTextField);
    configVM.coreVM.bindServer(coreServerPortTextField);
    configVM.coreVM.bindEnabled(coreEnabledCheckBox);
    configVM.coreVM.bindLoadDefault(defaultCoreButton);
    configVM.loggerVM.bindX(loggerXTextField);
    configVM.loggerVM.bindY(loggerYTextField);
    configVM.loggerVM.bindPort(loggerPortTextField);
    configVM.loggerVM.bindEnabled(loggerEnabledCheckBox);
    configVM.loggerVM.bindLoadDefault(defaultLoggerButton);
    configVM.runVM.bindX(runXTextField);
    configVM.runVM.bindY(runYTextField);
    configVM.runVM.bindPort(runPortTextField);
    configVM.runVM.bindEnabled(runEnabledCheckBox);
    configVM.runVM.bindLoadDefault(defaultRunButton);
    configVM.roleVM.bindX(roleXTextField);
    configVM.roleVM.bindY(roleYTextField);
    configVM.roleVM.bindPort(rolePortTextField);
    configVM.roleVM.bindEnabled(roleEnabledCheckBox);
    configVM.roleVM.bindLoadDefault(defaultRoleButton);
    configVM.loginVM.bindX(loginXTextField);
    configVM.loginVM.bindY(loginYTextField);
    configVM.loginVM.bindPort(loginPortTextField);
    configVM.loginVM.bindEnabled(loginEnabledCheckBox);
    configVM.loginVM.bindLoadDefault(defaultLoginButton);
    configVM.topVM.bindX(topXTextField);
    configVM.topVM.bindY(topYTextField);
    configVM.topVM.bindEnabled(topEnabledCheckBox);
    configVM.topVM.bindLoadDefault(defaultTopButton);
    configVM.bindRefresh(refreshConfigButton);
    configVM.bindSave(saveConfigButton);
  }

  private void initBackup() {
    dataDirectoryColumn.setCellValueFactory(param -> param.getValue().sourceDir);
    backupDirectoryColumn.setCellValueFactory(param -> param.getValue().destinationDir);
    //dataBackupTable.setItems(share.backupManager.backupList);
    dataBackupTable.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> changeBackupMode(newValue));
    dayModeHoursSpinner.setValueFactory(
        new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
    dayModeMinutesSpinner.setValueFactory(
        new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
    intervalModeHoursSpinner.setValueFactory(
        new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99, 0));
    intervalModeMinutesSpinner.setValueFactory(
        new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
    dayBackupModeRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
      intervalModeHoursSpinner.setDisable(newValue);
      intervalModeMinutesSpinner.setDisable(newValue);
      dayModeHoursSpinner.setDisable(!newValue);
      dayModeMinutesSpinner.setDisable(!newValue);
    });
    intervalBackupModeRadio.selectedProperty()
        .addListener((observable, oldValue, newValue) -> {
          dayModeHoursSpinner.setDisable(newValue);
          dayModeMinutesSpinner.setDisable(newValue);
          intervalModeHoursSpinner.setDisable(!newValue);
          intervalModeMinutesSpinner.setDisable(!newValue);
        });
    configVM.homeVM.bindBackup(autoRunBackupCheckBox);
  }

  private void changeBackupMode(BackupManager.BackupObject newValue) {
    dataDirectoryTextField.setText(newValue.sourceDir.get());
    backupDirectoryTextField.setText(newValue.destinationDir.get());
    backupFunctionCheckBox.setSelected(newValue.backupEnabled);
    compressFunctionCheckBox.setSelected(newValue.compressEnabled);
    if (newValue.backupMode == 0) {
      backupModeToggleGroup.selectToggle(dayBackupModeRadio);
      dayModeHoursSpinner.getValueFactory().setValue(newValue.hours);
      dayModeMinutesSpinner.getValueFactory().setValue(newValue.minutes);
    } else {
      backupModeToggleGroup.selectToggle(intervalBackupModeRadio);
      intervalModeHoursSpinner.getValueFactory().setValue(newValue.hours);
      intervalModeMinutesSpinner.getValueFactory().setValue(newValue.minutes);
    }
    deleteBackupButton.setDisable(false);
    modifyBackupButton.setDisable(false);
  }

  private void mainOutMessage(String message) {
  }

  @FXML void onStartServerClicked() {
    controlVM.startServer();
  }

  private void loadBackupList() {
    deleteBackupButton.setDisable(true);
    modifyBackupButton.setDisable(true);
    try {
      Path path = Paths.get(config.home.getPath()/*, share.backupListFile*/);
      Explorer.create(path);
      Ini ini = new Wini(path.toFile());
      Collection<Profile.Section> sections = ini.values();
      int index = 0;
      for (Profile.Section section : sections) {
        String source = section.get("Source", "");
        String destination = section.get("Save", "");
        if (Strings.isNullOrEmpty(source) || Strings.isNullOrEmpty(destination)) {
          continue;
        }
        BackupManager.BackupObject object = new BackupManager.BackupObject();
        object.index = index;
        index++;
        object.sourceDir.set(source);
        object.destinationDir.set(destination);
        object.hours = section.get("Hour", Integer.class, 0);
        object.minutes = section.get("Min", Integer.class, 0);
        object.backupMode = section.get("BackMode", Integer.class, 0);
        object.backupEnabled = section.get("GetBack", Boolean.class, true);
        object.compressEnabled = section.get("Zip", Boolean.class, true);
        //share.backupManager.addToList(object);
      }
    } catch (IOException e) {
      Dialogs.error("读取备份文件列表出错！", e).show();
    }
  }

  @FXML void onSelectHomePathClicked() {
    DirectoryChooser chooser = new DirectoryChooser();
    chooser.setTitle("请选择游戏服务端目录");
    chooser.setInitialDirectory(Paths.get(config.home.getPath()).toFile());
    Optional.ofNullable(chooser.showDialog(null))
        .ifPresent(file -> homePathTextField.setText(file.getPath()));
  }

  @FXML void onDefaultHomeClicked() {
    configVM.homeVM.loadDefault();
  }

  @FXML void onReloadAllClicked() {
    configVM.reloadAll();
  }

  @FXML void onNextHomeClicked() {
    Optional<String> path = JavaFxValidator.verifyDirectory(homePathTextField);
    if (!path.isPresent()) {
      return;
    }
    Optional<String> database = JavaFxValidator.verifyName(homeDatabaseTextField);
    if (!database.isPresent()) {
      return;
    }
    Optional<String> name = JavaFxValidator.verifyName(homeNameTextField);
    if (!name.isPresent()) {
      return;
    }
    Optional<String> host = JavaFxValidator.verifyAddress(homeHostTextField);
    if (!host.isPresent()) {
      return;
    }
    boolean wuxing = closeWuxingCheckBox.isSelected();
    config.home.setPath(path.get());
    config.home.setDatabase(database.get());
    config.home.setName(name.get());
    config.home.setHost(host.get());
    config.home.setWuxing(wuxing);
    configTabPane.getSelectionModel().selectNext();
  }

  @FXML void onLoginEnabledClicked() {
    config.login.setEnabled(loginEnabledCheckBox.isSelected());
  }

  @FXML void onDefaultLoginClicked() {
    configVM.loginVM.loadDefault();
  }

  @FXML void onPreviousLoginClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  @FXML void onNextLoginClicked() {
    Optional<Integer> x = JavaFxValidator.verifyPosition(loginXTextField);
    if (!x.isPresent()) {
      return;
    }
    Optional<Integer> y = JavaFxValidator.verifyPosition(loginYTextField);
    if (!y.isPresent()) {
      return;
    }
    Optional<Integer> port = JavaFxValidator.verifyPort(loginPortTextField);
    if (!port.isPresent()) {
      return;
    }
    boolean enabled = loginEnabledCheckBox.isSelected();
    config.login.setX(x.get());
    config.login.setY(y.get());
    config.login.setPort(port.get());
    config.login.setEnabled(enabled);
    configTabPane.getSelectionModel().selectNext();
  }

  @FXML void onRoleEnabledClicked() {
    config.role.setEnabled(roleEnabledCheckBox.isSelected());
  }

  @FXML void onDefaultRoleClicked() {
    configVM.roleVM.loadDefault();
  }

  @FXML void onPreviousRoleClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  @FXML void onNextRoleClicked() {
    Optional<Integer> x = JavaFxValidator.verifyPosition(roleXTextField);
    if (!x.isPresent()) {
      return;
    }
    Optional<Integer> y = JavaFxValidator.verifyPosition(roleYTextField);
    if (!y.isPresent()) {
      return;
    }
    Optional<Integer> port = JavaFxValidator.verifyPort(rolePortTextField);
    if (!port.isPresent()) {
      return;
    }
    boolean enabled = roleEnabledCheckBox.isSelected();
    config.role.setX(x.get());
    config.role.setY(y.get());
    config.role.setPort(port.get());
    config.role.setEnabled(enabled);
    configTabPane.getSelectionModel().selectNext();
  }

  @FXML void onRunEnabledClicked() {
    config.run.setEnabled(runEnabledCheckBox.isSelected());
  }

  @FXML void onDefaultRunClicked() {
    configVM.runVM.loadDefault();
  }

  @FXML void onPreviousRunClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  @FXML void onNextRunClicked() {
    Optional<Integer> x = JavaFxValidator.verifyPosition(runXTextField);
    if (!x.isPresent()) {
      return;
    }
    Optional<Integer> y = JavaFxValidator.verifyPosition(runYTextField);
    if (!y.isPresent()) {
      return;
    }
    Optional<Integer> port = JavaFxValidator.verifyPort(runPortTextField);
    if (!port.isPresent()) {
      return;
    }
    boolean enabled = runEnabledCheckBox.isSelected();
    config.run.setX(x.get());
    config.run.setY(y.get());
    config.run.setPort(port.get());
    config.run.setEnabled(enabled);
    configTabPane.getSelectionModel().selectNext();
  }

  @FXML void onAccountEnabledClicked() {
    config.account.setEnabled(accountEnabledCheckBox.isSelected());
  }

  @FXML void onDefaultAccountClicked() {
    configVM.accountVM.loadDefault();
  }

  @FXML void onPreviousAccountClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  @FXML void onNextAccountClicked() {
    Optional<Integer> x = JavaFxValidator.verifyPosition(accountXTextField);
    if (!x.isPresent()) {
      return;
    }
    Optional<Integer> y = JavaFxValidator.verifyPosition(accountYTextField);
    if (!y.isPresent()) {
      return;
    }
    Optional<Integer> port = JavaFxValidator.verifyPort(accountPortTextField);
    if (!port.isPresent()) {
      return;
    }
    Optional<Integer> server = JavaFxValidator.verifyPort(accountServerPortTextField);
    if (!server.isPresent()) {
      return;
    }
    Optional<Integer> monitor = JavaFxValidator.verifyPort(accountMonitorPortTextField);
    if (!monitor.isPresent()) {
      return;
    }
    boolean enabled = accountEnabledCheckBox.isSelected();
    config.account.setX(x.get());
    config.account.setY(y.get());
    config.account.setPort(port.get());
    config.account.setServer(server.get());
    config.account.setMonitor(monitor.get());
    config.account.setEnabled(enabled);
    configTabPane.getSelectionModel().selectNext();
  }

  @FXML void onDatabaseEnabledClicked() {
    config.database.setEnabled(databaseEnabledCheckBox.isSelected());
  }

  @FXML void onDefaultDatabaseClicked() {
    configVM.databaseVM.loadDefault();
  }

  @FXML void onPreviousDatabaseClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  @FXML void onNextDatabaseClicked() {
    Optional<Integer> x = JavaFxValidator.verifyPosition(databaseXTextField);
    if (!x.isPresent()) {
      return;
    }
    Optional<Integer> y = JavaFxValidator.verifyPosition(databaseYTextField);
    if (!y.isPresent()) {
      return;
    }
    Optional<Integer> port = JavaFxValidator.verifyPort(databasePortTextField);
    if (!port.isPresent()) {
      return;
    }
    Optional<Integer> server = JavaFxValidator.verifyPort(databaseServerPortTextField);
    if (!server.isPresent()) {
      return;
    }
    boolean enabled = databaseEnabledCheckBox.isSelected();
    config.database.setX(x.get());
    config.database.setY(y.get());
    config.database.setPort(port.get());
    config.database.setServer(server.get());
    config.database.setEnabled(enabled);
    configTabPane.getSelectionModel().selectNext();
  }

  @FXML void onLoggerEnabledClicked() {
    config.logger.setEnabled(loggerEnabledCheckBox.isSelected());
  }

  @FXML void onDefaultLoggerClicked() {
    configVM.loggerVM.loadDefault();
  }

  @FXML void onPreviousLogServerConfigClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  @FXML void onNextLogServerConfigClicked() {
    Optional<Integer> x = JavaFxValidator.verifyPosition(loggerXTextField);
    if (!x.isPresent()) {
      return;
    }
    Optional<Integer> y = JavaFxValidator.verifyPosition(loggerYTextField);
    if (!y.isPresent()) {
      return;
    }
    Optional<Integer> port = JavaFxValidator.verifyPort(loggerPortTextField);
    if (!port.isPresent()) {
      return;
    }
    boolean enabled = loggerEnabledCheckBox.isSelected();
    config.logger.setX(x.get());
    config.logger.setY(y.get());
    config.logger.setPort(port.get());
    config.logger.setEnabled(enabled);
    configTabPane.getSelectionModel().selectNext();
  }

  @FXML void onCoreEnabledClicked() {
    config.core.setEnabled(coreEnabledCheckBox.isSelected());
  }

  @FXML void onDefaultCoreClicked() {
    configVM.coreVM.loadDefault();
  }

  @FXML void onPreviousCoreClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  @FXML void onNextCoreClicked() {
    Optional<Integer> x = JavaFxValidator.verifyPosition(coreXTextField);
    if (!x.isPresent()) {
      return;
    }
    Optional<Integer> y = JavaFxValidator.verifyPosition(coreYTextField);
    if (!y.isPresent()) {
      return;
    }
    Optional<Integer> port = JavaFxValidator.verifyPort(corePortTextField);
    if (!port.isPresent()) {
      return;
    }
    Optional<Integer> server = JavaFxValidator.verifyPort(coreServerPortTextField);
    if (!server.isPresent()) {
      return;
    }
    boolean enabled = coreEnabledCheckBox.isSelected();
    config.core.setX(x.get());
    config.core.setY(y.get());
    config.core.setPort(port.get());
    config.core.setServer(server.get());
    config.core.setEnabled(enabled);
    configTabPane.getSelectionModel().selectNext();
  }

  @FXML void onTopEnabledClicked() {
    config.top.setEnabled(topEnabledCheckBox.isSelected());
  }

  @FXML void onDefaultTopClicked() {
    configVM.topVM.loadDefault();
  }

  @FXML void onPreviousTopClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  @FXML void onNextTopClicked() {
    Optional<Integer> x = JavaFxValidator.verifyPosition(topXTextField);
    if (!x.isPresent()) {
      return;
    }
    Optional<Integer> y = JavaFxValidator.verifyPosition(topYTextField);
    if (!y.isPresent()) {
      return;
    }
    boolean enabled = topEnabledCheckBox.isSelected();
    config.top.setX(x.get());
    config.top.setY(y.get());
    config.top.setEnabled(enabled);
    configTabPane.getSelectionModel().selectNext();
  }

  @FXML void onRefreshConfigClicked() {
    configVM.refresh();
    Dialogs.info("引擎配置文件已经刷新完毕...").show();
  }

  @FXML void onPreviousSaveConfigClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  @FXML void onSaveConfigClicked() {
    configVM.save();
    Dialogs.info("配置文件已经保存完毕...")
        .showAndWait()
        .filter(ButtonType.OK::equals)
        .flatMap(buttonType -> Dialogs.confirm("是否刷新游戏服务器配置文件？"))
        .ifPresent(buttonType -> {
          onRefreshConfigClicked();
          configTabPane.getSelectionModel().selectFirst();
          mainTabPane.getSelectionModel().selectFirst();
        });
  }

  @FXML void onModifyBackupClicked() {
    BackupManager.BackupObject object = dataBackupTable.getSelectionModel().getSelectedItem();
    if (object != null) {
      String source = dataDirectoryTextField.getText().trim();
      if (Strings.isNullOrEmpty(source)) {
        Dialogs.warn("请选择数据目录！！").show();
        return;
      }
      String destination = backupDirectoryTextField.getText().trim();
      if (Strings.isNullOrEmpty(destination)) {
        Dialogs.warn("请选择备份目录！！").show();
        return;
      }
      int hours;
      int minutes;
      if (dayBackupModeRadio.isSelected()) {
        hours = dayModeHoursSpinner.getValue();
        minutes = dayModeMinutesSpinner.getValue();
      } else {
        hours = intervalModeHoursSpinner.getValue();
        minutes = intervalModeMinutesSpinner.getValue();
      }
      object.sourceDir.set(source);
      object.destinationDir.set(destination);
      object.hours = hours;
      object.minutes = minutes;
      object.backupEnabled = backupFunctionCheckBox.isSelected();
      object.compressEnabled = compressFunctionCheckBox.isSelected();
      if (dayBackupModeRadio.isSelected()) {
        object.backupMode = 0;
      } else {
        object.backupMode = 1;
      }
      Dialogs.alert("修改成功！！").show();
    }
  }

  @FXML void onDeleteBackupClicked() {
    BackupManager.BackupObject object = dataBackupTable.getSelectionModel().getSelectedItem();
    if (object != null) {
      share.backupManager.backupList.remove(object);
      Dialogs.alert("删除成功！").show();
    } else {
      Dialogs.alert("删除失败！").show();
    }
  }

  @FXML void onAddBackupClicked() {
    String source = dataDirectoryTextField.getText().trim();
    if (Strings.isNullOrEmpty(source)) {
      Dialogs.warn("请选择数据目录！！").show();
      return;
    }
    String destination = backupDirectoryTextField.getText().trim();
    if (Strings.isNullOrEmpty(destination)) {
      Dialogs.warn("请选择备份目录！！").show();
      return;
    }
    if (share.backupManager.findObject(source).isPresent()) {
      Dialogs.warn("此数据目录已在备份列表内！！");
      return;
    }
    int hours;
    int minutes;
    if (dayBackupModeRadio.isSelected()) {
      hours = dayModeHoursSpinner.getValue();
      minutes = dayModeMinutesSpinner.getValue();
    } else {
      hours = intervalModeHoursSpinner.getValue();
      minutes = intervalModeMinutesSpinner.getValue();
    }
    BackupManager.BackupObject object = new BackupManager.BackupObject();
    object.index = share.backupManager.backupList.size();
    object.sourceDir.set(source);
    object.destinationDir.set(destination);
    object.hours = hours;
    object.minutes = minutes;
    object.backupEnabled = backupFunctionCheckBox.isSelected();
    object.compressEnabled = compressFunctionCheckBox.isSelected();
    if (dayBackupModeRadio.isSelected()) {
      object.backupMode = 0;
    } else {
      object.backupMode = 1;
    }
    share.backupManager.backupList.add(object);
    refBackupListToView();
    Dialogs.alert("增加成功！！").show();
  }

  @FXML void onSaveBackupClicked() {
    saveBackupButton.setDisable(true);
    Path path = Paths.get(share.gameDirectory, share.backupListFile);
    try {
      IOHelper.delete(path);
      IOHelper.create(path);
      Ini ini = new Wini(path.toFile());
      for (int i = 0; i < share.backupManager.backupList.size(); i++) {
        BackupManager.BackupObject object = share.backupManager.backupList.get(i);
        ini.put(String.valueOf(i), "Source", object.sourceDir.get());
        ini.put(String.valueOf(i), "Save", object.destinationDir.get());
        ini.put(String.valueOf(i), "Hour", object.hours);
        ini.put(String.valueOf(i), "Min", object.minutes);
        ini.put(String.valueOf(i), "BackMode", object.backupMode);
        ini.put(String.valueOf(i), "GetBack", object.backupEnabled);
        ini.put(String.valueOf(i), "Zip", object.compressEnabled);
      }
      ini.store();
    } catch (IOException e) {
      Dialogs.error("保存备份配置失败！", e).show();
    }
    Dialogs.alert("保存成功！").show();
    saveBackupButton.setDisable(false);
  }

  @FXML void onStartBackupClicked() {
    switch (share.backupStartStatus) {
      case 0:
        share.backupStartStatus = 1;
        startBackupButton.setText("停止");
        share.backupManager.start();
        backupMessageLabel.setTextFill(Color.GREEN);
        backupMessageLabel.setText("数据备份功能启动中...");
        break;
      case 1:
        share.backupStartStatus = 0;
        startBackupButton.setText("启动");
        share.backupManager.stop();
        backupMessageLabel.setTextFill(Color.RED);
        backupMessageLabel.setText("数据备份功能已停止...");
        break;
    }
  }

  @FXML void onAutoRunBackupClicked() {
    share.autoRunBakEnabled = autoRunBackupCheckBox.isSelected();
    share.profile.ini.put(BASIC_SECTION_NAME, "AutoRunBak", share.autoRunBakEnabled);
    try {
      share.profile.ini.store();
    } catch (IOException e) {
      log.error("保存备份配置失败！", e);
    }
  }

  @FXML void onChooseDataDirectoryClicked() {
    DirectoryChooser chooser = new DirectoryChooser();
    chooser.setTitle("请选择你要备份的数据目录");
    chooser.setInitialDirectory(new File(share.gameDirectory));
    File file = chooser.showDialog(null);
    if (file != null) {
      dataDirectoryTextField.setText(file.getAbsolutePath());
    }
  }

  @FXML void onChooseBackupDirectoryClicked() {
    DirectoryChooser chooser = new DirectoryChooser();
    chooser.setTitle("请选择备份文件的输出目录");
    chooser.setInitialDirectory(new File(share.gameDirectory));
    File file = chooser.showDialog(null);
    if (file != null) {
      backupDirectoryTextField.setText(file.getAbsolutePath());
    }
  }

  @FXML void onStartClearDataClicked() {
    if (startState == STOPPED_STATE) {
      startClearDataButton.setDisable(true);
      File homeDirectory = new File(share.gameDirectory);
      if (deleteRoleDataCheckBox.isSelected()) {
        IOHelper.delete(Paths.get(homeDirectory.getPath(), "DBServer\\DB\\Hum.DB"));
        IOHelper.delete(Paths.get(homeDirectory.getPath(), "DBServer\\DB\\Mir.DB"));
        IOHelper.delete(Paths.get(homeDirectory.getPath(), "DBServer\\DB\\Mir.DB.idx"));
      }
      if (deleteAccountDataCheckBox.isSelected()) {
        IOHelper.delete(Paths.get(homeDirectory.getPath(), "LoginSrv\\DB\\Id.DB"));
        IOHelper.delete(Paths.get(homeDirectory.getPath(), "LoginSrv\\DB\\Id.DB.idx"));
      }
      if (deleteGuildDataCheckBox.isSelected()) {
        IOHelper.deleteAll(Paths.get(homeDirectory.getPath(), "Mir200\\GuildBase\\Guilds\\"));
        Path guildListPath = Paths.get(homeDirectory.getPath(), "Mir200\\GuildBase\\GuildList.txt");
        if (Files.exists(guildListPath)) {
          IOHelper.write(guildListPath, "");
        }
      }
      if (clearShabakeDataCheckBox.isSelected()) {
        List<String> castleList =
            IOHelper.lines(Paths.get(homeDirectory.getPath(), "Mir200\\Castle\\List.txt"));
        castleList.stream()
            .map(s -> String.format("Mir200\\Castle\\%s\\AttackSabukWall.txt", s))
            .map(s -> Paths.get(homeDirectory.getPath(), s))
            .forEach(file -> IOHelper.write(file, ""));
        castleList.stream()
            .map(s -> String.format("Mir200\\Castle\\%s\\SabukW.txt", s))
            .map(s -> new File(homeDirectory, s))
            .filter(File::exists)
            .forEach(file -> {
              try {
                Ini ini = new Wini(file);
                ini.put("Setup", "OwnGuild", "");
                ini.put("Setup", "ChangeDate", "");
                ini.put("Setup", "WarDate", "");
                ini.put("Setup", "IncomeToday", "");
                ini.put("Setup", "TotalGold", "");
                ini.put("Setup", "TodayIncome", "");

                ini.put("Defense", "MainDoorHP", "10000");
                ini.put("Defense", "LeftWallHP", "5000");
                ini.put("Defense", "CenterWallHP", "5000");
                ini.put("Defense", "RightWallHP", "5000");
                ini.put("Defense", "Archer_1_HP", "2000");
                ini.put("Defense", "Archer_2_HP", "2000");
                ini.put("Defense", "Archer_3_HP", "2000");
                ini.put("Defense", "Archer_4_HP", "2000");
                ini.put("Defense", "Archer_5_HP", "2000");
                ini.put("Defense", "Archer_6_HP", "2000");
                ini.put("Defense", "Archer_7_HP", "2000");
                ini.put("Defense", "Archer_8_HP", "2000");
                ini.put("Defense", "Archer_9_HP", "2000");
                ini.put("Defense", "Archer_10_HP", "2000");
                ini.put("Defense", "Archer_11_HP", "2000");
                ini.put("Defense", "Archer_12_HP", "2000");
                ini.store();
              } catch (IOException e) {
                log.error("清理沙巴克数据的配置文件出错！", e);
              }
            });
      }
      if (clearGlobalVariateCheckBox.isSelected()) {
        IOHelper.delete(Paths.get(homeDirectory.getPath(), "Mir200\\Global.ini"));
      }
      if (resetItemIDCountCheckBox.isSelected()) {
        File mir2SetupFile = new File(homeDirectory, "Mir200\\!Setup.txt");
        if (mir2SetupFile.exists()) {
          try {
            Ini ini = new Wini(mir2SetupFile);
            ini.put("Setup", "ItemNumber", 10000);
            ini.put("Setup", "ItemNumberEx", 2000000000);
            ini.store();
          } catch (IOException e) {
            log.error("复位物品 ID 计数出错！", e);
          }
        }
      }
      if (clearRoleRelationDataCheckBox.isSelected()) {
        IOHelper.write(Paths.get(homeDirectory.getPath(), "Mir200\\Envir\\UnForceMaster.txt"), "");
        IOHelper.write(Paths.get(homeDirectory.getPath(), "Mir200\\Envir\\UnFriend.txt"), "");
        IOHelper.write(Paths.get(homeDirectory.getPath(), "Mir200\\Envir\\UnMarry.txt"), "");
        IOHelper.write(Paths.get(homeDirectory.getPath(), "Mir200\\Envir\\UnMaster.txt"), "");
      }
      if (deleteNPCMakeDataCheckBox.isSelected()) {
        IOHelper.deleteAll(Paths.get(homeDirectory.getPath(), "Mir200\\Envir\\Market_Upg\\"));
      }
      if (deleteEMailDataCheckBox.isSelected()) {
        IOHelper.delete(Paths.get(homeDirectory.getPath(), "Mir200\\EMail\\EMailData.dat"));
        IOHelper.delete(Paths.get(homeDirectory.getPath(), "Mir200\\EMail\\EMailName.txt"));
      }
      if (deleteAccountLoggerCheckBox.isSelected()) {
        IOHelper.deleteAll(Paths.get(homeDirectory.getPath(), "LoginSrv\\ChrLog\\"));
      }
      if (deleteM2ServerLoggerCheckBox.isSelected()) {
        IOHelper.deleteAll(Paths.get(homeDirectory.getPath(), "Mir200\\Log\\"));
        IOHelper.deleteAll(Paths.get(homeDirectory.getPath(), "Mir200\\ConLog\\"));
      }
      if (deleteGameLoggerCheckBox.isSelected()) {
        IOHelper.deleteAll(Paths.get(homeDirectory.getPath(), "LogServer\\BaseDir\\"));
      }
      startClearDataButton.setDisable(false);
      Dialogs.alert("全部清理完成！").show();
    } else {
      Dialogs.warn("请将服务器处于停止状态下再进行操作！").show();
    }
  }
}
