package randall.gamecenter.model.profile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j(topic = "randall")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Getter
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
}
