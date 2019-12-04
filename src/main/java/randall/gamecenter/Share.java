package randall.gamecenter;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import org.ini4j.Ini;
import org.ini4j.Wini;
import randall.common.ui.Dialogs;
import randall.common.util.IOHelper;

/**
 * 共享逻辑。
 *
 * @author mrzhqiang
 */
public final class Share {
  public static final int MAX_RUN_GATE_COUNT = 8;

  public static final int DB_SERVER_PROCESS_CODE = 1001;
  public static final int LOGIN_SERVER_PROCESS_CODE = 1002;
  public static final int LOG_SERVER_PROCESS_CODE = 1003;
  public static final int M2_SERVER_PROCESS_CODE = 1004;
  public static final int LOGIN_GATE_PROCESS_CODE = 1005;
  public static final int SEL_GATE_PROCESS_CODE = 1006;
  public static final int RUN_GATE_PROCESS_CODE = 1007;
  public static final int PLUG_TOP_PROCESS_CODE = 1008;

  public static final int STOPPED_STATE = 0;
  public static final int STARTING_STATE = 1;
  public static final int RUNNING_STATE = 2;
  public static final int STOPPING_STATE = 3;
  public static final int ERROR_STATE = 9;

  public static final String QUIT_CODE = ":QUIT";

  public static final String BASIC_SECTION_NAME = "GameConfig";
  public static final String DB_SERVER_SECTION_NAME = "DBServer";
  public static final String LOGIN_SRV_SECTION_NAME = "LoginSrv";
  public static final String M2_SERVER_SECTION_NAME = "M2Server";
  public static final String LOG_SERVER_SECTION_NAME = "LogServer";
  public static final String RUN_GATE_SECTION_NAME = "RunGate";
  public static final String SEL_GATE_SECTION_NAME = "SelGate";
  public static final String LOGIN_GATE_SECTION_NAME = "LoginGate";
  public static final String PLUG_TOP_SECTION_NAME = "PlugTop";

  public static final String ALL_IP_ADDRESS = "0.0.0.0";
  public static final String PRIMARY_IP_ADDRESS = "127.0.0.1";
  public static final String SECOND_IP_ADDRESS = "127.0.0.2";
  public static final int ONLINE_USER_LIMIT = 2000;

  public static final String SERVER_CONFIG_FILE = "Config.ini";

  public static final String DB_SERVER_SECTION_NAME_2 = "DBServer";
  public static final String LOGIN_SRV_SECTION_NAME_2 = "LoginSrv";

  public static final String M2_SERVER_CONFIG_FILE = "!Setup.txt";
  public static final String M2_SERVER_SECTION_NAME_1 = "Server";
  public static final String M2_SERVER_SECTION_NAME_2 = "Share";

  public static final String LOG_SERVER_SECTION_2 = "LogDataServer";

  public static final String RUN_GATE_SECTION_NAME_2 = "RunGate";
  public static final String SEL_GATE_SECTION_NAME_2 = "SelGate";
  public static final String LOGIN_GATE_SECTION_NAME_2 = "LoginGate";

  public static final String DEFAULT_GAME_DIRECTORY = "D:\\randall-m2\\MirServer\\";
  public static final String DEFAULT_DB_NAME = "HeroDB";
  public static final String DEFAULT_GAME_NAME = "兰达尔第一季";
  public static final Boolean DEFAULT_AUTO_RUN_BACKUP = false;
  public static final boolean DEFAULT_IP_2_ENABLED = false;
  public static final boolean DEFAULT_CLOSE_WUXING_ENABLED = false;

  public Ini ini;

  public String textStartGame = "启动游戏服务器";
  public String textStopGame = "停止游戏服务器";
  public String textCancelStartGame = "取消启动服务器";
  public String textCancelStopGame = "取消停止服务器";
  public String configFile = "Config.ini";
  public String backupListFile = "BackupList.txt";

  public String gameName = DEFAULT_GAME_NAME;
  public String gameDirectory = DEFAULT_GAME_DIRECTORY;
  public String heroDBName = DEFAULT_DB_NAME;
  public String extIPAddr = PRIMARY_IP_ADDRESS;
  public String extIPAddr2 = SECOND_IP_ADDRESS;
  public boolean autoRunBakEnabled = DEFAULT_AUTO_RUN_BACKUP;
  public boolean closeWuXinEnabled = DEFAULT_CLOSE_WUXING_ENABLED;
  public boolean ip2Enabled = DEFAULT_IP_2_ENABLED;

