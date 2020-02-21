package randall.gamecenter;

import de.felixroske.jfxsupport.FXMLController;
import helper.javafx.ui.Dialogs;
import io.reactivex.disposables.CompositeDisposable;
import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.DirectoryChooser;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import randall.gamecenter.backup.BackupViewModel;
import randall.gamecenter.backup.model.BackupData;
import randall.gamecenter.backup.model.BackupMode;
import randall.gamecenter.clean.CleanViewModel;
import randall.gamecenter.config.ConfigViewModel;
import randall.gamecenter.config.model.Config;
import randall.gamecenter.control.ControlViewModel;
import randall.gamecenter.control.model.StartMode;
import randall.gamecenter.util.JavaFxValidator;

/**
 * 控制器。
 * <p>
 * 这是 JavaFx 级别的控制器，主要是将 View 绑定到 ViewModel，以实现模块化设计。
 */
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FXMLController
public final class Controller {
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
  @FXML TableView<BackupData> dataBackupTable;
  @FXML TableColumn<BackupData, String> dataDirectoryColumn;
  @FXML TableColumn<BackupData, String> backupDirectoryColumn;
  @FXML RadioButton timingBackupModeRadio;
  @FXML RadioButton delayBackupModeRadio;
  @FXML ToggleGroup backupModeToggleGroup;
  @FXML Spinner<Integer> timingHoursSpinner;
  @FXML Spinner<Integer> delayHoursSpinner;
  @FXML Spinner<Integer> timingMinutesSpinner;
  @FXML Spinner<Integer> delayMinutesSpinner;
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
  @FXML CheckBox deleteCoreLoggerCheckBox;
  @FXML CheckBox clearGlobalVariateCheckBox;
  @FXML CheckBox deleteGameLoggerCheckBox;
  @FXML CheckBox resetItemIDCountCheckBox;
  @FXML CheckBox clearRoleRelationDataCheckBox;
  @FXML Button startClearDataButton;

  private final CompositeDisposable disposable = new CompositeDisposable();

  private final ControlViewModel controlVM;
  private final ConfigViewModel configVM;
  private final BackupViewModel backupVM;
  private final CleanViewModel cleanVM;

  private final Config config;

  @FXML void initialize() {
    initControl();
    initConfig();
    initBackup();
    iniClean();
    mainTabPane.getSelectionModel().selectFirst();
    configTabPane.getSelectionModel().selectFirst();
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
  }

  private void initConfig() {
    configVM.homeVM.bindPath(homePathTextField);
    configVM.homeVM.bindName(homeNameTextField);
    configVM.homeVM.bindDatabase(homeDatabaseTextField);
    configVM.homeVM.bindHost(homeHostTextField);
    configVM.homeVM.bindBackup(autoRunBackupCheckBox);
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
    configVM.loadData();
  }

  private void initBackup() {
    dataDirectoryColumn.setCellValueFactory(param -> param.getValue().sourceProperty());
    backupDirectoryColumn.setCellValueFactory(param -> param.getValue().destinationProperty());
    backupVM.bindTable(dataBackupTable);
    backupVM.bindDataDirectory(dataDirectoryTextField);
    backupVM.bindBackupDirectory(backupDirectoryTextField);
    backupVM.bindEnabled(backupFunctionCheckBox);
    backupVM.bindCompressEnabled(compressFunctionCheckBox);
    backupVM.bindTimingMode(timingBackupModeRadio);
    backupVM.bindDelayMode(delayBackupModeRadio);
    backupVM.bindTimingHours(timingHoursSpinner);
    backupVM.bindTimingMinutes(timingMinutesSpinner);
    backupVM.bindDelayHours(delayHoursSpinner);
    backupVM.bindDelayMinutes(delayMinutesSpinner);
    backupVM.bindModify(modifyBackupButton);
    backupVM.bindDelete(deleteBackupButton);
    backupVM.bindAdd(addBackupButton);
    backupVM.bindSave(saveBackupButton);
    backupVM.bindStart(startBackupButton);
    backupVM.bindMessage(backupMessageLabel);
    backupVM.loadBackupList();
    backupVM.startBackup();
  }

