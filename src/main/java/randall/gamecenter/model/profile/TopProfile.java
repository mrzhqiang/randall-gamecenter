package randall.gamecenter.model.profile;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("gamecenter.top")
public class TopProfile {
  private Integer x;
  private Integer y;
  private Boolean enabled;
  private String path;
}