  public long stopTick;
  public long stopTimeout = 10000;

  public final Config config = new Config();

  public final Program dbServer = new Program();
  public final Program loginServer = new Program();
  public final Program logServer = new Program();
  public final Program m2Server = new Program();
  public final List<Program> runGate = Lists.newArrayListWithCapacity(MAX_RUN_GATE_COUNT);
  public final Program selGate = new Program();
  public final Program selGate1 = new Program();
  public final Program loginGate = new Program();
  public final Program loginGate2 = new Program();
  public final Program plugTop = new Program();

  public BackupManager backupManager = new BackupManager();
  public int backupStartStatus = 0;

  public Share() {
    try {
      Path path = Paths.get(gameDirectory, this.configFile);
      if (Files.notExists(path)) {
        IOHelper.mkdir(path.getParent());
        IOHelper.create(path);
      }
      ini = new Wini(path.toFile());
    } catch (IOException e) {
      Dialogs.error("初始化配置文件出错！！", e).show();
    }
  }

  public void loadConfig() {
    // todo refactor as basic config and db server config etc.
    if (ini.get(BASIC_SECTION_NAME) != null) {
      gameDirectory = ini.get(BASIC_SECTION_NAME).get("GameDirectory", DEFAULT_GAME_DIRECTORY);
      heroDBName = ini.get(BASIC_SECTION_NAME).get("HeroDBName", DEFAULT_DB_NAME);
      gameName = ini.get(BASIC_SECTION_NAME).get("GameName", DEFAULT_GAME_NAME);
      extIPAddr = ini.get(BASIC_SECTION_NAME).get("ExtIPaddr", DEFAULT_GAME_DIRECTORY);
      extIPAddr2 = ini.get(BASIC_SECTION_NAME).get("ExtIPaddr2", PRIMARY_IP_ADDRESS);
      autoRunBakEnabled =
          ini.get(BASIC_SECTION_NAME).get("AutoRunBak", Boolean.class, DEFAULT_AUTO_RUN_BACKUP);
      ip2Enabled = ini.get(BASIC_SECTION_NAME).get("IP2", Boolean.class, DEFAULT_IP_2_ENABLED);
      closeWuXinEnabled = ini.get(BASIC_SECTION_NAME)
          .get("CloseWuXin", Boolean.class, DEFAULT_CLOSE_WUXING_ENABLED);
    }
    if (ini.get(DB_SERVER_SECTION_NAME) != null) {
      config.dbServer.mainFormX = ini.get(DB_SERVER_SECTION_NAME)
          .get("MainFormX", Integer.class, config.dbServer.mainFormX);
      config.dbServer.mainFormY = ini.get(DB_SERVER_SECTION_NAME)
          .get("MainFormY", Integer.class, config.dbServer.mainFormY);
      config.dbServer.gatePort =
          ini.get(DB_SERVER_SECTION_NAME).get("GatePort", Integer.class, config.dbServer.gatePort);
      config.dbServer.serverPort = ini.get(DB_SERVER_SECTION_NAME)
          .get("ServerPort", Integer.class, config.dbServer.serverPort);
      config.dbServer.getStart =
          ini.get(DB_SERVER_SECTION_NAME).get("GetStart", Boolean.class, config.dbServer.getStart);
    }
    if (ini.get(LOGIN_SRV_SECTION_NAME) != null) {
      config.loginSrv.mainFormX = ini.get(LOGIN_SRV_SECTION_NAME)
          .get("MainFormX", Integer.class, config.loginSrv.mainFormX);
      config.loginSrv.mainFormY = ini.get(LOGIN_SRV_SECTION_NAME)
          .get("MainFormY", Integer.class, config.loginSrv.mainFormY);
      config.loginSrv.gatePort =
          ini.get(LOGIN_SRV_SECTION_NAME).get("GatePort", Integer.class, config.loginSrv.gatePort);
      config.loginSrv.serverPort = ini.get(LOGIN_SRV_SECTION_NAME)
          .get("ServerPort", Integer.class, config.loginSrv.serverPort);
      config.loginSrv.monPort =
          ini.get(LOGIN_SRV_SECTION_NAME).get("MonPort", Integer.class, config.loginSrv.monPort);
      config.loginSrv.getStart =
          ini.get(LOGIN_SRV_SECTION_NAME).get("GetStart", Boolean.class, config.loginSrv.getStart);
    }
    if (ini.get(M2_SERVER_SECTION_NAME) != null) {
      config.m2Server.mainFormX = ini.get(M2_SERVER_SECTION_NAME)
          .get("MainFormX", Integer.class, config.m2Server.mainFormX);
      config.m2Server.mainFormY = ini.get(M2_SERVER_SECTION_NAME)
          .get("MainFormY", Integer.class, config.m2Server.mainFormY);
      config.m2Server.gatePort =
          ini.get(M2_SERVER_SECTION_NAME).get("GatePort", Integer.class, config.m2Server.gatePort);
      config.m2Server.msgSrvPort = ini.get(M2_SERVER_SECTION_NAME)
          .get("MsgSrvPort", Integer.class, config.m2Server.msgSrvPort);
      config.m2Server.getStart =
          ini.get(M2_SERVER_SECTION_NAME).get("GetStart", Boolean.class, config.m2Server.getStart);
    }
    if (ini.get(LOG_SERVER_SECTION_NAME) != null) {
      config.logServer.mainFormX = ini.get(LOG_SERVER_SECTION_NAME)
          .get("MainFormX", Integer.class, config.logServer.mainFormX);
      config.logServer.mainFormY = ini.get(LOG_SERVER_SECTION_NAME)
          .get("MainFormY", Integer.class, config.logServer.mainFormY);
      config.logServer.port =
          ini.get(LOG_SERVER_SECTION_NAME).get("Port", Integer.class, config.logServer.port);
      config.logServer.getStart = ini.get(LOG_SERVER_SECTION_NAME)
          .get("GetStart", Boolean.class, config.logServer.getStart);
    }
    if (ini.get(RUN_GATE_SECTION_NAME) != null) {
      config.runGate.mainFormX =
          ini.get(RUN_GATE_SECTION_NAME).get("MainFormX", Integer.class, config.runGate.mainFormX);
      config.runGate.mainFormY =
          ini.get(RUN_GATE_SECTION_NAME).get("MainFormY", Integer.class, config.runGate.mainFormY);
      // get start
      config.runGate.getStart[0] = ini.get(RUN_GATE_SECTION_NAME)
          .get("GetStart1", Boolean.class, config.runGate.getStart[0]);
      config.runGate.getStart[1] = ini.get(RUN_GATE_SECTION_NAME)
          .get("GetStart2", Boolean.class, config.runGate.getStart[1]);
      config.runGate.getStart[2] = ini.get(RUN_GATE_SECTION_NAME)
          .get("GetStart3", Boolean.class, config.runGate.getStart[2]);
      config.runGate.getStart[3] = ini.get(RUN_GATE_SECTION_NAME)
          .get("GetStart4", Boolean.class, config.runGate.getStart[3]);
      config.runGate.getStart[4] = ini.get(RUN_GATE_SECTION_NAME)
          .get("GetStart5", Boolean.class, config.runGate.getStart[4]);
      config.runGate.getStart[5] = ini.get(RUN_GATE_SECTION_NAME)
          .get("GetStart6", Boolean.class, config.runGate.getStart[5]);
      config.runGate.getStart[6] = ini.get(RUN_GATE_SECTION_NAME)
          .get("GetStart7", Boolean.class, config.runGate.getStart[6]);
      config.runGate.getStart[7] = ini.get(RUN_GATE_SECTION_NAME)
          .get("GetStart8", Boolean.class, config.runGate.getStart[7]);
      // gate port
      config.runGate.gatePort[0] = ini.get(RUN_GATE_SECTION_NAME)
          .get("GatePort1", Integer.class, config.runGate.gatePort[0]);
      config.runGate.gatePort[1] = ini.get(RUN_GATE_SECTION_NAME)
          .get("GatePort2", Integer.class, config.runGate.gatePort[1]);
      config.runGate.gatePort[2] = ini.get(RUN_GATE_SECTION_NAME)
          .get("GatePort3", Integer.class, config.runGate.gatePort[2]);
      config.runGate.gatePort[3] = ini.get(RUN_GATE_SECTION_NAME)
          .get("GatePort4", Integer.class, config.runGate.gatePort[3]);
      config.runGate.gatePort[4] = ini.get(RUN_GATE_SECTION_NAME)
          .get("GatePort5", Integer.class, config.runGate.gatePort[4]);
      config.runGate.gatePort[5] = ini.get(RUN_GATE_SECTION_NAME)
          .get("GatePort6", Integer.class, config.runGate.gatePort[5]);
      config.runGate.gatePort[6] = ini.get(RUN_GATE_SECTION_NAME)
          .get("GatePort7", Integer.class, config.runGate.gatePort[6]);
      config.runGate.gatePort[7] = ini.get(RUN_GATE_SECTION_NAME)
          .get("GatePort8", Integer.class, config.runGate.gatePort[7]);
    }
    if (ini.get(SEL_GATE_SECTION_NAME) != null) {
      config.selGate.mainFormX =
          ini.get(SEL_GATE_SECTION_NAME).get("MainFormX", Integer.class, config.selGate.mainFormX);
      config.selGate.mainFormY =
          ini.get(SEL_GATE_SECTION_NAME).get("MainFormY", Integer.class, config.selGate.mainFormY);
      config.selGate.gatePort[0] = ini.get(SEL_GATE_SECTION_NAME)
          .get("GatePort1", Integer.class, config.selGate.gatePort[0]);
      config.selGate.gatePort[1] = ini.get(SEL_GATE_SECTION_NAME)
          .get("GatePort2", Integer.class, config.selGate.gatePort[1]);
      config.selGate.getStart1 =
          ini.get(SEL_GATE_SECTION_NAME).get("GetStart1", Boolean.class, config.selGate.getStart1);
      config.selGate.getStart2 =
          ini.get(SEL_GATE_SECTION_NAME).get("GetStart2", Boolean.class, config.selGate.getStart2);
    }
    if (ini.get(LOGIN_GATE_SECTION_NAME) != null) {
      config.loginGate.mainFormX = ini.get(LOGIN_GATE_SECTION_NAME)
          .get("MainFormX", Integer.class, config.loginGate.mainFormX);
      config.loginGate.mainFormY = ini.get(LOGIN_GATE_SECTION_NAME)
          .get("MainFormY", Integer.class, config.loginGate.mainFormY);
      config.loginGate.gatePort = ini.get(LOGIN_GATE_SECTION_NAME)
          .get("GatePort", Integer.class, config.loginGate.gatePort);
      config.loginGate.getStart = ini.get(LOGIN_GATE_SECTION_NAME)
          .get("GetStart", Boolean.class, config.loginGate.getStart);
    }
    if (ini.get(PLUG_TOP_SECTION_NAME) != null) {
      config.plugTop.mainFormX =
          ini.get(PLUG_TOP_SECTION_NAME).get("MainFormX", Integer.class, config.plugTop.mainFormX);
      config.plugTop.mainFormY =
          ini.get(PLUG_TOP_SECTION_NAME).get("MainFormY", Integer.class, config.plugTop.mainFormY);
      config.plugTop.getStart =
          ini.get(PLUG_TOP_SECTION_NAME).get("GetStart", Boolean.class, config.plugTop.getStart);
    }
  }

