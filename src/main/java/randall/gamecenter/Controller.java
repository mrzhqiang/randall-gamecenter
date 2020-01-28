package randall.gamecenter;

import com.google.common.base.Strings;
import de.felixroske.jfxsupport.FXMLController;
import helper.DateTimeHelper;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import javafx.application.Platform;
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
import randall.common.ui.Dialogs;
import randall.common.util.IOHelper;
import randall.common.util.Networks;
import randall.gamecenter.model.BackupManager;
import randall.gamecenter.model.Share;
import randall.gamecenter.model.StartMode;
import randall.gamecenter.viewmodel.ConfigViewModel;
import randall.gamecenter.viewmodel.ControlViewModel;

import static randall.gamecenter.model.Share.ALL_IP_ADDRESS;
import static randall.gamecenter.model.Share.BASIC_SECTION_NAME;
import static randall.gamecenter.model.Share.DB_SERVER_PROCESS_CODE;
import static randall.gamecenter.model.Share.DB_SERVER_SECTION_NAME_2;
import static randall.gamecenter.model.Share.LOGIN_GATE_PROCESS_CODE;
import static randall.gamecenter.model.Share.LOGIN_GATE_SECTION_NAME_2;
import static randall.gamecenter.model.Share.LOGIN_SERVER_PROCESS_CODE;
import static randall.gamecenter.model.Share.LOGIN_SRV_SECTION_NAME_2;
import static randall.gamecenter.model.Share.LOG_SERVER_PROCESS_CODE;
import static randall.gamecenter.model.Share.LOG_SERVER_SECTION_2;
import static randall.gamecenter.model.Share.M2_SERVER_CONFIG_FILE;
import static randall.gamecenter.model.Share.M2_SERVER_PROCESS_CODE;
import static randall.gamecenter.model.Share.M2_SERVER_SECTION_NAME_1;
import static randall.gamecenter.model.Share.M2_SERVER_SECTION_NAME_2;
import static randall.gamecenter.model.Share.MAX_RUN_GATE_COUNT;
import static randall.gamecenter.model.Share.ONLINE_USER_LIMIT;
import static randall.gamecenter.model.Share.PLUG_TOP_PROCESS_CODE;
import static randall.gamecenter.model.Share.PRIMARY_IP_ADDRESS;
import static randall.gamecenter.model.Share.QUIT_CODE;
import static randall.gamecenter.model.Share.RUNNING_STATE;
import static randall.gamecenter.model.Share.RUN_GATE_PROCESS_CODE;
import static randall.gamecenter.model.Share.RUN_GATE_SECTION_NAME_2;
import static randall.gamecenter.model.Share.SECOND_IP_ADDRESS;
import static randall.gamecenter.model.Share.SEL_GATE_PROCESS_CODE;
import static randall.gamecenter.model.Share.SEL_GATE_SECTION_NAME_2;
import static randall.gamecenter.model.Share.SERVER_CONFIG_FILE;
import static randall.gamecenter.model.Share.STOPPED_STATE;

/**
 * 控制器。
 *
 * @author mrzhqiang
 */
