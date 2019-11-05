package main.java.randall.gamecenter;

import main.java.randall.gamecenter.dialog.AlertDialog;
import com.google.common.collect.Lists;
import org.ini4j.Ini;
import org.ini4j.Profile;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author mrzhqiang
 */
public final class Share {
    private static final Logger LOGGER = LoggerFactory.getLogger("gamecenter");

    public static final int MAX_RUN_GATE_COUNT = 8;

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

    public static final String DB_SERVER_SECTION_NAME_2 = "DBServer";

    private static final String DEFAULT_GAME_DIRECTORY = "D:\\mir-server\\MirServer\\";
    private static final String DEFAULT_DB_NAME = "HeroDB";
    private static final String DEFAULT_GAME_NAME = "兰达尔第一季";
    private static final Boolean DEFAULT_AUTO_RUN_BACKUP = false;
    private static final boolean DEFAULT_IP_2_ENABLED = false;
    private static final boolean DEFAULT_CLOSE_WUXING_ENABLED = false;

    private Ini ini;
    public String textStartGame = "启动游戏服务器";
    public String textStopGame = "停止游戏服务器";
    public String textCancelStartGame = "取消启动服务器";
    public String textCancelStopGame = "取消停止服务器";
    private String configFile = ".\\Config.ini";

    public String gameDirectory = DEFAULT_GAME_DIRECTORY;
    public String heroDBName = DEFAULT_DB_NAME;
    public String gameName = DEFAULT_GAME_NAME;
    public String extIPAddr = PRIMARY_IP_ADDRESS;
    public String extIPAddr2 = SECOND_IP_ADDRESS;
    public boolean autoRunBakEnabled = DEFAULT_AUTO_RUN_BACKUP;
    public boolean ip2Enabled = DEFAULT_IP_2_ENABLED;
    public boolean closeWuXinEnabled = DEFAULT_CLOSE_WUXING_ENABLED;

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