  public void saveConfig() {
    // ini put 方法可以创建不存在的 section，因此无需判断 section 是否存在
    ini.put(BASIC_SECTION_NAME, "GameDirectory", gameDirectory);
    ini.put(BASIC_SECTION_NAME, "HeroDBName", heroDBName);
    ini.put(BASIC_SECTION_NAME, "GameName", gameName);
    ini.put(BASIC_SECTION_NAME, "ExtIPaddr", extIPAddr);
    ini.put(BASIC_SECTION_NAME, "ExtIPaddr2", extIPAddr2);
    ini.put(BASIC_SECTION_NAME, "AutoRunBak", autoRunBakEnabled);
    ini.put(BASIC_SECTION_NAME, "IP2", ip2Enabled);
    ini.put(BASIC_SECTION_NAME, "CloseWuXin", closeWuXinEnabled);

    ini.put(DB_SERVER_SECTION_NAME, "MainFormX", config.dbServer.mainFormX);
    ini.put(DB_SERVER_SECTION_NAME, "MainFormY", config.dbServer.mainFormY);
    ini.put(DB_SERVER_SECTION_NAME, "GatePort", config.dbServer.gatePort);
    ini.put(DB_SERVER_SECTION_NAME, "ServerPort", config.dbServer.serverPort);
    ini.put(DB_SERVER_SECTION_NAME, "GetStart", config.dbServer.getStart);

    ini.put(LOGIN_SRV_SECTION_NAME, "MainFormX", config.loginSrv.mainFormX);
    ini.put(LOGIN_SRV_SECTION_NAME, "MainFormY", config.loginSrv.mainFormY);
    ini.put(LOGIN_SRV_SECTION_NAME, "GatePort", config.loginSrv.gatePort);
    ini.put(LOGIN_SRV_SECTION_NAME, "ServerPort", config.loginSrv.serverPort);
    ini.put(LOGIN_SRV_SECTION_NAME, "MonPort", config.loginSrv.monPort);
    ini.put(LOGIN_SRV_SECTION_NAME, "GetStart", config.loginSrv.getStart);

    ini.put(M2_SERVER_SECTION_NAME, "MainFormX", config.m2Server.mainFormX);
    ini.put(M2_SERVER_SECTION_NAME, "MainFormY", config.m2Server.mainFormY);
    ini.put(M2_SERVER_SECTION_NAME, "GatePort", config.m2Server.gatePort);
    ini.put(M2_SERVER_SECTION_NAME, "MsgSrvPort", config.m2Server.msgSrvPort);
    ini.put(M2_SERVER_SECTION_NAME, "GetStart", config.m2Server.getStart);

    ini.put(LOG_SERVER_SECTION_NAME, "MainFormX", config.logServer.mainFormX);
    ini.put(LOG_SERVER_SECTION_NAME, "MainFormY", config.logServer.mainFormY);
    ini.put(LOG_SERVER_SECTION_NAME, "Port", config.logServer.port);
    ini.put(LOG_SERVER_SECTION_NAME, "GetStart", config.logServer.getStart);

    ini.put(RUN_GATE_SECTION_NAME, "MainFormX", config.runGate.mainFormX);
    ini.put(RUN_GATE_SECTION_NAME, "MainFormY", config.runGate.mainFormY);

    ini.put(RUN_GATE_SECTION_NAME, "GetStart1", config.runGate.getStart[0]);
    ini.put(RUN_GATE_SECTION_NAME, "GetStart2", config.runGate.getStart[1]);
    ini.put(RUN_GATE_SECTION_NAME, "GetStart3", config.runGate.getStart[2]);
    ini.put(RUN_GATE_SECTION_NAME, "GetStart4", config.runGate.getStart[3]);
    ini.put(RUN_GATE_SECTION_NAME, "GetStart5", config.runGate.getStart[4]);
    ini.put(RUN_GATE_SECTION_NAME, "GetStart6", config.runGate.getStart[5]);
    ini.put(RUN_GATE_SECTION_NAME, "GetStart7", config.runGate.getStart[6]);
    ini.put(RUN_GATE_SECTION_NAME, "GetStart8", config.runGate.getStart[7]);

    ini.put(RUN_GATE_SECTION_NAME, "GatePort1", config.runGate.gatePort[0]);
    ini.put(RUN_GATE_SECTION_NAME, "GatePort2", config.runGate.gatePort[1]);
    ini.put(RUN_GATE_SECTION_NAME, "GatePort3", config.runGate.gatePort[2]);
    ini.put(RUN_GATE_SECTION_NAME, "GatePort4", config.runGate.gatePort[3]);
    ini.put(RUN_GATE_SECTION_NAME, "GatePort5", config.runGate.gatePort[4]);
    ini.put(RUN_GATE_SECTION_NAME, "GatePort6", config.runGate.gatePort[5]);
    ini.put(RUN_GATE_SECTION_NAME, "GatePort7", config.runGate.gatePort[6]);
    ini.put(RUN_GATE_SECTION_NAME, "GatePort8", config.runGate.gatePort[7]);

    ini.put(SEL_GATE_SECTION_NAME, "MainFormX", config.selGate.mainFormX);
    ini.put(SEL_GATE_SECTION_NAME, "MainFormY", config.selGate.mainFormY);
    ini.put(SEL_GATE_SECTION_NAME, "GatePort1", config.selGate.gatePort[0]);
    ini.put(SEL_GATE_SECTION_NAME, "GatePort2", config.selGate.gatePort[1]);
    ini.put(SEL_GATE_SECTION_NAME, "GetStart1", config.selGate.getStart1);
    ini.put(SEL_GATE_SECTION_NAME, "GetStart2", config.selGate.getStart2);

    ini.put(LOGIN_GATE_SECTION_NAME, "MainFormX", config.loginGate.mainFormX);
    ini.put(LOGIN_GATE_SECTION_NAME, "MainFormY", config.loginGate.mainFormY);
    ini.put(LOGIN_GATE_SECTION_NAME, "GatePort", config.loginGate.gatePort);
    ini.put(LOGIN_GATE_SECTION_NAME, "GetStart", config.loginGate.getStart);

    ini.put(PLUG_TOP_SECTION_NAME, "MainFormX", config.plugTop.mainFormX);
    ini.put(PLUG_TOP_SECTION_NAME, "MainFormY", config.plugTop.mainFormY);
    ini.put(PLUG_TOP_SECTION_NAME, "GetStart", config.plugTop.getStart);

    try {
      ini.store();
    } catch (IOException e) {
      Dialogs.error("保存配置文件时出错！", e).show();
    }
  }

