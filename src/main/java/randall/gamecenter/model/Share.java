package randall.gamecenter.model;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 共享逻辑。
 *
 * @author mrzhqiang
 */
@Component
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

  public String textStartGame = "启动游戏服务器";
  public String textStopGame = "停止游戏服务器";
  public String textCancelStartGame = "取消启动服务器";
  public String textCancelStopGame = "取消停止服务器";
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

  //public final Config config = new Config();

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

  public final Profile profile;

  @Autowired
  public Share(Profile profile) {
    this.profile = profile;
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
}
