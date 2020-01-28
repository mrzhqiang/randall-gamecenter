package randall.gamecenter.model.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j(topic = "randall")
@Getter
@Component
public class Config {
  private static final String DATABASE_SECTION_NAME = "DBServer";
  private static final String ACCOUNT_SECTION_NAME = "LoginSrv";
  private static final String CORE_SECTION_NAME = "M2Server";
  private static final String LOGGER_SECTION_NAME = "LogServer";
  private static final String RUN_SECTION_NAME = "RunGate";
  private static final String ROLE_SECTION_NAME = "SelGate";
  private static final String LOGIN_SECTION_NAME = "LoginGate";
  private static final String TOP_SECTION_NAME = "PlugTop";

  private final BasicConfig home = new BasicConfig();
  private final ServerConfig database = new ServerConfig(DATABASE_SECTION_NAME);
  private final MonitorServerConfig account = new MonitorServerConfig(ACCOUNT_SECTION_NAME);
  private final ServerConfig core = new ServerConfig(CORE_SECTION_NAME);
  private final ServerConfig logger = new ServerConfig(LOGGER_SECTION_NAME);
  private final GateConfig run = new GateConfig(RUN_SECTION_NAME);
  private final GateConfig role = new GateConfig(ROLE_SECTION_NAME);
  private final GateConfig login = new GateConfig(LOGIN_SECTION_NAME);
  private final PlugConfig top = new PlugConfig(TOP_SECTION_NAME);
}