  public static class Program {
    public boolean getStart;
    public boolean reStart;
    // 0 default; 1 starting; 2 started; 9 failed or error
    public byte startStatus;
    public Process process;
    public UUID processCode;
    public String programFile;
    public String directory;
    public Integer mainFormX;
    public Integer mainFormY;

    public Disposable disposable;

    public Observable<String> start() {
      if (Strings.isNullOrEmpty(directory)) {
        directory = ".\\";
      }
      if (!directory.endsWith("\\")) {
        directory = directory + "\\";
      }
      String command = String.format("%s%s %d %d", directory, programFile, mainFormX, mainFormY);
      try {
        process = Runtime.getRuntime().exec(command);
      } catch (IOException e) {
        throw new RuntimeException("启动程序[" + programFile + "]出错！！", e);
      }
      return Observable.create((ObservableEmitter<String> emitter) -> {
        InputStream inputStream = process.getInputStream();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
          String line;
          while ((line = reader.readLine()) != null) {
            emitter.onNext(line);
            if (!process.isAlive()) {
              return;
            }
          }
        } catch (IOException e) {
          emitter.tryOnError(e);
        }
        startStatus = 0;
        process = null;
        processCode = null;
        emitter.onComplete();
      }).subscribeOn(Schedulers.io());
    }

