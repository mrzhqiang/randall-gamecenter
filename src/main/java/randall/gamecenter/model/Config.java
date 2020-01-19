package randall.gamecenter.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.ini4j.Ini;
import org.ini4j.Wini;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import randall.common.ui.Dialogs;
import randall.common.util.IOHelper;
import randall.gamecenter.model.profile.AccountProfile;
import randall.gamecenter.model.profile.CoreProfile;
import randall.gamecenter.model.profile.DatabaseProfile;
import randall.gamecenter.model.profile.HomeProfile;
import randall.gamecenter.model.profile.LoggerProfile;
import randall.gamecenter.model.profile.LoginProfile;
import randall.gamecenter.model.profile.RoleProfile;
import randall.gamecenter.model.profile.RunProfile;
import randall.gamecenter.model.profile.TopProfile;

@Slf4j(topic = "randall")
@Getter
@Component
public class Config {
  private static final String FILENAME = "Config.ini";

  private final HomeProfile home;
  private final DatabaseProfile database;
  private final AccountProfile account;
  private final CoreProfile core;
  private final LoggerProfile logger;
  private final RunProfile run;
  private final RoleProfile role;
  private final LoginProfile login;
  private final TopProfile top;

  public Ini ini;

  @Autowired
  public Config(HomeProfile home, DatabaseProfile database, AccountProfile account,
      CoreProfile core, LoggerProfile logger, RunProfile run, RoleProfile role, LoginProfile login,
      TopProfile top) {
    this.home = home;
    this.database = database;
    this.account = account;
    this.core = core;
    this.logger = logger;
    this.run = run;
    this.role = role;
    this.login = login;
    this.top = top;

    try {
      Path path = Paths.get(home.getPath(), FILENAME);
      if (Files.notExists(path)) {
        IOHelper.mkdir(path.getParent());
        IOHelper.create(path);
      }
      ini = new Wini(path.toFile());
    } catch (IOException e) {
      Dialogs.error("初始化配置文件出错！！", e).show();
    }
  }

  public void load() {
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

  public void save() {
    try {
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
    } catch (IOException e) {
      Dialogs.error("保存配置文件时出错！", e).show();
    }
  }
}
