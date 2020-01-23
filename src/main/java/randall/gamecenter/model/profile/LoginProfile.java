package randall.gamecenter.model.profile;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("gamecenter.login")
public class LoginProfile {
  private Integer x;
  private Integer y;
  private Integer port;
  private Boolean enabled;
  private String path;
}
