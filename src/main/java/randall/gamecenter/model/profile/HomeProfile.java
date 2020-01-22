package randall.gamecenter.model.profile;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("gamecenter.home")
public class HomeProfile {
  private String path;
  private String name;
  private String alias;
  private String version;
  private String fullName;
  private String database;
  private String host;
  private Boolean backup;
  private Boolean wuxing;
}
