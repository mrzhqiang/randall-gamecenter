package randall.gamecenter;

import com.google.common.base.Strings;
import helper.DateTimeHelper;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.ini4j.Ini;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import randall.gamecenter.util.Dialogs;
import randall.gamecenter.util.Files;
import randall.gamecenter.util.Networks;

import static randall.gamecenter.Share.ALL_IP_ADDRESS;
import static randall.gamecenter.Share.DB_SERVER_SECTION_NAME_2;
import static randall.gamecenter.Share.LOGIN_GATE_SECTION_NAME_2;
import static randall.gamecenter.Share.LOGIN_SRV_SECTION_NAME_2;
import static randall.gamecenter.Share.LOG_SERVER_SECTION_2;
import static randall.gamecenter.Share.M2_SERVER_CONFIG_FILE;
import static randall.gamecenter.Share.M2_SERVER_SECTION_NAME_1;
import static randall.gamecenter.Share.M2_SERVER_SECTION_NAME_2;
import static randall.gamecenter.Share.ONLINE_USER_LIMIT;
import static randall.gamecenter.Share.PRIMARY_IP_ADDRESS;
import static randall.gamecenter.Share.RUN_GATE_SECTION_NAME_2;
import static randall.gamecenter.Share.SECOND_IP_ADDRESS;
import static randall.gamecenter.Share.SEL_GATE_SECTION_NAME_2;
import static randall.gamecenter.Share.SERVER_CONFIG_FILE;

/**
 * 控制器。
 *
 * @author mrzhqiang
 */
public final class Controller {
  private static final Logger LOGGER = LoggerFactory.getLogger("randall");

  public static final int SG_START_NOW = 1001;
  public static final int SG_START_OK = 1002;

  public static final int STOPPED_STATE = 0;
  public static final int STARTING_STATE = 1;
  public static final int RUNNING_STATE = 2;
  public static final int STOPPING_STATE = 3;
  public static final int ERROR_STATE = 9;

  public TabPane mainTabPane;
  public CheckBox dbServerCheckBox;
  public CheckBox loginSrvCheckBox;
  public CheckBox m2ServerCheckBox;
  public CheckBox logServerCheckBox;
  public CheckBox gameGateCheckBox1;
  public CheckBox gameGateCheckBox2;
  public CheckBox gameGateCheckBox3;
  public CheckBox gameGateCheckBox4;
  public CheckBox gameGateCheckBox5;
  public CheckBox gameGateCheckBox6;
  public CheckBox gameGateCheckBox7;
  public CheckBox gameGateCheckBox8;
  public CheckBox selGateCheckBox1;
  public CheckBox selGateCheckBox2;
  public CheckBox loginGateCheckBox;
  public ComboBox<StartMode> startModeComboBox;
  public Spinner<Integer> hoursSpinner;
  public Spinner<Integer> minutesSpinner;
  public TextArea gameInfoTextArea;
  public Button btnStartGame;

  public TabPane configTabPane;
  public TextField primaryAddressTextField;
  public CheckBox doubleAddressCheckBox;
  public TextField secondAddressTextField;
  public CheckBox dynamicAddressCheckBox;
  public TextField gameNameTextField;
  public TextField dbNameTextField;
  public TextField gameDirTextField;
  public Spinner<Integer> allPortPlusSpinner;
  public CheckBox closeWuxingCheckBox;
  public CheckBox openLoginGateCheckBox;
  public TextField loginGateFormXTextField;
  public TextField loginGateFormYTextField;
  public TextField loginGatePortTextField;
  public CheckBox openSelGateCheckBox1;
  public CheckBox openSelGateCheckBox2;
  public TextField selGateFormXTextField;
  public TextField selGateFormYTextField;
  public TextField selGatePortTextField1;
  public TextField selGatePortTextField2;
  public CheckBox openRunGateCheckBox1;
  public CheckBox openRunGateCheckBox2;
  public CheckBox openRunGateCheckBox3;
  public CheckBox openRunGateCheckBox4;
  public CheckBox openRunGateCheckBox5;
  public CheckBox openRunGateCheckBox6;
  public CheckBox openRunGateCheckBox7;
  public CheckBox openRunGateCheckBox8;
  public TextField runGateFormXTextField;
  public TextField runGateFormYTextField;
  public TextField runGatePortTextField1;
  public TextField runGatePortTextField2;
  public TextField runGatePortTextField3;
  public TextField runGatePortTextField4;
  public TextField runGatePortTextField5;
  public TextField runGatePortTextField6;
  public TextField runGatePortTextField7;
  public TextField runGatePortTextField8;
  public CheckBox openLoginSrvCheckBox;
  public TextField loginSrvFormXTextField;
  public TextField loginSrvFormYTextField;
  public TextField loginSrvGatePortTextField;
  public TextField loginSrvMonPortTextField;
  public TextField loginSrvServerPortTextField;
  public CheckBox openDbServerCheckBox;
  public TextField dbServerFormXTextField;
  public TextField dbServerFormYTextField;
  public TextField dbServerGatePortTextField;
  public TextField dbServerServerPortTextField;
  public CheckBox openLogServerCheckBox;
  public TextField logServerFormXTextField;
  public TextField logServerFormYTextField;
  public TextField logServerGatePortTextField;
  public CheckBox openM2ServerCheckBox;
  public TextField m2ServerFormXTextField;
  public TextField m2ServerFormYTextField;
  public TextField m2ServerGatePortTextField;
  public TextField m2ServerServerPortTextField;
  public CheckBox openPlugTopCheckBox;
  public TextField plugTopFormXTextField;
  public TextField plugTopFormYTextField;
  public CheckBox plugTopCheckBox;

  private boolean opened = false;
  // 0 -- default; 1 -- starting; 2 -- running; 3 -- stopping; 9 -- error
  private int startState = 0;
  // 0 -- disabled; 1 -- enabled;
  private int backupState = 0;

  private long refTick;
  private long showTick;
  private long runTick;

  private final Share share = new Share();
  private final Timer startGameTimer = new Timer();
  private final Timer stopGameTimer = new Timer();
  private final Timer checkRunTimer = new Timer();