@Slf4j(topic = "randall")
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
  @FXML TextArea gameInfoTextArea;
  @FXML Button startGameButton;
  /* 配置向导 */
  @FXML TabPane configTabPane;
  @FXML TextField homeHostTextField;
  @FXML TextField homeNameTextField;
  @FXML TextField homeDatabaseTextField;
  @FXML TextField homePathTextField;
  @FXML Spinner<Integer> portOffsetSpinner;
  @FXML CheckBox closeWuxingCheckBox;
  @FXML CheckBox loginEnabledCheckBox;
  @FXML TextField loginXTextField;
  @FXML TextField loginYTextField;
  @FXML TextField loginPortTextField;
  @FXML CheckBox roleEnabledCheckBox;
  @FXML TextField roleXTextField;
  @FXML TextField roleYTextField;
  @FXML TextField rolePortTextField;
  @FXML CheckBox runEnabledCheckBox;
  @FXML TextField runXTextField;
  @FXML TextField runYTextField;
  @FXML TextField runPortTextField;
  @FXML CheckBox accountEnabledCheckBox;
  @FXML TextField accountXTextField;
  @FXML TextField accountYTextField;
  @FXML TextField accountPortTextField;
  @FXML TextField accountMonitorPortTextField;
  @FXML TextField accountServerPortTextField;
  @FXML CheckBox databaseEnabledCheckBox;
  @FXML TextField databaseXTextField;
  @FXML TextField databaseYTextField;
  @FXML TextField databasePortTextField;
  @FXML TextField databaseServerPortTextField;
  @FXML CheckBox openLogServerCheckBox;
  @FXML TextField logServerFormXTextField;
  @FXML TextField logServerFormYTextField;
  @FXML TextField logServerGatePortTextField;
  @FXML CheckBox openM2ServerCheckBox;
  @FXML TextField m2ServerFormXTextField;
  @FXML TextField m2ServerFormYTextField;
  @FXML TextField m2ServerGatePortTextField;
  @FXML TextField m2ServerServerPortTextField;
  @FXML CheckBox openPlugTopCheckBox;
  @FXML TextField plugTopFormXTextField;
  @FXML TextField plugTopFormYTextField;
  /* 数据备份 */
  @FXML TableView<BackupManager.BackupObject> dataBackupTableView;
  @FXML TableColumn<BackupManager.BackupObject, String> dataDirectoryTableColumn;
  @FXML TableColumn<BackupManager.BackupObject, String> backupDirectoryTableColumn;
  @FXML RadioButton dayBackupModeRadioButton;
  @FXML RadioButton intervalBackupModeRadioButton;
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
  @FXML CheckBox clearSabacDataCheckBox;
  @FXML CheckBox deleteM2ServerLoggerCheckBox;
  @FXML CheckBox clearGlobalVariateCheckBox;
  @FXML CheckBox deleteGameLoggerCheckBox;
  @FXML CheckBox resetItemIDCountCheckBox;
  @FXML CheckBox clearRoleRelationDataCheckBox;
  @FXML Button startClearDataButton;

  private boolean gateStopped;
  private long gateStopTick;
  // 0 -- default; 1 -- starting; 2 -- running; 3 -- stopping; 9 -- error
  private int startState = 0;

  private long runTick;
  private long runTime;

  private Timer startGameTimer = new Timer();
  private Timer stopGameTimer = new Timer();
  private Timer checkRunTimer = new Timer();

  private final ControlViewModel controlVM;
  private final ConfigViewModel configVM;
  private final Share share;

  @FXML void initialize() {
    controlVM.bindDatabase(databaseCheckBox);
    controlVM.bindAccount(accountCheckBox);
    controlVM.bindLogger(loggerCheckBox);
    controlVM.bindCore(coreCheckBox);
    controlVM.bindRun(runCheckBox);
    controlVM.bindRole(roleCheckBox);
    controlVM.bindLogin(loginCheckBox);
    controlVM.bindTop(topCheckBox);
    controlVM.bindStartMode(startModeComboBox);
    controlVM.bindHours(hoursSpinner);
    controlVM.bindMinutes(minutesSpinner);
    controlVM.bindStartGame(startGameButton);

    configVM.bindHomePath(homePathTextField);
    configVM.bindHomeDatabase(homeDatabaseTextField);
    configVM.bindHomeName(homeNameTextField);
    configVM.bindHomeHost(homeHostTextField);
    configVM.bindPortOffset(portOffsetSpinner);
    configVM.bindWuxing(closeWuxingCheckBox);

    configVM.bindLoginX(loginXTextField);
    configVM.bindLoginY(loginYTextField);
    configVM.bindLoginPort(loginPortTextField);
    configVM.bindLoginEnabled(loginEnabledCheckBox);

    configVM.bindRoleX(roleXTextField);
    configVM.bindRoleY(roleYTextField);
    configVM.bindRolePort(rolePortTextField);
    configVM.bindRoleEnabled(roleEnabledCheckBox);

    configVM.bindRunX(runXTextField);
    configVM.bindRunY(runYTextField);
    configVM.bindRunPort(runPortTextField);
    configVM.bindRunEnabled(runEnabledCheckBox);

    configVM.bindAccountX(accountXTextField);
    configVM.bindAccountY(accountYTextField);
    configVM.bindAccountPort(accountPortTextField);
    configVM.bindAccountServerPort(accountServerPortTextField);
    configVM.bindAccountMonitorPort(accountMonitorPortTextField);
    configVM.bindAccountEnabled(accountEnabledCheckBox);

    configVM.bindDatabaseX(databaseXTextField);
    configVM.bindDatabaseY(databaseYTextField);
    configVM.bindDatabasePort(databasePortTextField);
    configVM.bindDatabaseServerPort(databaseServerPortTextField);
    configVM.bindDatabaseEnabled(databaseEnabledCheckBox);

    configVM.bindLoggerX(loginXTextField);
    configVM.bindLoggerY(loginYTextField);
    configVM.bindLoggerPort(loginPortTextField);
    configVM.bindLoggerEnabled(loginEnabledCheckBox);

    configVM.bindCoreX(loginXTextField);
    configVM.bindCoreY(loginYTextField);
    configVM.bindCorePort(loginPortTextField);
    configVM.bindCoreServerPort(loginPortTextField);
    configVM.bindCoreEnabled(loginEnabledCheckBox);

    configVM.bindTopX(loginXTextField);
    configVM.bindTopY(loginYTextField);
    configVM.bindTopEnabled(loginEnabledCheckBox);

    dataDirectoryTableColumn.setCellValueFactory(param -> param.getValue().sourceDir);
    backupDirectoryTableColumn.setCellValueFactory(param -> param.getValue().destinationDir);
    dataBackupTableView.setItems(share.backupManager.backupList);
    dataBackupTableView.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> changeBackupMode(newValue));
    dayModeHoursSpinner.setValueFactory(
        new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
    dayModeMinutesSpinner.setValueFactory(
        new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
    intervalModeHoursSpinner.setValueFactory(
        new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99, 0));
    intervalModeMinutesSpinner.setValueFactory(
        new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
    dayBackupModeRadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
      intervalModeHoursSpinner.setDisable(newValue);
      intervalModeMinutesSpinner.setDisable(newValue);
      dayModeHoursSpinner.setDisable(!newValue);
      dayModeMinutesSpinner.setDisable(!newValue);
    });
    intervalBackupModeRadioButton.selectedProperty()
        .addListener((observable, oldValue, newValue) -> {
          dayModeHoursSpinner.setDisable(newValue);
          dayModeMinutesSpinner.setDisable(newValue);
          intervalModeHoursSpinner.setDisable(!newValue);
          intervalModeMinutesSpinner.setDisable(!newValue);
        });
    mainTabPane.selectionModelProperty().addListener((observable, oldValue, newValue) -> {
      // todo 程序运行时，不可以切换标签，只有默认状态下才可以
    });

    mainTabPane.getSelectionModel().select(0);
    configTabPane.getSelectionModel().select(0);
    startState = 0;
    gameInfoTextArea.clear();
    //share.profile.load();
    loadBackupList();
    refBackupListToView();
    if (!startService()) {
      return;
    }
    refGameConsole();
    autoRunBackupCheckBox.setSelected(share.autoRunBakEnabled);
    if (share.autoRunBakEnabled) {
      onStartBackupClicked();
    }
  }

  private void initView() {

    //backupModeToggleGroup.selectToggle(dayBackupModeRadioButton);
  }

  private void changeBackupMode(BackupManager.BackupObject newValue) {
    dataDirectoryTextField.setText(newValue.sourceDir.get());
    backupDirectoryTextField.setText(newValue.destinationDir.get());
    backupFunctionCheckBox.setSelected(newValue.backupEnabled);
    compressFunctionCheckBox.setSelected(newValue.compressEnabled);
    if (newValue.backupMode == 0) {
      backupModeToggleGroup.selectToggle(dayBackupModeRadioButton);
      dayModeHoursSpinner.getValueFactory().setValue(newValue.hours);
      dayModeMinutesSpinner.getValueFactory().setValue(newValue.minutes);
    } else {
      backupModeToggleGroup.selectToggle(intervalBackupModeRadioButton);
      intervalModeHoursSpinner.getValueFactory().setValue(newValue.hours);
      intervalModeMinutesSpinner.getValueFactory().setValue(newValue.minutes);
    }
    deleteBackupButton.setDisable(false);
    modifyBackupButton.setDisable(false);
  }

  private void refGameConsole() {
    // 刷新控制台按钮的选中状态
    coreCheckBox.setSelected(controlVM.getConfigModel().getConfig().getCore().getEnabled());
    databaseCheckBox.setSelected(controlVM.getConfigModel().getConfig().getDatabase().getEnabled());
    accountCheckBox.setSelected(controlVM.getConfigModel().getConfig().getAccount().getEnabled());
    loggerCheckBox.setSelected(controlVM.getConfigModel().getConfig().getLogger().getEnabled());
    loginCheckBox.setSelected(controlVM.getConfigModel().getConfig().getLogin().getEnabled());
    roleCheckBox.setSelected(controlVM.getConfigModel().getConfig().getRole().getEnabled());
    runCheckBox.setSelected(controlVM.getConfigModel().getConfig().getRun().getEnabled());
    topCheckBox.setSelected(controlVM.getConfigModel().getConfig().getTop().getEnabled());

    // 第一步 基本设置
    homePathTextField.setText(controlVM.getConfigModel().getConfig().getHome().getPath());
    homeDatabaseTextField.setText(controlVM.getConfigModel().getConfig().getHome().getDatabase());
    homeNameTextField.setText(controlVM.getConfigModel().getConfig().getHome().getFullName());
    homeHostTextField.setText(controlVM.getConfigModel().getConfig().getHome().getHost());
    closeWuxingCheckBox.setSelected(controlVM.getConfigModel().getConfig().getHome().getWuxing());
    // 第二步 登录网关
    loginXTextField.setText(
        String.valueOf(controlVM.getConfigModel().getConfig().getLogin().getX()));
    loginYTextField.setText(
        String.valueOf(controlVM.getConfigModel().getConfig().getLogin().getY()));
    loginEnabledCheckBox.setSelected(
        controlVM.getConfigModel().getConfig().getLogin().getEnabled());
    loginPortTextField.setText(
        String.valueOf(controlVM.getConfigModel().getConfig().getLogin().getPort()));
    // 第三步 角色网关
    roleXTextField.setText(String.valueOf(controlVM.getConfigModel().getConfig().getRole().getX()));
    roleYTextField.setText(String.valueOf(controlVM.getConfigModel().getConfig().getRole().getY()));
    roleEnabledCheckBox.setSelected(controlVM.getConfigModel().getConfig().getRole().getEnabled());
    rolePortTextField.setText(
        String.valueOf(controlVM.getConfigModel().getConfig().getRole().getPort()));
    // 第四步 游戏网关
    runXTextField.setText(String.valueOf(controlVM.getConfigModel().getConfig().getRun().getX()));
    runYTextField.setText(String.valueOf(controlVM.getConfigModel().getConfig().getRun().getY()));
    runEnabledCheckBox.setSelected(controlVM.getConfigModel().getConfig().getRun().getEnabled());
    runPortTextField.setText(
        String.valueOf(controlVM.getConfigModel().getConfig().getRun().getPort()));
    // 第五步 登录服务器
    accountXTextField.setText(
        String.valueOf(controlVM.getConfigModel().getConfig().getAccount().getX()));
    accountYTextField.setText(
        String.valueOf(controlVM.getConfigModel().getConfig().getAccount().getY()));
    accountEnabledCheckBox.setSelected(
        controlVM.getConfigModel().getConfig().getAccount().getEnabled());
    accountPortTextField.setText(
        String.valueOf(controlVM.getConfigModel().getConfig().getAccount().getPort()));
    accountServerPortTextField.setText(
        String.valueOf(controlVM.getConfigModel().getConfig().getAccount().getServerPort()));
    accountMonitorPortTextField.setText(
        String.valueOf(controlVM.getConfigModel().getConfig().getAccount().getMonitorPort()));
    // 第六步 数据库服务器
    databaseXTextField.setText(
        String.valueOf(controlVM.getConfigModel().getConfig().getDatabase().getX()));
    databaseYTextField.setText(
        String.valueOf(controlVM.getConfigModel().getConfig().getDatabase().getY()));
    databaseEnabledCheckBox.setSelected(
        controlVM.getConfigModel().getConfig().getDatabase().getEnabled());
    databasePortTextField.setText(
        String.valueOf(controlVM.getConfigModel().getConfig().getDatabase().getPort()));
    databaseServerPortTextField.setText(
        String.valueOf(controlVM.getConfigModel().getConfig().getDatabase().getServerPort()));
    // 第七步 游戏日志服务器
    logServerFormXTextField.setText(String.valueOf(controlVM.getConfigModel().getConfig().getLogger().getX()));
    logServerFormYTextField.setText(String.valueOf(controlVM.getConfigModel().getConfig().getLogger().getY()));
    openLogServerCheckBox.setSelected(controlVM.getConfigModel().getConfig().getLogger().getEnabled());
    logServerGatePortTextField.setText(String.valueOf(controlVM.getConfigModel().getConfig().getLogger().getPort()));
    // 第八步 游戏主引擎服务器
    m2ServerFormXTextField.setText(String.valueOf(controlVM.getConfigModel().getConfig().getCore().getX()));
    m2ServerFormYTextField.setText(String.valueOf(controlVM.getConfigModel().getConfig().getCore().getY()));
    openM2ServerCheckBox.setSelected(controlVM.getConfigModel().getConfig().getCore().getEnabled());
    m2ServerGatePortTextField.setText(String.valueOf(controlVM.getConfigModel().getConfig().getCore().getPort()));
    m2ServerServerPortTextField.setText(String.valueOf(controlVM.getConfigModel().getConfig().getCore().getServerPort()));
    // 第九步 排行榜插件
    plugTopFormXTextField.setText(String.valueOf(controlVM.getConfigModel().getConfig().getTop().getX()));
    plugTopFormYTextField.setText(String.valueOf(controlVM.getConfigModel().getConfig().getTop().getY()));
    openPlugTopCheckBox.setSelected(controlVM.getConfigModel().getConfig().getTop().getEnabled());
  }

  private boolean startService() {
    mainOutMessage("正在启动游戏客户端控制器...");
    mainOutMessage("游戏控制台启动完成...");
    return true;
  }

  private void mainOutMessage(String message) {
    log.info(message);
    gameInfoTextArea.appendText(String.format("[%s] -- %s" + System.lineSeparator(),
        DateTimeHelper.format(Date.from(Instant.now())),
        message));
  }

  @FXML void onStartGameClicked() {
    controlVM.onStartGame();
  }

  private void loadBackupList() {
    deleteBackupButton.setDisable(true);
    modifyBackupButton.setDisable(true);
    try {
      Path path = Paths.get(share.gameDirectory, share.backupListFile);
      if (Files.notExists(path)) {
        IOHelper.create(path);
      }
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
        share.backupManager.addToList(object);
      }
    } catch (IOException e) {
      Dialogs.error("读取备份文件列表出错！", e).show();
    }
  }

  private void refBackupListToView() {
    // 不做任何事，因为我们已经在视图上绑定好了数据
  }

  @PreDestroy
  public void onDestroy() {
    startGameTimer.cancel();
    stopGameTimer.cancel();
    checkRunTimer.cancel();
    if (share.backupManager != null) {
      share.backupManager.stop();
    }
  }

  public void onOpenLoginGateClicked() {
    controlVM.getConfigModel().getConfig().getLogin().setEnabled(loginEnabledCheckBox.isSelected());
  }

  public void onOpenSelGate1Clicked() {
    controlVM.getConfigModel().getConfig().getRole().setEnabled(roleEnabledCheckBox.isSelected());
  }

  public void onOpenRunGate1Clicked() {
    controlVM.getConfigModel().getConfig().getRun().setEnabled(runEnabledCheckBox.isSelected());
  }

  public void onOpenLoginSrvClicked() {
    controlVM.getConfigModel()
        .getConfig()
        .getAccount()
        .setEnabled(accountEnabledCheckBox.isSelected());
  }

  public void onOpenDBServerClicked() {
    controlVM.getConfigModel()
        .getConfig()
        .getDatabase()
        .setEnabled(databaseEnabledCheckBox.isSelected());
  }

  public void onOpenLogServerClicked() {
    controlVM.getConfigModel().getConfig().getLogger().setEnabled(openLogServerCheckBox.isSelected());
  }

  public void onOpenM2ServerClicked() {
    controlVM.getConfigModel().getConfig().getCore().setEnabled(openM2ServerCheckBox.isSelected());
  }

  public void onOpenPlugTopClicked() {
    controlVM.getConfigModel().getConfig().getTop().setEnabled(openPlugTopCheckBox.isSelected());
  }

  public void onReloadAllConfigClicked() {
    configVM.onReloadAll();
  }

  public void onNextHomeConfigClicked() {
    configVM.onHomeNext(configTabPane.getSelectionModel());
  }

  @FXML void onDefaultHomeConfigClicked() {
    configVM.onHomeDefault();
  }

  public void onPreviousLoginGateConfigClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  public void onNextLoginGateConfigClicked() {
    int port = Integer.parseInt(loginPortTextField.getText().trim());
    if (!Networks.isPort(port)) {
      Dialogs.warn("网关端口设置错误！！").show();
      loginPortTextField.requestFocus();
      return;
    }
    controlVM.getConfigModel().getConfig().getLogin().setPort(port);
    configTabPane.getSelectionModel().selectNext();
  }

  public void onDefaultLoginGateConfigClicked() {
    //share.config.loginGate = new Share.LoginGateConfig();
    refGameConsole();
  }

  public void onPreviousSelGateConfigClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  public void onNextSelGateConfigClicked() {
    int port1 = Integer.parseInt(rolePortTextField.getText().trim());
    if (!Networks.isPort(port1)) {
      Dialogs.warn("网关端口设置错误！！").show();
      rolePortTextField.requestFocus();
      return;
    }
    controlVM.getConfigModel().getConfig().getRole().setPort(port1);

    configTabPane.getSelectionModel().selectNext();
  }

  public void onDefaultSelGateConfigClicked() {
    //share.config.selGate = new Share.SelGateConfig();
    refGameConsole();
  }

  public void onPreviousRunGateConfigClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  public void onNextRunGateConfigClicked() {
    int port1 = Integer.parseInt(runPortTextField.getText().trim());
    if (!Networks.isPort(port1)) {
      Dialogs.warn("网关一端口设置错误！！").show();
      runPortTextField.requestFocus();
      return;
    }
    controlVM.getConfigModel().getConfig().getRun().setPort(port1);

    configTabPane.getSelectionModel().selectNext();
  }

  public void onDefaultRunGateConfigClicked() {
    //share.config.runGate = new Share.RunGateConfig();
    refGameConsole();
  }

  public void onPreviousLoginSrvConfigClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  public void onNextLoginSrvConfigClicked() {
    int gatePort = Integer.parseInt(accountPortTextField.getText().trim());
    if (!Networks.isPort(gatePort)) {
      Dialogs.warn("网关端口设置错误！！").show();
      loginPortTextField.requestFocus();
      return;
    }
    int serverPort = Integer.parseInt(accountServerPortTextField.getText().trim());
    if (!Networks.isPort(serverPort)) {
      Dialogs.warn("通讯端口设置错误！！").show();
      accountServerPortTextField.requestFocus();
      return;
    }
    int monPort = Integer.parseInt(accountMonitorPortTextField.getText().trim());
    if (!Networks.isPort(monPort)) {
      Dialogs.warn("监控端口设置错误！！").show();
      accountMonitorPortTextField.requestFocus();
      return;
    }

    controlVM.getConfigModel().getConfig().getAccount().setPort(gatePort);
    controlVM.getConfigModel().getConfig().getAccount().setServerPort(serverPort);
    controlVM.getConfigModel().getConfig().getAccount().setMonitorPort(monPort);

    configTabPane.getSelectionModel().selectNext();
  }

  public void onDefaultLoginSrvConfigClicked() {
    //share.config.loginSrv = new Share.LoginSrvConfig();
    refGameConsole();
  }

  public void onPreviousDbServerConfigClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  public void onNextDbServerConfigClicked() {
    int gatePort = Integer.parseInt(databasePortTextField.getText().trim());
    if (!Networks.isPort(gatePort)) {
      Dialogs.warn("网关端口设置错误！！").show();
      databasePortTextField.requestFocus();
      return;
    }
    int serverPort = Integer.parseInt(databaseServerPortTextField.getText().trim());
    if (!Networks.isPort(serverPort)) {
      Dialogs.warn("通讯端口设置错误！！").show();
      databaseServerPortTextField.requestFocus();
      return;
    }

    controlVM.getConfigModel().getConfig().getDatabase().setPort(gatePort);
    controlVM.getConfigModel().getConfig().getDatabase().setServerPort(serverPort);

    configTabPane.getSelectionModel().selectNext();
  }

  public void onDefaultDbServerConfigClicked() {
    //share.config.dbServer = new Share.DBServerConfig();
    refGameConsole();
  }

  public void onPreviousLogServerConfigClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  public void onNextLogServerConfigClicked() {
    int port = Integer.parseInt(logServerGatePortTextField.getText().trim());
    if (!Networks.isPort(port)) {
      Dialogs.warn("端口设置错误！！").show();
      logServerGatePortTextField.requestFocus();
      return;
    }
    controlVM.getConfigModel().getConfig().getLogger().setPort(port);

    configTabPane.getSelectionModel().selectNext();
  }

  public void onDefaultLogServerConfigClicked() {
    //share.config.logServer = new Share.LogServerConfig();
    refGameConsole();
  }

  public void onPreviousM2ServerConfigClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  public void onNextM2ServerConfigClicked() {
    int gatePort = Integer.parseInt(m2ServerGatePortTextField.getText().trim());
    if (!Networks.isPort(gatePort)) {
      Dialogs.warn("网关端口设置错误！！").show();
      m2ServerGatePortTextField.requestFocus();
      return;
    }
    int serverPort = Integer.parseInt(m2ServerServerPortTextField.getText().trim());
    if (!Networks.isPort(serverPort)) {
      Dialogs.warn("通讯端口设置错误！！").show();
      m2ServerServerPortTextField.requestFocus();
      return;
    }
    controlVM.getConfigModel().getConfig().getCore().setPort(gatePort);
    controlVM.getConfigModel().getConfig().getCore().setServerPort(serverPort);

    configTabPane.getSelectionModel().selectNext();
  }

  public void onDefaultM2ServerConfigClicked() {
    //share.config.m2Server = new Share.M2ServerConfig();
    refGameConsole();
  }

  public void onPreviousPlugTopConfigClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  public void onNextPlugTopConfigClicked() {
    configTabPane.getSelectionModel().selectNext();
  }

  public void onDefaultPlugTopConfigClicked() {
    //share.config.plugTop = new Share.PlugTopConfig();
    refGameConsole();
  }

  public void onPreviousSaveConfigClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  public void onSaveConfigClicked() {
    controlVM.getConfigModel().getConfig().save();
    Dialogs.alert("配置文件已经保存完毕...")
        .showAndWait()
        .filter(ButtonType.OK::equals)
        .flatMap(buttonType -> Dialogs.confirm("是否生成新的游戏服务器配置文件？"))
        .ifPresent(buttonType -> {
          onGenerateConfigClicked();
          configTabPane.getSelectionModel().selectFirst();
          mainTabPane.getSelectionModel().selectFirst();
        });
  }

  public void onGenerateConfigClicked() {
    generateGameConfig();
    refGameConsole();
    Dialogs.alert("引擎配置文件已经生成完毕...").show();
  }

  private void generateGameConfig() {
    IOHelper.mkdir(Paths.get(share.gameDirectory));
    generateDBServerConfig();
    generateLoginServerConfig();
    generateM2ServerConfig();
    generateLogServerConfig();
    generateRunGateConfig();
    generateSelGateConfig();
    generateLoginGateConfig();
  }

  private void generateMultiRunGateConfig(int index) {
    if (index > 0 && index < MAX_RUN_GATE_COUNT) {
      Path runGateDir = Paths.get(share.gameDirectory, "RunGate\\");
      IOHelper.mkdir(runGateDir);

      try {
        Path runGateConfigPath = Paths.get(runGateDir.toString(), SERVER_CONFIG_FILE);
        IOHelper.create(runGateConfigPath);
        Ini ini = new Wini(runGateConfigPath.toFile());
        ini.put(RUN_GATE_SECTION_NAME_2, "Title", share.gameName);
        ini.put(RUN_GATE_SECTION_NAME_2, "GateAddr", ALL_IP_ADDRESS);
        ini.put(RUN_GATE_SECTION_NAME_2, "GatePort", controlVM.getConfigModel().getConfig().getRun().getPort());
        ini.store();
      } catch (IOException e) {
        Dialogs.error("生成游戏网关[" + (index + 1) + "]配置出错！！", e).show();
      }
    }
  }

  private void generateMultiSelGateConfig(int index) {
    if (index != 0 && index != 1) {
      return;
    }
    Path selGateDir = Paths.get(share.gameDirectory, "SelGate\\");
    IOHelper.mkdir(selGateDir);

    try {
      Path selGateConfigPath = Paths.get(selGateDir.toString(), SERVER_CONFIG_FILE);
      IOHelper.create(selGateConfigPath);
      Ini ini = new Wini(selGateConfigPath.toFile());
      ini.put(SEL_GATE_SECTION_NAME_2, "Title", share.gameName);
      ini.put(SEL_GATE_SECTION_NAME_2, "GateAddr", ALL_IP_ADDRESS);
      ini.put(SEL_GATE_SECTION_NAME_2, "GatePort", controlVM.getConfigModel().getConfig().getRole().getPort());
      if (share.ip2Enabled) {
        if (index == 0) {
          ini.put(SEL_GATE_SECTION_NAME_2, "ServerAddr", PRIMARY_IP_ADDRESS);
        } else {
          ini.put(SEL_GATE_SECTION_NAME_2, "ServerAddr", SECOND_IP_ADDRESS);
        }
      }
      ini.store();
    } catch (IOException e) {
      Dialogs.error("生成角色网关[" + index + "]出错！！", e).show();
    }
  }

  private void generateMultiLoginGateConfig(int index) {
    if (index != 0 && index != 1) {
      return;
    }
    Path loginGateDir = Paths.get(share.gameDirectory, "LoginGate\\");
    IOHelper.mkdir(loginGateDir);

    try {
      Path loginGateConfigPath = Paths.get(loginGateDir.toString(), SERVER_CONFIG_FILE);
      IOHelper.create(loginGateConfigPath);
      Ini ini = new Wini(loginGateConfigPath.toFile());
      ini.put(LOGIN_SRV_SECTION_NAME_2, "Title", share.gameName);
      ini.put(LOGIN_SRV_SECTION_NAME_2, "GatePort", controlVM.getConfigModel().getConfig().getLogin().getPort());
      if (share.ip2Enabled) {
        if (index == 0) {
          ini.put(LOGIN_SRV_SECTION_NAME_2, "GateAddr", share.extIPAddr);
          ini.put(LOGIN_SRV_SECTION_NAME_2, "ServerAddr", PRIMARY_IP_ADDRESS);
        } else {
          ini.put(LOGIN_SRV_SECTION_NAME_2, "GateAddr", share.extIPAddr2);
          ini.put(LOGIN_SRV_SECTION_NAME_2, "ServerAddr", SECOND_IP_ADDRESS);
        }
      } else {
        ini.put(LOGIN_SRV_SECTION_NAME_2, "GateAddr", ALL_IP_ADDRESS);
      }
      ini.store();
    } catch (IOException e) {
      Dialogs.error("生成角色网关[" + index + "]出错！！", e).show();
    }
  }

  private void generateLoginGateConfig() {
    Path loginGateDir = Paths.get(share.gameDirectory, "LoginGate\\");
    IOHelper.mkdir(loginGateDir);

    try {
      Path configPath = Paths.get(loginGateDir.toString(), SERVER_CONFIG_FILE);
      IOHelper.create(configPath);
      Ini ini = new Wini(configPath.toFile());
      ini.put(LOGIN_GATE_SECTION_NAME_2, "Title", share.gameName);
      ini.put(LOGIN_GATE_SECTION_NAME_2, "ServerAddr", PRIMARY_IP_ADDRESS);
      ini.put(LOGIN_GATE_SECTION_NAME_2, "ServerPort", controlVM.getConfigModel().getConfig().getAccount().getPort());
      ini.put(LOGIN_GATE_SECTION_NAME_2, "GateAddr", ALL_IP_ADDRESS);
      ini.put(LOGIN_GATE_SECTION_NAME_2, "GatePort", controlVM.getConfigModel().getConfig().getLogin().getPort());
      ini.store();
    } catch (IOException e) {
      Dialogs.error("生成登陆网关配置文件出错！！", e).show();
    }
  }

  private void generateSelGateConfig() {
    Path selGateDir = Paths.get(share.gameDirectory, "SelGate\\");
    IOHelper.mkdir(selGateDir);

    try {
      Path configPath = Paths.get(selGateDir.toString(), SERVER_CONFIG_FILE);
      IOHelper.create(configPath);
      Ini ini = new Wini(configPath.toFile());
      ini.put(SEL_GATE_SECTION_NAME_2, "Title", share.gameName);
      ini.put(SEL_GATE_SECTION_NAME_2, "ServerAddr", PRIMARY_IP_ADDRESS);
      ini.put(SEL_GATE_SECTION_NAME_2, "ServerPort", controlVM.getConfigModel().getConfig().getDatabase().getPort());
      ini.put(SEL_GATE_SECTION_NAME_2, "GateAddr", ALL_IP_ADDRESS);
      ini.put(SEL_GATE_SECTION_NAME_2, "GatePort", controlVM.getConfigModel().getConfig().getRole().getPort());
      ini.store();
    } catch (IOException e) {
      Dialogs.error("生成角色网关配置文件出错！！", e).show();
    }
  }

  private void generateRunGateConfig() {
    Path runGateDir = Paths.get(share.gameDirectory, "RunGate\\");
    IOHelper.mkdir(runGateDir);

    try {
      Path configPath = Paths.get(runGateDir.toString(), SERVER_CONFIG_FILE);
      IOHelper.create(configPath);
      Ini ini = new Wini(configPath.toFile());
      ini.put(RUN_GATE_SECTION_NAME_2, "Title", share.gameName);
      ini.put(RUN_GATE_SECTION_NAME_2, "ServerAddr", PRIMARY_IP_ADDRESS);
      ini.put(RUN_GATE_SECTION_NAME_2, "ServerPort", controlVM.getConfigModel().getConfig().getCore().getPort());
      ini.put(RUN_GATE_SECTION_NAME_2, "GateAddr", ALL_IP_ADDRESS);
      ini.put(RUN_GATE_SECTION_NAME_2, "GatePort", controlVM.getConfigModel().getConfig().getRun().getPort());
      ini.put(RUN_GATE_SECTION_NAME_2, "CenterAddr", PRIMARY_IP_ADDRESS);
      ini.put(RUN_GATE_SECTION_NAME_2, "CenterPort", controlVM.getConfigModel().getConfig().getAccount().getServerPort());
      ini.store();
    } catch (IOException e) {
      Dialogs.error("生成游戏网关配置文件出错！！", e).show();
    }
  }

  private void generateLogServerConfig() {
    Path logSrvDir = Paths.get(share.gameDirectory, "LogServer\\");
    IOHelper.mkdir(logSrvDir);

    try {
      Path logSrvPath = Paths.get(logSrvDir.toString(), "LogData.ini");
      IOHelper.create(logSrvPath);
      Ini ini = new Wini(logSrvPath.toFile());
      ini.put(LOG_SERVER_SECTION_2, "ServerName", share.gameName);
      ini.put(LOG_SERVER_SECTION_2, "Port", controlVM.getConfigModel().getConfig().getLogger().getPort());
      ini.put(LOG_SERVER_SECTION_2, "BaseDir", "BaseDir\\");
      ini.store();
    } catch (IOException e) {
      Dialogs.error("生成日志服务器配置文件出错！！", e).show();
    }

    IOHelper.mkdir(Paths.get(logSrvDir.toString(), "BaseDir\\"));
  }

  private void generateM2ServerConfig() {
    Path m2SrvDir = Paths.get(share.gameDirectory, "Mir200\\");
    IOHelper.mkdir(m2SrvDir);

    try {
      Path m2SrvPath = Paths.get(m2SrvDir.toString(), M2_SERVER_CONFIG_FILE);
      IOHelper.create(m2SrvPath);
      Ini ini = new Wini(m2SrvPath.toFile());
      ini.put(M2_SERVER_SECTION_NAME_1, "ServerName", share.gameName);
      ini.put(M2_SERVER_SECTION_NAME_1, "DBName", share.heroDBName);
      ini.put(M2_SERVER_SECTION_NAME_1, "GateAddr", ALL_IP_ADDRESS);
      ini.put(M2_SERVER_SECTION_NAME_1, "GatePort", controlVM.getConfigModel().getConfig().getCore().getPort());
      ini.put(M2_SERVER_SECTION_NAME_1, "DBAddr", PRIMARY_IP_ADDRESS);
      ini.put(M2_SERVER_SECTION_NAME_1, "DBPort", controlVM.getConfigModel().getConfig().getDatabase().getServerPort());
      ini.put(M2_SERVER_SECTION_NAME_1, "IDSAddr", PRIMARY_IP_ADDRESS);
      ini.put(M2_SERVER_SECTION_NAME_1, "IDSPort", controlVM.getConfigModel().getConfig().getAccount().getServerPort());
      ini.put(M2_SERVER_SECTION_NAME_1, "MsgSrvAddr", ALL_IP_ADDRESS);
      ini.put(M2_SERVER_SECTION_NAME_1, "MsgSrvPort", controlVM.getConfigModel().getConfig().getCore().getServerPort());
      ini.put(M2_SERVER_SECTION_NAME_1, "LogServerAddr", PRIMARY_IP_ADDRESS);
      ini.put(M2_SERVER_SECTION_NAME_1, "LogServerPort", controlVM.getConfigModel().getConfig().getLogger().getPort());
      ini.put(M2_SERVER_SECTION_NAME_1, "CloseWuXin", share.closeWuXinEnabled);

      ini.put(M2_SERVER_SECTION_NAME_2, "GuildDir", "GuildBase\\Guilds\\");
      ini.put(M2_SERVER_SECTION_NAME_2, "GuildFile", "GuildBase\\GuildList.txt");
      ini.put(M2_SERVER_SECTION_NAME_2, "ConLogDir", "ConLog\\");
      ini.put(M2_SERVER_SECTION_NAME_2, "CastleDir", "Castle\\");
      ini.put(M2_SERVER_SECTION_NAME_2, "CastleFile", "Castle\\List.txt");
      ini.put(M2_SERVER_SECTION_NAME_2, "GameDataDir", "Envir\\");
      ini.put(M2_SERVER_SECTION_NAME_2, "EnvirDir", "Envir\\");
      ini.put(M2_SERVER_SECTION_NAME_2, "MapDir", "Map\\");
      ini.put(M2_SERVER_SECTION_NAME_2, "NoticeDir", "Notice\\");
      ini.put(M2_SERVER_SECTION_NAME_2, "LogDir", "Log\\");
      ini.put(M2_SERVER_SECTION_NAME_2, "EMailDir", "EMail\\");
      ini.store();
    } catch (IOException e) {
      Dialogs.error("生成服务端核心配置出错！！", e).show();
    }

    // todo 批量创建文件
    IOHelper.mkdir(Paths.get(m2SrvDir.toString(), "GuildBase\\"));
    IOHelper.mkdir(Paths.get(m2SrvDir.toString(), "GuildBase\\Guilds\\"));
    IOHelper.mkdir(Paths.get(m2SrvDir.toString(), "ConLog\\"));
    IOHelper.mkdir(Paths.get(m2SrvDir.toString(), "Castle\\"));
    IOHelper.mkdir(Paths.get(m2SrvDir.toString(), "Envir\\"));
    IOHelper.mkdir(Paths.get(m2SrvDir.toString(), "Map\\"));
    IOHelper.mkdir(Paths.get(m2SrvDir.toString(), "Notice\\"));
    IOHelper.mkdir(Paths.get(m2SrvDir.toString(), "Log\\"));
    IOHelper.mkdir(Paths.get(m2SrvDir.toString(), "EMail\\"));

    IOHelper.write(Paths.get(m2SrvDir.toString(), "!servertable.txt"), PRIMARY_IP_ADDRESS);
  }

  private void generateLoginServerConfig() {
    Path loginSrvDir = Paths.get(share.gameDirectory, "LoginSrv\\");
    IOHelper.mkdir(loginSrvDir);

    try {
      Path loginSrvPath = Paths.get(loginSrvDir.toString(), "Logsrv.ini");
      IOHelper.create(loginSrvPath);
      Ini ini = new Wini(loginSrvPath.toFile());
      ini.put(LOGIN_SRV_SECTION_NAME_2, "ServerAddr", ALL_IP_ADDRESS);
      ini.put(LOGIN_SRV_SECTION_NAME_2, "ServerPort", controlVM.getConfigModel().getConfig().getAccount().getServerPort());
      ini.put(LOGIN_SRV_SECTION_NAME_2, "GateAddr", ALL_IP_ADDRESS);
      ini.put(LOGIN_SRV_SECTION_NAME_2, "GatePort", controlVM.getConfigModel().getConfig().getAccount().getPort());
      ini.put(LOGIN_SRV_SECTION_NAME_2, "MonAddr", ALL_IP_ADDRESS);
      ini.put(LOGIN_SRV_SECTION_NAME_2, "MonPort", controlVM.getConfigModel().getConfig().getAccount().getMonitorPort());
      ini.put(LOGIN_SRV_SECTION_NAME_2, "CloseWuXin", share.closeWuXinEnabled);
      ini.put(LOGIN_SRV_SECTION_NAME_2, "IDDir", "DB\\");
      ini.put(LOGIN_SRV_SECTION_NAME_2, "CountLogDir", "ChrLog\\");
      ini.store();
    } catch (IOException e) {
      Dialogs.error("生成登陆服务器配置文件出错", e).show();
    }

    StringBuilder builder = new StringBuilder(PRIMARY_IP_ADDRESS);
    if (share.ip2Enabled) {
      builder.append(System.lineSeparator()).append(SECOND_IP_ADDRESS);
    }
    IOHelper.write(Paths.get(loginSrvDir.toString(), "!serveraddr.txt"), builder.toString());

    String content = String.format("%s %s %d", share.gameName, share.gameName, ONLINE_USER_LIMIT);
    IOHelper.write(Paths.get(loginSrvDir.toString(), "!UserLimit.txt"), content);

    builder = new StringBuilder(PRIMARY_IP_ADDRESS);
    if (controlVM.getConfigModel().getConfig().getRole().isEnabled()) {
      builder.append(String.format(" %s %d", share.extIPAddr, controlVM.getConfigModel().getConfig().getRole().getPort()));
    }
    builder.append(System.lineSeparator());
    IOHelper.write(Paths.get(loginSrvDir.toString(), "!addrtable.txt"), builder.toString());
    IOHelper.mkdir(Paths.get(loginSrvDir.toString(), "ChrLog\\"));
    IOHelper.mkdir(Paths.get(loginSrvDir.toString(), "DB\\"));
  }

  private void generateDBServerConfig() {
    Path dbServerDir = Paths.get(share.gameDirectory, "DBServer\\");
    IOHelper.mkdir(dbServerDir);
    Path dbFileDir = Paths.get(dbServerDir.toString(), "DB\\");
    IOHelper.mkdir(dbFileDir);

    try {
      Path dbSrcPath = Paths.get(dbServerDir.toString(), "Dbsrc.ini");
      IOHelper.create(dbSrcPath);
      Ini ini = new Wini(dbSrcPath.toFile());
      ini.put(DB_SERVER_SECTION_NAME_2, "ServerName", share.gameName);
      ini.put(DB_SERVER_SECTION_NAME_2, "ServerAddr", PRIMARY_IP_ADDRESS);
      ini.put(DB_SERVER_SECTION_NAME_2, "ServerPort", controlVM.getConfigModel().getConfig().getDatabase().getServerPort());
      ini.put(DB_SERVER_SECTION_NAME_2, "GateAddr", ALL_IP_ADDRESS);
      ini.put(DB_SERVER_SECTION_NAME_2, "GatePort", controlVM.getConfigModel().getConfig().getDatabase().getPort());
      ini.put(DB_SERVER_SECTION_NAME_2, "IDSAddr", PRIMARY_IP_ADDRESS);
      ini.put(DB_SERVER_SECTION_NAME_2, "IDSPort", controlVM.getConfigModel().getConfig().getAccount().getServerPort());
      ini.put(DB_SERVER_SECTION_NAME_2, "DBName", share.heroDBName);
      ini.put(DB_SERVER_SECTION_NAME_2, "DBDir", "DB\\");
      ini.store();
    } catch (IOException e) {
      Dialogs.error("生成数据库服务器配置文件出错！！", e).show();
    }

    StringBuilder builder = new StringBuilder(PRIMARY_IP_ADDRESS);
    if (share.ip2Enabled) {
      builder.append(System.lineSeparator()).append(SECOND_IP_ADDRESS);
    }
    IOHelper.write(Paths.get(dbServerDir.toString(), "!addrtable.txt"), builder.toString());

    builder = new StringBuilder(PRIMARY_IP_ADDRESS);
    //for (int i = 0; i < share.config.runGate.getStart.length; i++) {
    if (controlVM.getConfigModel().getConfig().getRun().isEnabled()) {
      builder.append(
          String.format(" %s %d", share.extIPAddr, controlVM.getConfigModel().getConfig().getRun().getPort()));
    }
    //}
    builder.append(System.lineSeparator());
    IOHelper.write(Paths.get(dbServerDir.toString(), "!serverinfo.txt"), builder.toString());
    IOHelper.write(Paths.get(dbServerDir.toString(), "FUserName.txt"), ";创建人物过滤字符，一行一个过滤");
  }

  public void onModifyBackupClicked() {
    BackupManager.BackupObject object = dataBackupTableView.getSelectionModel().getSelectedItem();
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
      if (dayBackupModeRadioButton.isSelected()) {
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
      if (dayBackupModeRadioButton.isSelected()) {
        object.backupMode = 0;
      } else {
        object.backupMode = 1;
      }
      Dialogs.alert("修改成功！！").show();
    }
  }

  public void onDeleteBackupClicked() {
    BackupManager.BackupObject object = dataBackupTableView.getSelectionModel().getSelectedItem();
    if (object != null) {
      share.backupManager.backupList.remove(object);
      Dialogs.alert("删除成功！").show();
    } else {
      Dialogs.alert("删除失败！").show();
    }
  }

  public void onAddBackupClicked() {
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
    if (dayBackupModeRadioButton.isSelected()) {
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
    if (dayBackupModeRadioButton.isSelected()) {
      object.backupMode = 0;
    } else {
      object.backupMode = 1;
    }
    share.backupManager.backupList.add(object);
    refBackupListToView();
    Dialogs.alert("增加成功！！").show();
  }

  public void onSaveBackupClicked() {
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

  public void onStartBackupClicked() {
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

  public void onAutoRunBackupClicked() {
    share.autoRunBakEnabled = autoRunBackupCheckBox.isSelected();
    share.profile.ini.put(BASIC_SECTION_NAME, "AutoRunBak", share.autoRunBakEnabled);
    try {
      share.profile.ini.store();
    } catch (IOException e) {
      log.error("保存备份配置失败！", e);
    }
  }

  public void onChooseDataDirectoryClicked() {
    DirectoryChooser chooser = new DirectoryChooser();
    chooser.setTitle("请选择你要备份的数据目录");
    chooser.setInitialDirectory(new File(share.gameDirectory));
    File file = chooser.showDialog(null);
    if (file != null) {
      dataDirectoryTextField.setText(file.getAbsolutePath());
    }
  }

  public void onChooseBackupDirectoryClicked() {
    DirectoryChooser chooser = new DirectoryChooser();
    chooser.setTitle("请选择备份文件的输出目录");
    chooser.setInitialDirectory(new File(share.gameDirectory));
    File file = chooser.showDialog(null);
    if (file != null) {
      backupDirectoryTextField.setText(file.getAbsolutePath());
    }
  }

  public void onStartClearDataClicked() {
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
      if (clearSabacDataCheckBox.isSelected()) {
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

  public class StartGameTask extends TimerTask {
    @Override
    public void run() {
      if (share.dbServer.getStart) {
        switch (share.dbServer.startStatus) {
          case 0:
            share.dbServer.disposable = share.dbServer.start()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(Controller.this::handleProcessMessage);
            share.dbServer.startStatus = 1;
            return;
          case 1:
            log.debug("正在等待数据库服务器启动..");
            return;
        }
      }
      // todo 重构为 RxJava 的 Interval 数据流
      if (share.loginServer.getStart) {
        switch (share.loginServer.startStatus) {
          case 0:
            share.loginServer.disposable = share.loginServer.start()
                .observeOn(JavaFxScheduler.platform())
                .doOnError(throwable -> {
                  Dialogs.error(throwable).show();
                  share.loginServer.startStatus = 9;
                })
                .subscribe(Controller.this::handleProcessMessage);
            share.loginServer.startStatus = 1;
            return;
          case 1:
            log.debug("正在等待登陆服务器启动..");
            return;
        }
      }
      if (share.logServer.getStart) {
        switch (share.logServer.startStatus) {
          case 0:
            share.logServer.disposable = share.logServer.start()
                .observeOn(JavaFxScheduler.platform())
                .doOnError(throwable -> {
                  Dialogs.error(throwable).show();
                  share.logServer.startStatus = 9;
                })
                .subscribe(Controller.this::handleProcessMessage);
            share.logServer.startStatus = 1;
            return;
          case 1:
            log.debug("正在等待日志服务器启动..");
            return;
        }
      }
      if (share.m2Server.getStart) {
        switch (share.m2Server.startStatus) {
          case 0:
            share.m2Server.disposable = share.m2Server.start()
                .observeOn(JavaFxScheduler.platform())
                .doOnError(throwable -> {
                  Dialogs.error(throwable).show();
                  share.m2Server.startStatus = 9;
                })
                .subscribe(Controller.this::handleProcessMessage);
            share.m2Server.startStatus = 1;
            return;
          case 1:
            log.debug("正在等待核心服务器启动..");
            return;
        }
      }
      if (getStartRunGate()) {
        return;
      }
      boolean startRunGateOK = true;
      for (int i = 0; i < share.runGate.size(); i++) {
        Share.Program runGateProgram = share.runGate.get(i);
        if (runGateProgram.getStart) {
          if (runGateProgram.startStatus == 0) {
            generateMultiRunGateConfig(i);
            runGateProgram.disposable = runGateProgram.start()
                .subscribeOn(JavaFxScheduler.platform())
                .doOnError(throwable -> {
                  Dialogs.error(throwable).show();
                  runGateProgram.startStatus = 9;
                })
                .subscribe(Controller.this::handleProcessMessage);
            runGateProgram.startStatus = 1;
            startRunGateOK = false;
          }
        }
      }
      if (!startRunGateOK) {
        log.debug("正在等待游戏网关全部启动..");
        return;
      }
      if (share.selGate.getStart) {
        switch (share.selGate.startStatus) {
          case 0:
            generateMultiSelGateConfig(0);
            share.selGate.disposable = share.selGate.start()
                .observeOn(JavaFxScheduler.platform())
                .doOnError(throwable -> {
                  Dialogs.error(throwable).show();
                  share.selGate.startStatus = 9;
                })
                .subscribe(Controller.this::handleProcessMessage);
            share.selGate.startStatus = 1;
            return;
          case 1:
            log.debug("正在等待角色网关一启动..");
            return;
        }
      }
      if (share.selGate1.getStart) {
        switch (share.selGate1.startStatus) {
          case 0:
            generateMultiSelGateConfig(1);
            share.selGate1.disposable = share.selGate1.start()
                .observeOn(JavaFxScheduler.platform())
                .doOnError(throwable -> {
                  Dialogs.error(throwable).show();
                  share.selGate1.startStatus = 9;
                })
                .subscribe(Controller.this::handleProcessMessage);
            share.selGate1.startStatus = 1;
            return;
          case 1:
            log.debug("正在等待角色网关二启动..");
            return;
        }
      }

      StartMode startMode = startModeComboBox.getValue();
      if (StartMode.DELAY.equals(startMode)) {
        // 是否已经等了那么久
        if ((System.currentTimeMillis() - runTick) < runTime) {
          return;
        }
      } else if (StartMode.TIMING.equals(startMode)) {
        // 是否到达指定时间
        if (System.currentTimeMillis() < runTime) {
          return;
        }
      }

      if (share.loginGate.getStart) {
        switch (share.loginGate.startStatus) {
          case 0:
            generateMultiLoginGateConfig(0);
            share.loginGate.disposable = share.loginGate.start()
                .observeOn(JavaFxScheduler.platform())
                .doOnError(throwable -> {
                  Dialogs.error(throwable).show();
                  share.loginGate.startStatus = 9;
                })
                .subscribe(Controller.this::handleProcessMessage);
            share.loginGate.startStatus = 1;
            return;
          case 1:
            log.debug("正在等待登陆网关一启动..");
            return;
        }
      }
      if (share.loginGate2.getStart) {
        switch (share.loginGate2.startStatus) {
          case 0:
            generateMultiLoginGateConfig(1);
            share.loginGate2.disposable = share.loginGate2.start()
                .observeOn(JavaFxScheduler.platform())
                .doOnError(throwable -> {
                  Dialogs.error(throwable).show();
                  share.loginGate2.startStatus = 9;
                })
                .subscribe(Controller.this::handleProcessMessage);
            share.loginGate2.startStatus = 1;
            return;
          case 1:
            log.debug("正在等待登陆网关一启动..");
            return;
        }
      }

      if (share.plugTop.getStart) {
        switch (share.plugTop.startStatus) {
          case 0:
            share.plugTop.disposable = share.plugTop.start()
                .observeOn(JavaFxScheduler.platform())
                .doOnError(throwable -> {
                  Dialogs.error(throwable).show();
                  share.plugTop.startStatus = 9;
                })
                .subscribe(Controller.this::handleProcessMessage);
            share.plugTop.startStatus = 1;
            return;
          case 1:
            log.debug("正在等待游戏排行榜插件启动..");
            return;
        }
      }

      startGameTimer.cancel();
      startGameTimer = new Timer();
      checkRunTimer.schedule(new CheckRunTask(), 1000, 1000);
      Platform.runLater(() -> startGameButton.setText(share.textStopGame));
      startState = RUNNING_STATE;
    }

    private boolean getStartRunGate() {
      for (int i = 0; i < share.runGate.size(); i++) {
        Share.Program program = share.runGate.get(i);
        if (program.getStart && program.startStatus == 1) {
          return true;
        }
      }
      return false;
    }
  }

  private void handleProcessMessage(String message) {
    String[] split = message.split(":", 2);
    int code = Integer.parseInt(split[0]);
    UUID processCode = UUID.fromString(split[1]);
    switch (code) {
      case DB_SERVER_PROCESS_CODE:
        if (share.dbServer.processCode == null) {
          share.dbServer.processCode = processCode;
          mainOutMessage("正在启动数据库服务器...");
          return;
        }
        share.dbServer.startStatus = 2;
        mainOutMessage("启动数据库服务器成功！");
        break;
      case LOGIN_SERVER_PROCESS_CODE:
        if (share.loginServer.processCode == null) {
          share.loginServer.processCode = processCode;
          mainOutMessage("正在启动账号登陆服务器...");
          return;
        }
        share.loginServer.startStatus = 2;
        mainOutMessage("启动账号登陆服务器成功！");
        break;
      case LOG_SERVER_PROCESS_CODE:
        if (share.logServer.processCode == null) {
          share.logServer.processCode = processCode;
          mainOutMessage("正在启动日志服务器...");
          return;
        }
        share.logServer.startStatus = 2;
        mainOutMessage("启动日志服务器成功！");
        break;
      case M2_SERVER_PROCESS_CODE:
        if (share.m2Server.processCode == null) {
          share.m2Server.processCode = processCode;
          mainOutMessage("正在启动游戏引擎服务器...");
          return;
        }
        share.m2Server.startStatus = 2;
        mainOutMessage("启动游戏引擎服务器成功！");
        break;
      case LOGIN_GATE_PROCESS_CODE:
        if (share.loginGate.getStart && share.loginGate.startStatus == 1) {
          if (share.loginGate.processCode == null) {
            share.loginGate.processCode = processCode;
            mainOutMessage("正在启动登陆网关一...");
            return;
          }
          if (processCode.equals(share.loginGate.processCode)) {
            share.loginGate.startStatus = 2;
            mainOutMessage("启动登陆网关一成功！");
            return;
          }
        }
        if (share.loginGate2.getStart && share.loginGate2.startStatus == 1) {
          if (share.loginGate2.processCode == null) {
            share.loginGate2.processCode = processCode;
            mainOutMessage("正在启动登陆网关二...");
            return;
          }
          if (processCode.equals(share.loginGate2.processCode)) {
            share.loginGate2.startStatus = 2;
            mainOutMessage("启动登陆网关二成功！");
            return;
          }
        }
        break;
      case SEL_GATE_PROCESS_CODE:
        if (share.selGate.getStart && share.selGate.startStatus == 1) {
          if (share.selGate.processCode == null) {
            share.selGate.processCode = processCode;
            mainOutMessage("正在启动角色网关一...");
            return;
          }
          if (processCode.equals(share.selGate.processCode)) {
            share.selGate.startStatus = 2;
            mainOutMessage("启动角色网关一成功！");
            return;
          }
        }
        if (share.selGate1.getStart && share.selGate1.startStatus == 1) {
          if (share.selGate1.processCode == null) {
            share.selGate1.processCode = processCode;
            mainOutMessage("正在启动角色网关二...");
            return;
          }
          if (processCode.equals(share.selGate.processCode)) {
            share.selGate1.startStatus = 2;
            mainOutMessage("启动角色网关二成功！");
            return;
          }
        }
        break;
      case RUN_GATE_PROCESS_CODE:
        for (int i = 0; i < share.runGate.size(); i++) {
          Share.Program program = share.runGate.get(i);
          if (program.getStart && program.startStatus == 1) {
            if (program.processCode == null) {
              program.processCode = processCode;
              mainOutMessage("正在启动游戏网关[" + (i + 1) + "]...");
              return;
            }
            if (processCode.equals(program.processCode)) {
              program.startStatus = 2;
              mainOutMessage("启动游戏网关[" + (i + 1) + "]成功！");
              return;
            }
          }
        }
        break;
      case PLUG_TOP_PROCESS_CODE:
        if (share.plugTop.processCode == null) {
          share.plugTop.processCode = processCode;
          mainOutMessage("正在启动游戏排行榜引擎...");
          return;
        }
        share.plugTop.startStatus = 2;
        mainOutMessage("启动游戏排行榜引擎成功！");
        break;
    }
  }

  public class CheckRunTask extends TimerTask {
    @Override public void run() {
      if (share.dbServer.getStart) {
        if (share.dbServer.process == null || !share.dbServer.process.isAlive()) {
          share.dbServer.disposable = share.dbServer.start()
              .observeOn(JavaFxScheduler.platform())
              .subscribe(Controller.this::handleProcessMessage);
          mainOutMessage("数据库异常关闭，已被重新启动...");
        }
      }
      if (share.loginServer.getStart) {
        if (share.loginServer.process == null || !share.loginServer.process.isAlive()) {
          share.loginServer.disposable = share.loginServer.start()
              .observeOn(JavaFxScheduler.platform())
              .subscribe(Controller.this::handleProcessMessage);
          mainOutMessage("登录服务器异常关闭，已被重新启动...");
        }
      }
      if (share.logServer.getStart) {
        if (share.logServer.process == null || !share.logServer.process.isAlive()) {
          share.logServer.disposable = share.logServer.start()
              .observeOn(JavaFxScheduler.platform())
              .subscribe(Controller.this::handleProcessMessage);
          mainOutMessage("日志服务器异常关闭，已被重新启动...");
        }
      }
      if (share.m2Server.getStart) {
        if (share.m2Server.process == null || !share.m2Server.process.isAlive()) {
          share.m2Server.disposable = share.m2Server.start()
              .observeOn(JavaFxScheduler.platform())
              .subscribe(Controller.this::handleProcessMessage);
          mainOutMessage("游戏引擎服务器异常关闭，已被重新启动...");
        }
      }
      for (int i = 0; i < share.runGate.size(); i++) {
        Share.Program program = share.runGate.get(i);
        if (program.getStart) {
          if (program.process == null || !program.process.isAlive()) {
            generateMultiRunGateConfig(i);
            program.processCode = null;
            program.disposable = program.start()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(Controller.this::handleProcessMessage);
            mainOutMessage("游戏网关[" + (i + 1) + "]异常关闭，已被重新启动...");
          }
        }
      }
      if (share.selGate.getStart) {
        if (share.selGate.process == null || !share.selGate.process.isAlive()) {
          generateMultiSelGateConfig(0);
          share.selGate.processCode = null;
          share.selGate.disposable = share.selGate.start()
              .observeOn(JavaFxScheduler.platform())
              .subscribe(Controller.this::handleProcessMessage);
          mainOutMessage("角色网关一异常关闭，已被重新启动...");
        }
      }
      if (share.selGate1.getStart) {
        if (share.selGate1.process == null || !share.selGate1.process.isAlive()) {
          generateMultiSelGateConfig(1);
          share.selGate1.processCode = null;
          share.selGate1.disposable = share.selGate1.start()
              .observeOn(JavaFxScheduler.platform())
              .subscribe(Controller.this::handleProcessMessage);
          mainOutMessage("角色网关二异常关闭，已被重新启动...");
        }
      }
      if (share.loginGate.getStart) {
        if (share.loginGate.process == null || !share.loginGate.process.isAlive()) {
          generateMultiLoginGateConfig(0);
          share.loginGate.processCode = null;
          share.loginGate.disposable = share.loginGate.start()
              .observeOn(JavaFxScheduler.platform())
              .subscribe(Controller.this::handleProcessMessage);
          mainOutMessage("登录网关一异常关闭，已被重新启动...");
        }
      }
      if (share.loginGate2.getStart) {
        if (share.loginGate2.process == null || !share.loginGate2.process.isAlive()) {
          generateMultiLoginGateConfig(1);
          share.loginGate2.processCode = null;
          share.loginGate2.disposable = share.loginGate2.start()
              .observeOn(JavaFxScheduler.platform())
              .subscribe(Controller.this::handleProcessMessage);
          mainOutMessage("登录网关二异常关闭，已被重新启动...");
        }
      }
      if (share.plugTop.getStart) {
        if (share.plugTop.process == null || !share.plugTop.process.isAlive()) {
          share.plugTop.processCode = null;
          share.plugTop.disposable = share.plugTop.start()
              .observeOn(JavaFxScheduler.platform())
              .subscribe(Controller.this::handleProcessMessage);
          mainOutMessage("排行榜插件异常关闭，已被重新启动...");
        }
      }
    }
  }

  public class StopGameTask extends TimerTask {

    @Override public void run() {
      if (share.loginGate.getStart && share.loginGate.startStatus > 1) {
        if (share.loginGate.process != null && share.loginGate.process.isAlive()) {
          if (share.loginGate.startStatus == 3) {
            if ((System.currentTimeMillis() - share.stopTick) > share.stopTimeout) {
              share.loginGate.stop();
              mainOutMessage("正常关闭超时，登陆网关一已被强行停止...");
            }
            return;
          }
          share.loginGate.sendMessage(QUIT_CODE);
          share.stopTick = System.currentTimeMillis();
          share.loginGate.startStatus = 3;
          return;
        } else {
          share.loginGate.startStatus = 0;
          mainOutMessage("登陆网关一已停止...");
        }
      }

      if (share.loginGate2.getStart && share.loginGate2.startStatus > 1) {
        if (share.loginGate2.process != null && share.loginGate2.process.isAlive()) {
          if (share.loginGate2.startStatus == 3) {
            if ((System.currentTimeMillis() - share.stopTick) > share.stopTimeout) {
              share.loginGate2.stop();
              mainOutMessage("正常关闭超时，登陆网关二已被强行停止...");
            }
            return;
          }
          share.loginGate2.sendMessage(QUIT_CODE);
          share.stopTick = System.currentTimeMillis();
          share.loginGate2.startStatus = 3;
          return;
        } else {
          share.loginGate2.startStatus = 0;
          mainOutMessage("登陆网关二已停止...");
        }
      }

      if (share.selGate.getStart && share.selGate.startStatus > 1) {
        if (share.selGate.process != null && share.selGate.process.isAlive()) {
          if (share.selGate.startStatus == 3) {
            if ((System.currentTimeMillis() - share.stopTick) > share.stopTimeout) {
              share.selGate.stop();
              mainOutMessage("正常关闭超时，角色网关一已被强行停止...");
            }
            return;
          }
          share.selGate.sendMessage(QUIT_CODE);
          share.stopTick = System.currentTimeMillis();
          share.selGate.startStatus = 3;
          return;
        } else {
          share.selGate.startStatus = 0;
          mainOutMessage("角色网关一已停止...");
        }
      }

      if (share.selGate1.getStart && share.selGate1.startStatus > 1) {
        if (share.selGate1.process != null && share.selGate1.process.isAlive()) {
          if (share.selGate1.startStatus == 3) {
            if ((System.currentTimeMillis() - share.stopTick) > share.stopTimeout) {
              share.selGate1.stop();
              mainOutMessage("正常关闭超时，角色网关二已被强行停止...");
            }
            return;
          }
          share.selGate1.sendMessage(QUIT_CODE);
          share.stopTick = System.currentTimeMillis();
          share.selGate1.startStatus = 3;
          return;
        } else {
          share.selGate1.startStatus = 0;
          mainOutMessage("角色网关二已停止...");
        }
      }

      for (int i = 0; i < share.runGate.size(); i++) {
        Share.Program program = share.runGate.get(i);
        if (program.getStart && program.startStatus > 1) {
          if (program.process != null && program.process.isAlive()) {
            if (program.startStatus == 3) {
              if ((System.currentTimeMillis() - share.stopTick) > share.stopTimeout) {
                program.stop();
                mainOutMessage("正常关闭超时，游戏网关[" + (i + 1) + "]已被强行停止...");
              }
              return;
            }
            program.sendMessage(QUIT_CODE);
            // fixme 全局变量不能用于多个游戏网关的判断，这里会出现 bug
            share.stopTick = System.currentTimeMillis();
            program.startStatus = 3;
            return;
          } else {
            program.startStatus = 0;
            mainOutMessage("游戏网关[" + (i + 1) + "]已停止...");
          }
        }
      }

      if (getStopRunGate()) {
        gateStopped = false;
        return;
      }

      if (share.m2Server.getStart && share.m2Server.startStatus > 1) {
        if (!gateStopped) {
          gateStopped = true;
          gateStopTick = System.currentTimeMillis() + 5000;
          mainOutMessage("网关已全部关闭，延时5秒关闭游戏引擎...");
          return;
        }
        if (gateStopTick > System.currentTimeMillis()) {
          return;
        }

        if (share.m2Server.process != null && share.m2Server.process.isAlive()) {
          if (share.m2Server.startStatus == 3) {
            return;
          }
          share.m2Server.sendMessage(QUIT_CODE);
          share.stopTick = System.currentTimeMillis();
          share.m2Server.startStatus = 3;
          return;
        } else {
          share.m2Server.startStatus = 0;
          mainOutMessage("游戏引擎主程序已停止...");
        }
      }

      if (share.loginServer.getStart && share.loginServer.startStatus > 1) {
        if (share.loginServer.process != null && share.loginServer.process.isAlive()) {
          if (share.loginServer.startStatus == 3) {
            if ((System.currentTimeMillis() - share.stopTick) > share.stopTimeout) {
              // todo 1000 delay
              share.loginServer.stop();
              mainOutMessage("正常关闭超时，登陆服务器已被强行停止...");
            }
            return;
          }
          share.loginServer.sendMessage(QUIT_CODE);
          share.stopTick = System.currentTimeMillis();
          share.loginServer.startStatus = 3;
          return;
        } else {
          share.loginServer.startStatus = 0;
          mainOutMessage("登陆服务器已停止...");
        }
      }

      if (share.logServer.getStart && share.logServer.startStatus > 1) {
        if (share.logServer.process != null && share.logServer.process.isAlive()) {
          if (share.logServer.startStatus == 3) {
            if ((System.currentTimeMillis() - share.stopTick) > share.stopTimeout) {
              share.logServer.stop();
              mainOutMessage("正常关闭超时，日志服务器已被强行停止...");
            }
            return;
          }
          share.logServer.sendMessage(QUIT_CODE);
          share.stopTick = System.currentTimeMillis();
          share.logServer.startStatus = 3;
          return;
        } else {
          share.logServer.startStatus = 0;
          mainOutMessage("日志服务器已停止...");
        }
      }

      if (share.dbServer.getStart && share.dbServer.startStatus > 1) {
        if (share.dbServer.process != null && share.dbServer.process.isAlive()) {
          if (share.dbServer.startStatus == 3) {
            if ((System.currentTimeMillis() - share.stopTick) > share.stopTimeout) {
              share.dbServer.stop();
              mainOutMessage("正常关闭超时，数据库服务器已被强行停止...");
            }
            return;
          }
          share.dbServer.sendMessage(QUIT_CODE);
          share.stopTick = System.currentTimeMillis();
          share.dbServer.startStatus = 3;
          return;
        } else {
          share.dbServer.startStatus = 0;
          mainOutMessage("数据库服务器已停止...");
        }
      }

      mainOutMessage("所有程序停止完毕！");
      stopGameTimer.cancel();
      stopGameTimer = new Timer();
      Platform.runLater(() -> startGameButton.setText(share.textStartGame));
      startState = STOPPED_STATE;
    }

    private boolean getStopRunGate() {
      for (int i = 0; i < share.runGate.size(); i++) {
        Share.Program program = share.runGate.get(i);
        if (program.getStart && program.startStatus > 1) {
          return true;
        }
      }
      return false;
    }
  }
}