    public Share() {
        ini = new Wini();
        try {
            File file = new File(configFile);
            if (!file.exists() && file.createNewFile()) {
                LOGGER.info("创建新的配置文件：" + file.getName());
            }
            ini.load(file);
        } catch (IOException e) {
            AlertDialog.showError("初始化配置文件出错！！", e);
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
            autoRunBakEnabled = ini.get(BASIC_SECTION_NAME).get("AutoRunBak", Boolean.class, DEFAULT_AUTO_RUN_BACKUP);
            ip2Enabled = ini.get(BASIC_SECTION_NAME).get("IP2", Boolean.class, DEFAULT_IP_2_ENABLED);
            closeWuXinEnabled = ini.get(BASIC_SECTION_NAME).get("CloseWuXin", Boolean.class, DEFAULT_CLOSE_WUXING_ENABLED);
        }
        if (ini.get(DB_SERVER_SECTION_NAME) != null) {
            config.dbServer.mainFormX = ini.get(DB_SERVER_SECTION_NAME).get("MainFormX", Integer.class, config.dbServer.mainFormX);
            config.dbServer.mainFormY = ini.get(DB_SERVER_SECTION_NAME).get("MainFormY", Integer.class, config.dbServer.mainFormY);
            config.dbServer.gatePort = ini.get(DB_SERVER_SECTION_NAME).get("GatePort", Integer.class, config.dbServer.gatePort);
            config.dbServer.serverPort = ini.get(DB_SERVER_SECTION_NAME).get("ServerPort", Integer.class, config.dbServer.serverPort);
            config.dbServer.getStart = ini.get(DB_SERVER_SECTION_NAME).get("GetStart", Boolean.class, config.dbServer.getStart);
        }
        if (ini.get(LOGIN_SRV_SECTION_NAME) != null) {
            config.loginSrv.mainFormX = ini.get(LOGIN_SRV_SECTION_NAME).get("MainFormX", Integer.class, config.loginSrv.mainFormX);
            config.loginSrv.mainFormY = ini.get(LOGIN_SRV_SECTION_NAME).get("MainFormY", Integer.class, config.loginSrv.mainFormY);
            config.loginSrv.gatePort = ini.get(LOGIN_SRV_SECTION_NAME).get("GatePort", Integer.class, config.loginSrv.gatePort);
            config.loginSrv.serverPort = ini.get(LOGIN_SRV_SECTION_NAME).get("ServerPort", Integer.class, config.loginSrv.serverPort);
            config.loginSrv.monPort = ini.get(LOGIN_SRV_SECTION_NAME).get("MonPort", Integer.class, config.loginSrv.monPort);
            config.loginSrv.getStart = ini.get(LOGIN_SRV_SECTION_NAME).get("GetStart", Boolean.class, config.loginSrv.getStart);
        }
        if (ini.get(M2_SERVER_SECTION_NAME) != null) {
            config.m2Server.mainFormX = ini.get(M2_SERVER_SECTION_NAME).get("MainFormX", Integer.class, config.m2Server.mainFormX);
            config.m2Server.mainFormY = ini.get(M2_SERVER_SECTION_NAME).get("MainFormY", Integer.class, config.m2Server.mainFormY);
            config.m2Server.gatePort = ini.get(M2_SERVER_SECTION_NAME).get("GatePort", Integer.class, config.m2Server.gatePort);
            config.m2Server.msgSrvPort = ini.get(M2_SERVER_SECTION_NAME).get("MsgSrvPort", Integer.class, config.m2Server.msgSrvPort);
            config.m2Server.getStart = ini.get(M2_SERVER_SECTION_NAME).get("GetStart", Boolean.class, config.m2Server.getStart);
        }
        if (ini.get(LOG_SERVER_SECTION_NAME) != null) {
            config.logServer.mainFormX = ini.get(LOG_SERVER_SECTION_NAME).get("MainFormX", Integer.class, config.logServer.mainFormX);
            config.logServer.mainFormY = ini.get(LOG_SERVER_SECTION_NAME).get("MainFormY", Integer.class, config.logServer.mainFormY);
            config.logServer.port = ini.get(LOG_SERVER_SECTION_NAME).get("Port", Integer.class, config.logServer.port);
            config.logServer.getStart = ini.get(LOG_SERVER_SECTION_NAME).get("GetStart", Boolean.class, config.logServer.getStart);
        }
        if (ini.get(RUN_GATE_SECTION_NAME) != null) {
            config.runGate.mainFormX = ini.get(RUN_GATE_SECTION_NAME).get("MainFormX", Integer.class, config.runGate.mainFormX);
            config.runGate.mainFormY = ini.get(RUN_GATE_SECTION_NAME).get("MainFormY", Integer.class, config.runGate.mainFormY);
            // get start
            config.runGate.getStart[0] = ini.get(RUN_GATE_SECTION_NAME).get("GetStart1", Boolean.class, config.runGate.getStart[0]);
            config.runGate.getStart[1] = ini.get(RUN_GATE_SECTION_NAME).get("GetStart2", Boolean.class, config.runGate.getStart[1]);
            config.runGate.getStart[2] = ini.get(RUN_GATE_SECTION_NAME).get("GetStart3", Boolean.class, config.runGate.getStart[2]);
            config.runGate.getStart[3] = ini.get(RUN_GATE_SECTION_NAME).get("GetStart4", Boolean.class, config.runGate.getStart[3]);
            config.runGate.getStart[4] = ini.get(RUN_GATE_SECTION_NAME).get("GetStart5", Boolean.class, config.runGate.getStart[4]);
            config.runGate.getStart[5] = ini.get(RUN_GATE_SECTION_NAME).get("GetStart6", Boolean.class, config.runGate.getStart[5]);
            config.runGate.getStart[6] = ini.get(RUN_GATE_SECTION_NAME).get("GetStart7", Boolean.class, config.runGate.getStart[6]);
            config.runGate.getStart[7] = ini.get(RUN_GATE_SECTION_NAME).get("GetStart8", Boolean.class, config.runGate.getStart[7]);
            // gate port
            config.runGate.gatePort[0] = ini.get(RUN_GATE_SECTION_NAME).get("GatePort1", Integer.class, config.runGate.gatePort[0]);
            config.runGate.gatePort[1] = ini.get(RUN_GATE_SECTION_NAME).get("GatePort2", Integer.class, config.runGate.gatePort[1]);
            config.runGate.gatePort[2] = ini.get(RUN_GATE_SECTION_NAME).get("GatePort3", Integer.class, config.runGate.gatePort[2]);
            config.runGate.gatePort[3] = ini.get(RUN_GATE_SECTION_NAME).get("GatePort4", Integer.class, config.runGate.gatePort[3]);
            config.runGate.gatePort[4] = ini.get(RUN_GATE_SECTION_NAME).get("GatePort5", Integer.class, config.runGate.gatePort[4]);
            config.runGate.gatePort[5] = ini.get(RUN_GATE_SECTION_NAME).get("GatePort6", Integer.class, config.runGate.gatePort[5]);
            config.runGate.gatePort[6] = ini.get(RUN_GATE_SECTION_NAME).get("GatePort7", Integer.class, config.runGate.gatePort[6]);
            config.runGate.gatePort[7] = ini.get(RUN_GATE_SECTION_NAME).get("GatePort8", Integer.class, config.runGate.gatePort[7]);
        }
        if (ini.get(SEL_GATE_SECTION_NAME) != null) {
            config.selGate.mainFormX = ini.get(SEL_GATE_SECTION_NAME).get("MainFormX", Integer.class, config.selGate.mainFormX);
            config.selGate.mainFormY = ini.get(SEL_GATE_SECTION_NAME).get("MainFormY", Integer.class, config.selGate.mainFormY);
            config.selGate.gatePort[0] = ini.get(SEL_GATE_SECTION_NAME).get("GatePort1", Integer.class, config.selGate.gatePort[0]);
            config.selGate.gatePort[1] = ini.get(SEL_GATE_SECTION_NAME).get("GatePort2", Integer.class, config.selGate.gatePort[1]);
            config.selGate.getStart1 = ini.get(SEL_GATE_SECTION_NAME).get("GetStart1", Boolean.class, config.selGate.getStart1);
            config.selGate.getStart2 = ini.get(SEL_GATE_SECTION_NAME).get("GetStart2", Boolean.class, config.selGate.getStart2);
        }
        if (ini.get(LOGIN_GATE_SECTION_NAME) != null) {
            config.loginGate.mainFormX = ini.get(LOGIN_GATE_SECTION_NAME).get("MainFormX", Integer.class, config.loginGate.mainFormX);
            config.loginGate.mainFormY = ini.get(LOGIN_GATE_SECTION_NAME).get("MainFormY", Integer.class, config.loginGate.mainFormY);
            config.loginGate.gatePort = ini.get(LOGIN_GATE_SECTION_NAME).get("GatePort", Integer.class, config.loginGate.gatePort);
            config.loginGate.getStart = ini.get(LOGIN_GATE_SECTION_NAME).get("GetStart", Boolean.class, config.loginGate.getStart);
        }
        if (ini.get(PLUG_TOP_SECTION_NAME) != null) {
            config.plugTop.mainFormX = ini.get(PLUG_TOP_SECTION_NAME).get("MainFormX", Integer.class, config.plugTop.mainFormX);
            config.plugTop.mainFormY = ini.get(PLUG_TOP_SECTION_NAME).get("MainFormY", Integer.class, config.plugTop.mainFormY);
            config.plugTop.getStart = ini.get(PLUG_TOP_SECTION_NAME).get("GetStart", Boolean.class, config.plugTop.getStart);
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
            AlertDialog.showError("保存配置文件时出错！", e);
        }
    }

    public static class Program {
        public boolean getStart;
        public boolean reStart;
        public byte startStatus;
        public String programFile;
        public String directory;
        public Process process;
        public Integer mainFormX;
        public Integer mainFormY;
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
        public final DBServerConfig dbServer = new DBServerConfig();
        public final LoginSrvConfig loginSrv = new LoginSrvConfig();
        public final M2ServerConfig m2Server = new M2ServerConfig();
        public final LogServerConfig logServer = new LogServerConfig();
        public final RunGateConfig runGate = new RunGateConfig();
        public final SelGateConfig selGate = new SelGateConfig();
        public final LoginGateConfig loginGate = new LoginGateConfig();
        public final PlugTopConfig plugTop = new PlugTopConfig();
    }
}
