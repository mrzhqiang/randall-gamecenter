package randall.gamecenter.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j(topic = "randall")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Component
public class Profile {
  private final HomeProfile home;
  private final DatabaseProfile database;
  private final AccountProfile account;
  private final CoreProfile core;
  private final LoggerProfile logger;
  private final RunProfile run;
  private final RoleProfile role;
  private final LoginProfile login;
  private final TopProfile top;

  public void load() {
  }

  public void save() {
  }
}
