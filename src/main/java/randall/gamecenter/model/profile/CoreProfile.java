package randall.gamecenter.model.profile;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("gamecenter.core")
public class CoreProfile {
  private Integer x;
  private Integer y;
  private Integer port;
  private Integer serverPort;
  private Boolean enabled;
  private String path;
}