  @FXML
  public void initialize() {
    opened = false;
    mainTabPane.getSelectionModel().select(0);
    configTabPane.getSelectionModel().select(0);
    startState = 0;
    backupState = 0;
    gameInfoTextArea.clear();
    refTick = System.currentTimeMillis();
    share.loadConfig();
    loadBackupList();
    refBackupListToView();
    if (!startService()) {
      Platform.exit();
    }
    // 额外添加的监听器
    addListener();
    refGameConsole();
    // todo backup check from autoRunBakEnabled

    opened = true;
    if (share.autoRunBakEnabled) {
      // todo run backup service
    }
    startModeComboBox.setItems(FXCollections.observableArrayList(StartMode.values()));
    startModeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
      hoursSpinner.setDisable(newValue.equals(StartMode.NORMAL));
      minutesSpinner.setDisable(newValue.equals(StartMode.NORMAL));
    });
    startModeComboBox.getSelectionModel().select(StartMode.NORMAL);
  }

  private void addListener() {
    doubleAddressCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      secondAddressTextField.setDisable(newValue);
    });
    dynamicAddressCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      primaryAddressTextField.setDisable(newValue);
      secondAddressTextField.setDisable(newValue);
    });
    allPortPlusSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue > 0) {
        // 显示的内容，所有端口增加指定值，这样不怕覆盖已有的配置文件
        LOGGER.info("所有端口增加 " + newValue);
      } else {
        // todo 从配置文件还原显示的内容
      }
    });
  }

  private void refGameConsole() {
    opened = false;

    // 刷新控制台按钮的选中状态
    m2ServerCheckBox.setSelected(share.config.m2Server.getStart);
    dbServerCheckBox.setSelected(share.config.dbServer.getStart);
    loginSrvCheckBox.setSelected(share.config.loginSrv.getStart);
    logServerCheckBox.setSelected(share.config.logServer.getStart);
    loginGateCheckBox.setSelected(share.config.loginGate.getStart);
    selGateCheckBox1.setSelected(share.config.selGate.getStart1);
    selGateCheckBox2.setSelected(share.config.selGate.getStart2);
    gameGateCheckBox1.setSelected(share.config.runGate.getStart[0]);
    gameGateCheckBox2.setSelected(share.config.runGate.getStart[1]);
    gameGateCheckBox3.setSelected(share.config.runGate.getStart[2]);
    gameGateCheckBox4.setSelected(share.config.runGate.getStart[3]);
    gameGateCheckBox5.setSelected(share.config.runGate.getStart[4]);
    gameGateCheckBox6.setSelected(share.config.runGate.getStart[5]);
    gameGateCheckBox7.setSelected(share.config.runGate.getStart[6]);
    gameGateCheckBox8.setSelected(share.config.runGate.getStart[7]);
    plugTopCheckBox.setSelected(share.config.plugTop.getStart);

    // 第一步 基本设置
    gameDirTextField.setText(share.gameDirectory);
    dbNameTextField.setText(share.heroDBName);
    gameNameTextField.setText(share.gameName);
    primaryAddressTextField.setText(share.extIPAddr);
    secondAddressTextField.setText(share.extIPAddr2);
    dynamicAddressCheckBox.setSelected(share.ip2Enabled);
    closeWuxingCheckBox.setSelected(share.closeWuXinEnabled);
    // 第二步 登录网关
    loginGateFormXTextField.setText(String.valueOf(share.config.loginGate.mainFormX));
    loginGateFormYTextField.setText(String.valueOf(share.config.loginGate.mainFormY));
    openLoginGateCheckBox.setSelected(share.config.loginGate.getStart);
    loginGatePortTextField.setText(String.valueOf(share.config.loginGate.gatePort));
    // 第三步 角色网关
    selGateFormXTextField.setText(String.valueOf(share.config.selGate.mainFormX));
    selGateFormYTextField.setText(String.valueOf(share.config.selGate.mainFormY));
    openSelGateCheckBox1.setSelected(share.config.selGate.getStart1);
    openSelGateCheckBox2.setSelected(share.config.selGate.getStart2);
    selGatePortTextField1.setText(String.valueOf(share.config.selGate.gatePort[0]));
    selGatePortTextField2.setText(String.valueOf(share.config.selGate.gatePort[1]));
    // 第四步 游戏网关
    runGateFormXTextField.setText(String.valueOf(share.config.runGate.mainFormX));
    runGateFormYTextField.setText(String.valueOf(share.config.runGate.mainFormY));
    openRunGateCheckBox1.setSelected(share.config.runGate.getStart[0]);
    openRunGateCheckBox2.setSelected(share.config.runGate.getStart[1]);
    openRunGateCheckBox3.setSelected(share.config.runGate.getStart[2]);
    openRunGateCheckBox4.setSelected(share.config.runGate.getStart[3]);
    openRunGateCheckBox5.setSelected(share.config.runGate.getStart[4]);
    openRunGateCheckBox6.setSelected(share.config.runGate.getStart[5]);
    openRunGateCheckBox7.setSelected(share.config.runGate.getStart[6]);
    openRunGateCheckBox8.setSelected(share.config.runGate.getStart[7]);
    runGatePortTextField1.setText(String.valueOf(share.config.runGate.gatePort[0]));
    runGatePortTextField2.setText(String.valueOf(share.config.runGate.gatePort[1]));
    runGatePortTextField3.setText(String.valueOf(share.config.runGate.gatePort[2]));
    runGatePortTextField4.setText(String.valueOf(share.config.runGate.gatePort[3]));
    runGatePortTextField5.setText(String.valueOf(share.config.runGate.gatePort[4]));
    runGatePortTextField6.setText(String.valueOf(share.config.runGate.gatePort[5]));
    runGatePortTextField7.setText(String.valueOf(share.config.runGate.gatePort[6]));
    runGatePortTextField8.setText(String.valueOf(share.config.runGate.gatePort[7]));
    // 第五步 登录服务器
    loginSrvFormXTextField.setText(String.valueOf(share.config.loginSrv.mainFormX));
    loginSrvFormYTextField.setText(String.valueOf(share.config.loginSrv.mainFormY));
    openLoginSrvCheckBox.setSelected(share.config.loginSrv.getStart);
    loginSrvGatePortTextField.setText(String.valueOf(share.config.loginSrv.gatePort));
    loginSrvServerPortTextField.setText(String.valueOf(share.config.loginSrv.serverPort));
    loginSrvMonPortTextField.setText(String.valueOf(share.config.loginSrv.monPort));
    // 第六步 数据库服务器
    dbServerFormXTextField.setText(String.valueOf(share.config.dbServer.mainFormX));
    dbServerFormYTextField.setText(String.valueOf(share.config.dbServer.mainFormY));
    openDbServerCheckBox.setSelected(share.config.dbServer.getStart);
    dbServerGatePortTextField.setText(String.valueOf(share.config.dbServer.gatePort));
    dbServerServerPortTextField.setText(String.valueOf(share.config.dbServer.serverPort));
    // 第七步 游戏日志服务器
    logServerFormXTextField.setText(String.valueOf(share.config.logServer.mainFormX));
    logServerFormYTextField.setText(String.valueOf(share.config.logServer.mainFormY));
    openLogServerCheckBox.setSelected(share.config.logServer.getStart);
    logServerGatePortTextField.setText(String.valueOf(share.config.logServer.port));
    // 第八步 游戏主引擎服务器
    m2ServerFormXTextField.setText(String.valueOf(share.config.m2Server.mainFormX));
    m2ServerFormYTextField.setText(String.valueOf(share.config.m2Server.mainFormY));
    openM2ServerCheckBox.setSelected(share.config.m2Server.getStart);
    m2ServerGatePortTextField.setText(String.valueOf(share.config.m2Server.gatePort));
    m2ServerServerPortTextField.setText(String.valueOf(share.config.m2Server.msgSrvPort));
    // 第九步 排行榜插件
    plugTopFormXTextField.setText(String.valueOf(share.config.plugTop.mainFormX));
    plugTopFormYTextField.setText(String.valueOf(share.config.plugTop.mainFormY));
    openPlugTopCheckBox.setSelected(share.config.plugTop.getStart);

    opened = true;
  }

  private boolean startService() {
    mainOutMessage("正在启动游戏客户端控制器...");
    showTick = System.currentTimeMillis();
    mainOutMessage("游戏控制台启动完成...");
    return true;
  }

  private void mainOutMessage(String message) {
    LOGGER.info(message);
    gameInfoTextArea.appendText(
        "[" + DateTimeHelper.format(new Date()) + "] --" + message + System.lineSeparator());
  }

  public void onDBServerClicked() {
    share.config.dbServer.getStart = dbServerCheckBox.isSelected();
    // todo data binding
    //openDbServerCheckBox.setSelected(share.config.dbServer.getStart);
  }

  public void onLoginSrvClicked() {
    share.config.loginSrv.getStart = loginSrvCheckBox.isSelected();
  }

  public void onM2ServerClicked() {
    share.config.m2Server.getStart = m2ServerCheckBox.isSelected();
  }

  public void onLogServerClicked() {
    share.config.logServer.getStart = logServerCheckBox.isSelected();
  }

  public void onRunGate1Clicked() {
    share.config.runGate.getStart[0] = gameGateCheckBox1.isSelected();
  }

  public void onRunGate2Clicked() {
    share.config.runGate.getStart[1] = gameGateCheckBox2.isSelected();
  }

  public void onRunGate3Clicked() {
    share.config.runGate.getStart[2] = gameGateCheckBox3.isSelected();
  }

  public void onRunGate4Clicked() {
    share.config.runGate.getStart[3] = gameGateCheckBox4.isSelected();
  }

  public void onRunGate5Clicked() {
    share.config.runGate.getStart[4] = gameGateCheckBox5.isSelected();
  }

  public void onRunGate6Clicked() {
    share.config.runGate.getStart[5] = gameGateCheckBox6.isSelected();
  }

  public void onRunGate7Clicked() {
    share.config.runGate.getStart[6] = gameGateCheckBox7.isSelected();
  }

  public void onRunGate8Clicked() {
    share.config.runGate.getStart[7] = gameGateCheckBox8.isSelected();
  }

  public void onSelGate1Clicked() {
    share.config.selGate.getStart1 = selGateCheckBox1.isSelected();
  }

  public void onSelGate2Clicked() {
    share.config.selGate.getStart2 = selGateCheckBox2.isSelected();
  }

  public void onLoginGateClicked() {
    share.config.loginGate.getStart = loginGateCheckBox.isSelected();
  }

  public void onPlugTopClicked() {
    share.config.plugTop.getStart = plugTopCheckBox.isSelected();
  }

  public void onStartGameClicked() {
    switch (startState) {
      case STOPPED_STATE:
        Dialogs.confirm("是否确认启动游戏服务器？")
            .ifPresent(buttonType -> startGame());
        break;
      case STARTING_STATE:
        break;
      case RUNNING_STATE:
        break;
      case STOPPING_STATE:
        break;
      case ERROR_STATE:
        break;
    }
  }

  private void startGame() {
    runTick = System.currentTimeMillis();
    share.dbServer.getStart = share.config.dbServer.getStart;
    share.dbServer.reStart = true;
    share.dbServer.directory = share.gameDirectory + "DBServer\\";
    share.dbServer.programFile = share.config.dbServer.programFile;
    share.dbServer.mainFormX = share.config.dbServer.mainFormX;
    share.dbServer.mainFormY = share.config.dbServer.mainFormY;

    share.loginServer.getStart = share.config.loginSrv.getStart;
    share.loginServer.reStart = true;
    share.loginServer.directory = share.gameDirectory + "LoginSrv\\";
    share.loginServer.programFile = share.config.loginSrv.programFile;
    share.loginServer.mainFormX = share.config.loginSrv.mainFormX;
    share.loginServer.mainFormY = share.config.loginSrv.mainFormY;

    share.logServer.getStart = share.config.logServer.getStart;
    share.logServer.reStart = true;
    share.logServer.directory = share.gameDirectory + "LogServer\\";
    share.logServer.programFile = share.config.logServer.programFile;
    share.logServer.mainFormX = share.config.logServer.mainFormX;
    share.logServer.mainFormY = share.config.logServer.mainFormY;

    share.m2Server.getStart = share.config.m2Server.getStart;
    share.m2Server.reStart = true;
    share.m2Server.directory = share.gameDirectory + "Mir200\\";
    share.m2Server.programFile = share.config.m2Server.programFile;
    share.m2Server.mainFormX = share.config.m2Server.mainFormX;
    share.m2Server.mainFormY = share.config.m2Server.mainFormY;

    for (int i = 0; i < share.runGate.size(); i++) {
      share.runGate.get(i).startStatus = 0;
      share.runGate.get(i).getStart = share.config.runGate.getStart[i];
      share.runGate.get(i).reStart = true;
      share.runGate.get(i).directory = share.gameDirectory + "RunGate\\";
      share.runGate.get(i).programFile = share.config.runGate.programFile;
      if ((i + 1) % 2 == 0) {
        share.runGate.get(i).mainFormX = share.config.runGate.mainFormX + 276;
      } else {
        share.runGate.get(i).mainFormX = share.config.runGate.mainFormX;
      }
      if (i == 2 || i == 3 || i == 6 || i == 7) {
        share.runGate.get(i).mainFormY = share.config.runGate.mainFormY + 187;
      } else {
        share.runGate.get(i).mainFormY = share.config.runGate.mainFormY;
      }
    }

    share.selGate.getStart = share.config.selGate.getStart1;
    share.selGate.reStart = true;
    share.selGate.directory = share.gameDirectory + "SelGate\\";
    share.selGate.programFile = share.config.selGate.programFile;
    share.selGate.mainFormX = share.config.selGate.mainFormX;
    share.selGate.mainFormY = share.config.selGate.mainFormY;

    share.selGate1.getStart = share.config.selGate.getStart2;
    share.selGate1.reStart = true;
    share.selGate1.directory = share.gameDirectory + "SelGate\\";
    share.selGate1.programFile = share.config.selGate.programFile;
    share.selGate1.mainFormX = share.config.selGate.mainFormX;
    share.selGate1.mainFormY = share.config.selGate.mainFormY;

    share.loginGate.getStart = share.config.loginGate.getStart;
    share.loginGate.reStart = true;
    share.loginGate.directory = share.gameDirectory + "LoginGate\\";
    share.loginGate.programFile = share.config.loginGate.programFile;
    share.loginGate.mainFormX = share.config.loginGate.mainFormX;
    share.loginGate.mainFormY = share.config.loginGate.mainFormY;

    share.loginGate2.getStart = share.config.loginGate.getStart && share.ip2Enabled;
    share.loginGate2.reStart = true;
    share.loginGate2.directory = share.gameDirectory + "M2Server\\";
    share.loginGate2.programFile = share.config.loginGate.programFile;
    share.loginGate2.mainFormX = share.config.loginGate.mainFormX;
    share.loginGate2.mainFormY = share.config.loginGate.mainFormY;

    share.plugTop.getStart = share.config.plugTop.getStart;
    share.plugTop.reStart = true;
    share.plugTop.directory = share.gameDirectory + "Mir200\\";
    share.plugTop.programFile = share.config.plugTop.programFile;
    share.plugTop.mainFormX = share.config.plugTop.mainFormX;
    share.plugTop.mainFormY = share.config.plugTop.mainFormY;

    dbServerCheckBox.setSelected(share.config.dbServer.getStart);
    loginSrvCheckBox.setSelected(share.config.loginSrv.getStart);
    m2ServerCheckBox.setSelected(share.config.m2Server.getStart);
    logServerCheckBox.setSelected(share.config.logServer.getStart);
    loginGateCheckBox.setSelected(share.config.loginGate.getStart);
    selGateCheckBox1.setSelected(share.config.selGate.getStart1);
    selGateCheckBox2.setSelected(share.config.selGate.getStart2);
    gameGateCheckBox1.setSelected(share.config.runGate.getStart[0]);
    gameGateCheckBox2.setSelected(share.config.runGate.getStart[1]);
    gameGateCheckBox3.setSelected(share.config.runGate.getStart[2]);
    gameGateCheckBox4.setSelected(share.config.runGate.getStart[3]);
    gameGateCheckBox5.setSelected(share.config.runGate.getStart[4]);
    gameGateCheckBox6.setSelected(share.config.runGate.getStart[5]);
    gameGateCheckBox7.setSelected(share.config.runGate.getStart[6]);
    gameGateCheckBox8.setSelected(share.config.runGate.getStart[7]);

    btnStartGame.setText(share.textCancelStartGame);
    startState = STARTING_STATE;

    startGameTimer.schedule(new StartGameTask(), 1000, 1000);
  }

  private void loadBackupList() {
    // todo 加载备份列表
  }

  private void refBackupListToView() {

  }

  public void onDestroy() {
    startGameTimer.cancel();
    stopGameTimer.cancel();
    checkRunTimer.cancel();
  }

  public void onReloadAllConfigClicked(ActionEvent actionEvent) {

  }

  public void onNextBasicConfigClicked() {
    String gameDir = gameDirTextField.getText().trim();
    if (Strings.isNullOrEmpty(gameDir)) {
      Dialogs.warn("游戏目录输入不正确！！").show();
      gameDirTextField.requestFocus();
      return;
    }
    if (!gameDir.endsWith("\\")) {
      Dialogs.warn("游戏目录必须以“\\”结尾！！").show();
      gameDirTextField.requestFocus();
      return;
    }
    String gameName = gameNameTextField.getText().trim();
    if (Strings.isNullOrEmpty(gameName)) {
      Dialogs.warn("游戏名称输入不正确！！").show();
      gameNameTextField.requestFocus();
      return;
    }
    String dbName = dbNameTextField.getText().trim();
    if (Strings.isNullOrEmpty(dbName)) {
      Dialogs.warn("数据库名称输入不正确！！").show();
      dbNameTextField.requestFocus();
      return;
    }
    String ipAddress1 = primaryAddressTextField.getText().trim();
    if (Strings.isNullOrEmpty(ipAddress1) || !Networks.isAddressV4(ipAddress1)) {
      Dialogs.warn("游戏 IP 地址输入不正确！！").show();
      primaryAddressTextField.requestFocus();
      return;
    }
    boolean doubleAddress = doubleAddressCheckBox.isSelected();
    String ipAddress2 = secondAddressTextField.getText().trim();
    if (doubleAddress && (Strings.isNullOrEmpty(ipAddress2) || !Networks.isAddressV4(ipAddress2))) {
      Dialogs.warn("游戏 IP 地址输入不正确！！").show();
      secondAddressTextField.requestFocus();
      return;
    }

    share.gameDirectory = gameDir;
    share.gameName = gameName;
    share.heroDBName = dbName;
    share.extIPAddr = ipAddress1;
    share.ip2Enabled = doubleAddress;
    share.extIPAddr2 = ipAddress2;
    share.closeWuXinEnabled = closeWuxingCheckBox.isSelected();

    configTabPane.getSelectionModel().selectNext();
  }

  public void onDefaultBasicConfigClicked(ActionEvent actionEvent) {

  }

  public void onPreviousLoginGateConfigClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  public void onNextLoginGateConfigClicked() {
    int port = Integer.parseInt(loginGatePortTextField.getText().trim());
    if (!Networks.isPort(port)) {
      Dialogs.warn("网关端口设置错误！！").show();
      loginGatePortTextField.requestFocus();
      return;
    }
    share.config.loginGate.gatePort = port;
    configTabPane.getSelectionModel().selectNext();
  }

  public void onDefaultLoginGateConfigClicked(ActionEvent actionEvent) {

  }

  public void onPreviousSelGateConfigClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  public void onNextSelGateConfigClicked() {
    int port1 = Integer.parseInt(selGatePortTextField1.getText().trim());
    if (!Networks.isPort(port1)) {
      Dialogs.warn("网关端口设置错误！！").show();
      selGatePortTextField1.requestFocus();
      return;
    }
    share.config.selGate.gatePort[0] = port1;

    int port2 = Integer.parseInt(selGatePortTextField2.getText().trim());
    if (!Networks.isPort(port2)) {
      Dialogs.warn("网关端口设置错误！！").show();
      selGatePortTextField2.requestFocus();
      return;
    }
    share.config.selGate.gatePort[1] = port2;

    configTabPane.getSelectionModel().selectNext();
  }

  public void onDefaultSelGateConfigClicked(ActionEvent actionEvent) {

  }

  public void onPreviousRunGateConfigClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  public void onNextRunGateConfigClicked() {
    int port1 = Integer.parseInt(runGatePortTextField1.getText().trim());
    if (!Networks.isPort(port1)) {
      Dialogs.warn("网关一端口设置错误！！").show();
      runGatePortTextField1.requestFocus();
      return;
    }
    int port2 = Integer.parseInt(runGatePortTextField2.getText().trim());
    if (!Networks.isPort(port2)) {
      Dialogs.warn("网关二端口设置错误！！").show();
      runGatePortTextField2.requestFocus();
      return;
    }
    int port3 = Integer.parseInt(runGatePortTextField3.getText().trim());
    if (!Networks.isPort(port3)) {
      Dialogs.warn("网关三端口设置错误！！").show();
      runGatePortTextField3.requestFocus();
      return;
    }
    int port4 = Integer.parseInt(runGatePortTextField4.getText().trim());
    if (!Networks.isPort(port4)) {
      Dialogs.warn("网关四端口设置错误！！").show();
      runGatePortTextField4.requestFocus();
      return;
    }
    int port5 = Integer.parseInt(runGatePortTextField5.getText().trim());
    if (!Networks.isPort(port5)) {
      Dialogs.warn("网关五端口设置错误！！").show();
      runGatePortTextField5.requestFocus();
      return;
    }
    int port6 = Integer.parseInt(runGatePortTextField6.getText().trim());
    if (!Networks.isPort(port6)) {
      Dialogs.warn("网关六端口设置错误！！").show();
      runGatePortTextField6.requestFocus();
      return;
    }
    int port7 = Integer.parseInt(runGatePortTextField7.getText().trim());
    if (!Networks.isPort(port7)) {
      Dialogs.warn("网关七端口设置错误！！").show();
      runGatePortTextField7.requestFocus();
      return;
    }
    int port8 = Integer.parseInt(runGatePortTextField8.getText().trim());
    if (!Networks.isPort(port8)) {
      Dialogs.warn("网关八端口设置错误！！").show();
      runGatePortTextField8.requestFocus();
      return;
    }
    share.config.runGate.gatePort[0] = port1;
    share.config.runGate.gatePort[1] = port2;
    share.config.runGate.gatePort[2] = port3;
    share.config.runGate.gatePort[3] = port4;
    share.config.runGate.gatePort[4] = port5;
    share.config.runGate.gatePort[5] = port6;
    share.config.runGate.gatePort[6] = port7;
    share.config.runGate.gatePort[7] = port8;

    configTabPane.getSelectionModel().selectNext();
  }

  public void onDefaultRunGateConfigClicked(ActionEvent actionEvent) {

  }

  public void onPreviousLoginSrvConfigClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  public void onNextLoginSrvConfigClicked() {
    int gatePort = Integer.parseInt(loginSrvGatePortTextField.getText().trim());
    if (!Networks.isPort(gatePort)) {
      Dialogs.warn("网关端口设置错误！！").show();
      loginGatePortTextField.requestFocus();
      return;
    }
    int serverPort = Integer.parseInt(loginSrvServerPortTextField.getText().trim());
    if (!Networks.isPort(serverPort)) {
      Dialogs.warn("通讯端口设置错误！！").show();
      loginSrvServerPortTextField.requestFocus();
      return;
    }
    int monPort = Integer.parseInt(loginSrvMonPortTextField.getText().trim());
    if (!Networks.isPort(monPort)) {
      Dialogs.warn("监控端口设置错误！！").show();
      loginSrvMonPortTextField.requestFocus();
      return;
    }

    share.config.loginSrv.gatePort = gatePort;
    share.config.loginSrv.serverPort = serverPort;
    share.config.loginSrv.monPort = monPort;

    configTabPane.getSelectionModel().selectNext();
  }

  public void onDefaultLoginSrvConfigClicked(ActionEvent actionEvent) {

  }

  public void onPreviousDbServerConfigClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  public void onNextDbServerConfigClicked() {
    int gatePort = Integer.parseInt(dbServerGatePortTextField.getText().trim());
    if (!Networks.isPort(gatePort)) {
      Dialogs.warn("网关端口设置错误！！").show();
      dbServerGatePortTextField.requestFocus();
      return;
    }
    int serverPort = Integer.parseInt(dbServerServerPortTextField.getText().trim());
    if (!Networks.isPort(serverPort)) {
      Dialogs.warn("通讯端口设置错误！！").show();
      dbServerServerPortTextField.requestFocus();
      return;
    }

    share.config.dbServer.gatePort = gatePort;
    share.config.dbServer.serverPort = serverPort;

    configTabPane.getSelectionModel().selectNext();
  }

  public void onDefaultDbServerConfigClicked(ActionEvent actionEvent) {

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
    share.config.logServer.port = port;

    configTabPane.getSelectionModel().selectNext();
  }

  public void onDefaultLogServerConfigClicked(ActionEvent actionEvent) {
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
    share.config.m2Server.gatePort = gatePort;
    share.config.m2Server.msgSrvPort = serverPort;

    configTabPane.getSelectionModel().selectNext();
  }

  public void onDefaultM2ServerConfigClicked(ActionEvent actionEvent) {
  }

  public void onPreviousPlugTopConfigClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  public void onNextPlugTopConfigClicked() {
    configTabPane.getSelectionModel().selectNext();
  }

  public void onDefaultPlugTopConfigClicked(ActionEvent actionEvent) {
  }

  public void onPreviousSaveConfigClicked() {
    configTabPane.getSelectionModel().selectPrevious();
  }

  public void onSaveConfigClicked() {
    share.saveConfig();
    Dialogs.info("配置文件已经保存完毕...")
        .showAndWait()
        .filter(ButtonType.OK::equals)
        .flatMap(buttonType -> Dialogs.confirm("更新所有配置", "是否生成新的游戏服务器配置文件？"))
        .ifPresent(buttonType -> {
          onGenerateConfigClicked();
          configTabPane.getSelectionModel().selectFirst();
          mainTabPane.getSelectionModel().selectFirst();
        });
  }

  public void onGenerateConfigClicked() {
    generateGameConfig();
    refGameConsole();
    Dialogs.info("引擎配置文件已经生成完毕...").show();
  }

  private void generateGameConfig() {
    Files.mkdir(new File(share.gameDirectory));
    generateDBServerConfig();
    generateLoginServerConfig();
    generateM2ServerConfig();
    generateLogServerConfig();
    generateRunGateConfig();
    generateSelGateConfig();
    generateLoginGateConfig();
  }

  private void generateLoginGateConfig() {
    File loginGateDir = new File(share.gameDirectory, "LoginGate\\");
    Files.mkdir(loginGateDir);

    try {
      File configFile = new File(loginGateDir, SERVER_CONFIG_FILE);
      Files.create(configFile);
      Ini ini = new Wini(configFile);
      ini.put(LOGIN_GATE_SECTION_NAME_2, "Title", share.gameName);
      ini.put(LOGIN_GATE_SECTION_NAME_2, "ServerAddr", PRIMARY_IP_ADDRESS);
      ini.put(LOGIN_GATE_SECTION_NAME_2, "ServerPort", share.config.loginSrv.gatePort);
      ini.put(LOGIN_GATE_SECTION_NAME_2, "GateAddr", ALL_IP_ADDRESS);
      ini.put(LOGIN_GATE_SECTION_NAME_2, "GatePort", share.config.loginGate.gatePort);
      ini.store();
    } catch (IOException e) {
      Dialogs.error("生成登陆网关配置文件出错！！", e).show();
    }
  }

  private void generateSelGateConfig() {
    File selGateDir = new File(share.gameDirectory, "SelGate\\");
    Files.mkdir(selGateDir);

    try {
      File configFile = new File(selGateDir, SERVER_CONFIG_FILE);
      Files.create(configFile);
      Ini ini = new Wini(configFile);
      ini.put(SEL_GATE_SECTION_NAME_2, "Title", share.gameName);
      ini.put(SEL_GATE_SECTION_NAME_2, "ServerAddr", PRIMARY_IP_ADDRESS);
      ini.put(SEL_GATE_SECTION_NAME_2, "ServerPort", share.config.dbServer.gatePort);
      ini.put(SEL_GATE_SECTION_NAME_2, "GateAddr", ALL_IP_ADDRESS);
      ini.put(SEL_GATE_SECTION_NAME_2, "GatePort", share.config.selGate.gatePort[0]);
      ini.store();
    } catch (IOException e) {
      Dialogs.error("生成角色网关配置文件出错！！", e).show();
    }
  }

  private void generateRunGateConfig() {
    File runGateDir = new File(share.gameDirectory, "RunGate\\");
    Files.mkdir(runGateDir);

    try {
      File configFile = new File(runGateDir, SERVER_CONFIG_FILE);
      Files.create(configFile);
      Ini ini = new Wini(configFile);
      ini.put(RUN_GATE_SECTION_NAME_2, "Title", share.gameName);
      ini.put(RUN_GATE_SECTION_NAME_2, "ServerAddr", PRIMARY_IP_ADDRESS);
      ini.put(RUN_GATE_SECTION_NAME_2, "ServerPort", share.config.m2Server.gatePort);
      ini.put(RUN_GATE_SECTION_NAME_2, "GateAddr", ALL_IP_ADDRESS);
      ini.put(RUN_GATE_SECTION_NAME_2, "GatePort", share.config.runGate.gatePort[0]);
      ini.put(RUN_GATE_SECTION_NAME_2, "CenterAddr", PRIMARY_IP_ADDRESS);
      ini.put(RUN_GATE_SECTION_NAME_2, "CenterPort", share.config.loginSrv.serverPort);
      ini.store();
    } catch (IOException e) {
      Dialogs.error("生成游戏网关配置文件出错！！", e).show();
    }
  }

  private void generateLogServerConfig() {
    File logSrvDir = new File(share.gameDirectory, "LogServer\\");
    Files.mkdir(logSrvDir);

    try {
      File logSrvFile = new File(logSrvDir, "LogData.ini");
      Files.create(logSrvFile);
      Ini ini = new Wini(logSrvFile);
      ini.put(LOG_SERVER_SECTION_2, "ServerName", share.gameName);
      ini.put(LOG_SERVER_SECTION_2, "Port", share.config.logServer.port);
      ini.put(LOG_SERVER_SECTION_2, "BaseDir", ".\\BaseDir\\");
      ini.store();
    } catch (IOException e) {
      Dialogs.error("生成日志服务器配置文件出错！！", e).show();
    }

    Files.mkdir(new File(logSrvDir, "BaseDir\\"));
  }

  private void generateM2ServerConfig() {
    File m2SrvDir = new File(share.gameDirectory, "Mir200\\");
    Files.mkdir(m2SrvDir);

    try {
      File m2SrvFile = new File(m2SrvDir, M2_SERVER_CONFIG_FILE);
      Files.create(m2SrvFile);
      Ini ini = new Wini(m2SrvFile);
      ini.put(M2_SERVER_SECTION_NAME_1, "ServerName", share.gameName);
      ini.put(M2_SERVER_SECTION_NAME_1, "DBName", share.heroDBName);
      ini.put(M2_SERVER_SECTION_NAME_1, "GateAddr", ALL_IP_ADDRESS);
      ini.put(M2_SERVER_SECTION_NAME_1, "GatePort", share.config.m2Server.gatePort);
      ini.put(M2_SERVER_SECTION_NAME_1, "DBAddr", PRIMARY_IP_ADDRESS);
      ini.put(M2_SERVER_SECTION_NAME_1, "DBPort", share.config.dbServer.serverPort);
      ini.put(M2_SERVER_SECTION_NAME_1, "IDSAddr", PRIMARY_IP_ADDRESS);
      ini.put(M2_SERVER_SECTION_NAME_1, "IDSPort", share.config.loginSrv.serverPort);
      ini.put(M2_SERVER_SECTION_NAME_1, "MsgSrvAddr", ALL_IP_ADDRESS);
      ini.put(M2_SERVER_SECTION_NAME_1, "MsgSrvPort", share.config.m2Server.msgSrvPort);
      ini.put(M2_SERVER_SECTION_NAME_1, "LogServerAddr", PRIMARY_IP_ADDRESS);
      ini.put(M2_SERVER_SECTION_NAME_1, "LogServerPort", share.config.logServer.port);
      ini.put(M2_SERVER_SECTION_NAME_1, "CloseWuXin", share.closeWuXinEnabled);

      ini.put(M2_SERVER_SECTION_NAME_2, "GuildDir", ".\\GuildBase\\Guilds\\");
      ini.put(M2_SERVER_SECTION_NAME_2, "GuildFile", ".\\GuildBase\\GuildList.txt");
      ini.put(M2_SERVER_SECTION_NAME_2, "ConLogDir", ".\\ConLog\\");
      ini.put(M2_SERVER_SECTION_NAME_2, "CastleDir", ".\\Castle\\");
      ini.put(M2_SERVER_SECTION_NAME_2, "CastleFile", ".\\Castle\\List.txt");
      ini.put(M2_SERVER_SECTION_NAME_2, "GameDataDir", ".\\Envir\\");
      ini.put(M2_SERVER_SECTION_NAME_2, "EnvirDir", ".\\Envir\\");
      ini.put(M2_SERVER_SECTION_NAME_2, "MapDir", ".\\Map\\");
      ini.put(M2_SERVER_SECTION_NAME_2, "NoticeDir", ".\\Notice\\");
      ini.put(M2_SERVER_SECTION_NAME_2, "LogDir", ".\\Log\\");
      ini.put(M2_SERVER_SECTION_NAME_2, "EMailDir", ".\\EMail\\");
      ini.store();
    } catch (IOException e) {
      Dialogs.error("生成服务端核心配置出错！！", e).show();
    }

    // todo 批量创建文件
    Files.mkdir(new File(m2SrvDir, "GuildBase\\"));
    Files.mkdir(new File(m2SrvDir, "GuildBase\\Guilds\\"));
    Files.mkdir(new File(m2SrvDir, "ConLog\\"));
    Files.mkdir(new File(m2SrvDir, "Castle\\"));
    Files.mkdir(new File(m2SrvDir, "Envir\\"));
    Files.mkdir(new File(m2SrvDir, "Map\\"));
    Files.mkdir(new File(m2SrvDir, "Notice\\"));
    Files.mkdir(new File(m2SrvDir, "Log\\"));
    Files.mkdir(new File(m2SrvDir, "EMail\\"));

    Files.onceWrite(new File(m2SrvDir, "!servertable.txt"), PRIMARY_IP_ADDRESS);
  }

  private void generateLoginServerConfig() {
    File loginSrvDir = new File(share.gameDirectory, "LoginSrv\\");
    Files.mkdir(loginSrvDir);

    try {
      File loginSrvFile = new File(loginSrvDir, "Logsrv.ini");
      Files.create(loginSrvFile);
      Ini ini = new Wini(loginSrvFile);
      ini.put(LOGIN_SRV_SECTION_NAME_2, "ServerAddr", ALL_IP_ADDRESS);
      ini.put(LOGIN_SRV_SECTION_NAME_2, "ServerPort", share.config.loginSrv.serverPort);
      ini.put(LOGIN_SRV_SECTION_NAME_2, "GateAddr", ALL_IP_ADDRESS);
      ini.put(LOGIN_SRV_SECTION_NAME_2, "GatePort", share.config.loginSrv.gatePort);
      ini.put(LOGIN_SRV_SECTION_NAME_2, "MonAddr", ALL_IP_ADDRESS);
      ini.put(LOGIN_SRV_SECTION_NAME_2, "MonPort", share.config.loginSrv.monPort);
      ini.put(LOGIN_SRV_SECTION_NAME_2, "CloseWuXin", share.closeWuXinEnabled);
      ini.put(LOGIN_SRV_SECTION_NAME_2, "IDDir", ".\\DB\\");
      ini.put(LOGIN_SRV_SECTION_NAME_2, "CountLogDir", ".\\ChrLog\\");
      ini.store();
    } catch (IOException e) {
      Dialogs.error("生成登陆服务器配置文件出错", e).show();
    }

    StringBuilder builder = new StringBuilder(PRIMARY_IP_ADDRESS);
    if (share.ip2Enabled) {
      builder.append(System.lineSeparator()).append(SECOND_IP_ADDRESS);
    }
    Files.onceWrite(new File(loginSrvDir, "!serveraddr.txt"), builder.toString());

    String content = String.format("%s %s %d", share.gameName, share.gameName, ONLINE_USER_LIMIT);
    Files.onceWrite(new File(loginSrvDir, "!UserLimit.txt"), content);

    builder = new StringBuilder(PRIMARY_IP_ADDRESS);
    if (share.config.selGate.getStart1) {
      builder.append(String.format(" %s %d", share.extIPAddr, share.config.selGate.gatePort[0]));
    }
    builder.append(System.lineSeparator());
    if (share.ip2Enabled) {
      builder.append(SECOND_IP_ADDRESS);
      if (share.config.selGate.getStart2) {
        builder.append(String.format(" %s %d", share.extIPAddr2, share.config.selGate.gatePort[1]));
      }
    } else {
      if (share.config.selGate.getStart2) {
        builder.append(String.format(" %s %d", share.extIPAddr, share.config.selGate.gatePort[1]));
      }
    }
    Files.onceWrite(new File(loginSrvDir, "!addrtable.txt"), builder.toString());
    Files.mkdir(new File(loginSrvDir, "ChrLog\\"));
    Files.mkdir(new File(loginSrvDir, "DB\\"));
  }

  private void generateDBServerConfig() {
    File dbServerDir = new File(share.gameDirectory, "DBServer\\");
    Files.mkdir(dbServerDir);
    File dbFileDir = new File(dbServerDir, "DB\\");
    Files.mkdir(dbFileDir);

    try {
      File dbSrcFile = new File(dbServerDir, "Dbsrc.ini");
      Files.create(dbSrcFile);
      Ini ini = new Wini(dbSrcFile);
      ini.put(DB_SERVER_SECTION_NAME_2, "ServerName", share.gameName);
      ini.put(DB_SERVER_SECTION_NAME_2, "ServerAddr", PRIMARY_IP_ADDRESS);
      ini.put(DB_SERVER_SECTION_NAME_2, "ServerPort", share.config.dbServer.serverPort);
      ini.put(DB_SERVER_SECTION_NAME_2, "GateAddr", ALL_IP_ADDRESS);
      ini.put(DB_SERVER_SECTION_NAME_2, "GatePort", share.config.dbServer.gatePort);
      ini.put(DB_SERVER_SECTION_NAME_2, "IDSAddr", PRIMARY_IP_ADDRESS);
      ini.put(DB_SERVER_SECTION_NAME_2, "IDSPort", share.config.loginSrv.serverPort);
      ini.put(DB_SERVER_SECTION_NAME_2, "DBName", share.heroDBName);
      ini.put(DB_SERVER_SECTION_NAME_2, "DBDir", ".\\DB\\");
      ini.store();
    } catch (IOException e) {
      Dialogs.error("生成数据库服务器配置文件出错！！", e).show();
    }

    StringBuilder builder = new StringBuilder(PRIMARY_IP_ADDRESS);
    if (share.ip2Enabled) {
      builder.append(System.lineSeparator()).append(SECOND_IP_ADDRESS);
    }
    Files.onceWrite(new File(dbServerDir, "!addrtable.txt"), builder.toString());

    builder = new StringBuilder(PRIMARY_IP_ADDRESS);
    for (int i = 0; i < share.config.runGate.getStart.length; i++) {
      if (share.config.runGate.getStart[i]) {
        builder.append(
            String.format(" %s %d", share.extIPAddr, share.config.runGate.gatePort[i]));
      }
    }
    builder.append(System.lineSeparator());
    if (share.ip2Enabled) {
      builder.append(SECOND_IP_ADDRESS);
      for (int i = 0; i < share.config.runGate.getStart.length; i++) {
        if (share.config.runGate.getStart[i]) {
          builder.append(
              String.format(" %s %d", share.extIPAddr2, share.config.runGate.gatePort[i]));
        }
      }
    }
    Files.onceWrite(new File(dbServerDir, "!serverinfo.txt"), builder.toString());
    Files.onceWrite(new File(dbServerDir, "FUserName.txt"), ";创建人物过滤字符，一行一个过滤");
  }

  public enum StartMode {
    NORMAL("正常启动"),
    DELAY("延时启动"),
    TIMING("定时启动"),
    ;

    public final String name;

    StartMode(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  public class StartGameTask extends TimerTask {
    @Override
    public void run() {
      if (share.dbServer.getStart) {
        switch (share.dbServer.startStatus) {
          case 0:
            mainOutMessage("正在启动数据库服务器..");
            share.dbServer.start();
            share.dbServer.startStatus = 1;
            return;
          case 1:
            LOGGER.info("waiting database server program.");
            return;
        }
      }
      if (share.loginServer.getStart) {
        // todo login server
      }
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

  private void processDbServerMsg(String message) {
    String[] split = message.split(".", 2);
    int code = Integer.parseInt(split[0]);
    String data = split[1];
    switch (code) {
      case SG_START_NOW:
        break;
      case SG_START_OK:
        share.dbServer.startStatus = 2;
        break;
    }
    mainOutMessage(data);
  }
}