    public void stop() {
      if (disposable != null && !disposable.isDisposed()) {
        disposable.dispose();
        disposable = null;
      }
      processCode = null;
      if (process.isAlive()) {
        process.destroy();
        process = null;
      }
    }

    public void sendMessage(String message) {
      if (process != null && process.isAlive()) {
        OutputStream outputStream = process.getOutputStream();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
          writer.write(message);
          writer.flush();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static class DBServerConfig {
    public int mainFormX = 0;
    public int mainFormY = 373;
    public int gatePort = 5100;
    public int serverPort = 6000;
    public boolean getStart = true;
    public String programFile = "DBServer.exe";
  }

  public static class LoginSrvConfig {
    public int mainFormX = 252;
    public int mainFormY = 0;
    public int gatePort = 5500;
    public int serverPort = 5600;
    public int monPort = 3000;
    public boolean getStart = true;
    public String programFile = "LoginSrv.exe";
  }

  public static class M2ServerConfig {
    public int mainFormX = 561;
    public int mainFormY = 0;
    public int gatePort = 5000;
    public int msgSrvPort = 4900;
    public boolean getStart = true;
    public String programFile = "M2Server.exe";
  }

  public static class LogServerConfig {
    public int mainFormX = 252;
    public int mainFormY = 286;
    public int port = 10000;
    public boolean getStart = true;
    public String programFile = "LogDataServer.exe";
  }

  public static class RunGateConfig {
    public int mainFormX = 437;
    public int mainFormY = 373;
    public boolean[] getStart = {true, true, true, false, false, false, false, false};
    public int[] gatePort = {7200, 7201, 7202, 7203, 7204, 7205, 7206, 7207};
    public String programFile = "RunGate.exe";
  }

  public static class SelGateConfig {
    public int mainFormX = 0;
    public int mainFormY = 180;
    public int[] gatePort = {7100, 7101};
    public boolean getStart1 = true;
    public boolean getStart2 = false;
    public String programFile = "SelGate.exe";
  }

  public static class LoginGateConfig {
    public int mainFormX = 0;
    public int mainFormY = 0;
    public int gatePort = 7000;
    public boolean getStart = true;
    public String programFile = "LoginGate.exe";
  }

  public static class PlugTopConfig {
    public int mainFormX = 200;
    public int mainFormY = 0;
    public boolean getStart = true;
    public String programFile = "PlugTop.exe";
  }

  public static class Config {
    public DBServerConfig dbServer = new DBServerConfig();
    public LoginSrvConfig loginSrv = new LoginSrvConfig();
    public M2ServerConfig m2Server = new M2ServerConfig();
    public LogServerConfig logServer = new LogServerConfig();
    public RunGateConfig runGate = new RunGateConfig();
    public SelGateConfig selGate = new SelGateConfig();
    public LoginGateConfig loginGate = new LoginGateConfig();
    public PlugTopConfig plugTop = new PlugTopConfig();
  }
}
