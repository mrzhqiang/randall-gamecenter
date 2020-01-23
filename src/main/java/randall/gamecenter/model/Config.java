package randall.gamecenter.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import randall.gamecenter.model.config.BasicConfig;
import randall.gamecenter.model.config.GateConfig;
import randall.gamecenter.model.config.MonitorServerConfig;
import randall.gamecenter.model.config.PlugConfig;
import randall.gamecenter.model.config.ServerConfig;

@Slf4j(topic = "randall")
@Getter
public class Config {
  private static final String DATABASE_SERVER_SECTION_NAME = "DBServer";
  private static final String ACCOUNT_SERVER_SECTION_NAME = "LoginSrv";
  private static final String CORE_SERVER_SECTION_NAME = "M2Server";
  private static final String LOGGER_SERVER_SECTION_NAME = "LogServer";
  private static final String RUN_GATE_SECTION_NAME = "RunGate";
  private static final String ROLE_GATE_SECTION_NAME = "SelGate";
  private static final String LOGIN_GATE_SECTION_NAME = "LoginGate";
  private static final String TOP_PLUG_SECTION_NAME = "PlugTop";

  private final BasicConfig home = new BasicConfig();
  private final ServerConfig database = new ServerConfig(DATABASE_SERVER_SECTION_NAME);
  private final MonitorServerConfig account = new MonitorServerConfig(ACCOUNT_SERVER_SECTION_NAME);
  private final ServerConfig core = new ServerConfig(CORE_SERVER_SECTION_NAME);
  private final ServerConfig logger = new ServerConfig(LOGGER_SERVER_SECTION_NAME);
  private final GateConfig run = new GateConfig(RUN_GATE_SECTION_NAME);
  private final GateConfig role = new GateConfig(ROLE_GATE_SECTION_NAME);
  private final GateConfig login = new GateConfig(LOGIN_GATE_SECTION_NAME);
  private final PlugConfig top = new PlugConfig(TOP_PLUG_SECTION_NAME);
}
