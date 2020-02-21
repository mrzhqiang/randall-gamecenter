package randall.gamecenter.config.model;

import java.io.IOException;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ini4j.Ini;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import randall.gamecenter.util.IniLoader;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Component
public class Config {
  private static final String FILENAME = "Config.ini";
  private static final String DEFAULT_FILE = "classpath:" + FILENAME;

  private static final String HOME_SECTION = "home";
  private static final String DATABASE_SECTION = "database";
  private static final String ACCOUNT_SECTION = "account";
  private static final String CORE_SECTION = "core";
  private static final String LOGGER_SECTION = "logger";
  private static final String RUN_SECTION = "run";
  private static final String ROLE_SECTION = "role";
  private static final String LOGIN_SECTION = "login";
  private static final String TOP_SECTION = "top";

  public final HomeConfig home = HomeConfig.ofSection(HOME_SECTION);
  public final ServerConfig database = ServerConfig.ofSection(DATABASE_SECTION);
  public final ServerConfig account = ServerConfig.ofSection(ACCOUNT_SECTION);
  public final ServerConfig core = ServerConfig.ofSection(CORE_SECTION);
  public final ServerConfig logger = ServerConfig.ofSection(LOGGER_SECTION);
  public final ServerConfig run = ServerConfig.ofSection(RUN_SECTION);
  public final ServerConfig role = ServerConfig.ofSection(ROLE_SECTION);
  public final ServerConfig login = ServerConfig.ofSection(LOGIN_SECTION);
  public final ServerConfig top = ServerConfig.ofSection(TOP_SECTION);

  @Value(DEFAULT_FILE)
  private Resource defaultFile;

  public void init() {
    readAll(IniLoader.load(defaultFile));
    loadAll();
  }

  public void loadAll() {
    readAll(IniLoader.load(Paths.get(home.getPath(), FILENAME)));
  }

  public void loadDefault(IniAdapter adapter) {
    adapter.read(IniLoader.load(defaultFile));
  }

  public void load(IniAdapter adapter) {
    adapter.read(IniLoader.load(Paths.get(home.getPath(), FILENAME)));
  }

  public void save(IniAdapter adapter) {
    Ini ini = IniLoader.load(Paths.get(home.getPath(), FILENAME));
    adapter.write(ini);
    try {
      ini.store();
    } catch (IOException e) {
      log.error("保存配置文件失败！", e);
      throw new RuntimeException(e);
    }
  }

  public void saveAll() {
    try {
      writeAll(IniLoader.load(Paths.get(home.getPath(), FILENAME)));
    } catch (IOException e) {
      log.error("保存配置文件失败！", e);
      throw new RuntimeException(e);
    }
  }

  private void readAll(Ini ini) {
    home.read(ini);
    database.read(ini);
    account.read(ini);
    core.read(ini);
    logger.read(ini);
    run.read(ini);
    role.read(ini);
    login.read(ini);
    top.read(ini);
  }

  private void writeAll(Ini ini) throws IOException {
    home.write(ini);
    database.write(ini);
    account.write(ini);
    core.write(ini);
    logger.write(ini);
    run.write(ini);
    role.write(ini);
    login.write(ini);
    top.write(ini);
    ini.store();
  }
}