  private void iniClean() {
    cleanVM.bindStart(startClearDataButton);
    cleanVM.bindDatabase(deleteRoleDataCheckBox);
    cleanVM.bindAccount(deleteAccountDataCheckBox);
    cleanVM.bindGuild(deleteGuildDataCheckBox);
    cleanVM.bindShabake(clearShabakeDataCheckBox);
    cleanVM.bindGlobal(clearGlobalVariateCheckBox);
    cleanVM.bindItem(resetItemIDCountCheckBox);
    cleanVM.bindRelation(clearRoleRelationDataCheckBox);
    cleanVM.bindMake(deleteNPCMakeDataCheckBox);
    cleanVM.bindEmail(deleteEMailDataCheckBox);
    cleanVM.bindAccountLogger(deleteAccountLoggerCheckBox);
    cleanVM.bindCoreLogger(deleteCoreLoggerCheckBox);
    cleanVM.bindGameLogger(deleteGameLoggerCheckBox);
  }

  @FXML void onStartServerClicked() {
    controlVM.startServer();
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
    backupVM.modify.running();
    BackupData data = updateBackupData(backupVM.selected.getValue());
    if (data != null) {
      Dialogs.info("修改成功！！").show();
    }
    backupVM.modify.finished();
  }

  @FXML void onDeleteBackupClicked() {
    backupVM.deleteBackup();
  }

  @FXML void onAddBackupClicked() {
    backupVM.add.running();
    BackupData data = updateBackupData(BackupData.create());
    if (data != null) {
      backupVM.addData(data);
      Dialogs.info("增加成功！！").show();
    }
    backupVM.add.finished();
  }

  private BackupData updateBackupData(BackupData data) {
    Optional<String> source = JavaFxValidator.verifyDirectory(dataDirectoryTextField);
    if (!source.isPresent()) {
      return null;
    }
    Optional<String> destination = JavaFxValidator.verifyDirectory(backupDirectoryTextField);
    if (!destination.isPresent()) {
      return null;
    }
    if (backupVM.findSource(source.get()).isPresent()) {
      Dialogs.warn("此数据目录已在备份列表内！！");
      return null;
    }
    int hours = delayHoursSpinner.getValue();
    int minutes = delayMinutesSpinner.getValue();
    BackupMode mode = BackupMode.DELAY;
    if (timingBackupModeRadio.isSelected()) {
      hours = timingHoursSpinner.getValue();
      minutes = timingMinutesSpinner.getValue();
      mode = BackupMode.TIMING;
    }
    data.setSource(source.get());
    data.setDestination(destination.get());
    data.setHours(hours);
    data.setMinutes(minutes);
    data.setEnabled(backupFunctionCheckBox.isSelected());
    data.setCompressEnabled(compressFunctionCheckBox.isSelected());
    data.setMode(mode);
    return data;
  }

  @FXML void onSaveBackupClicked() {
    backupVM.saveBackup();
  }

  @FXML void onStartBackupClicked() {
    backupVM.startBackup();
  }

  @FXML void onAutoRunBackupClicked() {
    config.home.setBackup(autoRunBackupCheckBox.isSelected());
    config.save(config.home);
  }

  @FXML void onChooseDataDirectoryClicked() {
    DirectoryChooser chooser = new DirectoryChooser();
    chooser.setTitle("请选择你要备份的数据目录");
    chooser.setInitialDirectory(new File(config.home.getPath()));
    File file = chooser.showDialog(null);
    if (file != null) {
      dataDirectoryTextField.setText(file.getAbsolutePath());
    }
  }

  @FXML void onChooseBackupDirectoryClicked() {
    DirectoryChooser chooser = new DirectoryChooser();
    chooser.setTitle("请选择备份文件的输出目录");
    chooser.setInitialDirectory(new File(config.home.getPath()));
    File file = chooser.showDialog(null);
    if (file != null) {
      backupDirectoryTextField.setText(file.getAbsolutePath());
    }
  }

  @FXML void onStartClearDataClicked() {
    if (controlVM.stopped()) {
      cleanVM.clean();
    } else {
      Dialogs.warn("请将服务器处于停止状态下再进行操作！").show();
    }
  }
}
