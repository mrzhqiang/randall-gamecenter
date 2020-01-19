package randall.gamecenter.model;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import randall.gamecenter.model.profile.AccountProfile;
import randall.gamecenter.model.profile.CoreProfile;
import randall.gamecenter.model.profile.DatabaseProfile;
import randall.gamecenter.model.profile.HomeProfile;
import randall.gamecenter.model.profile.LoggerProfile;
import randall.gamecenter.model.profile.LoginProfile;
import randall.gamecenter.model.profile.RoleProfile;
import randall.gamecenter.model.profile.RunProfile;
import randall.gamecenter.model.profile.TopProfile;

@Getter
@Component
public class Config {
  private final HomeProfile home;
  private final DatabaseProfile database;
  private final AccountProfile account;
  private final CoreProfile core;
  private final LoggerProfile logger;
  private final RunProfile run;
  private final RoleProfile role;
  private final LoginProfile login;
  private final TopProfile top;

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
  }
}
