package randall.gamecenter.model.profile;

import lombok.Data;
import org.ini4j.Profile;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("gamecenter.top")
public class TopProfile implements IniReader, IniWriter {
  private Integer x;
  private Integer y;
  private Boolean enabled;
  private String path;

  @Override public void read(Profile.Section section) {
    x = section.get("MainFormX", Integer.class, x);
    y = section.get("MainFormY", Integer.class, y);
    enabled = section.get("GetStart", Boolean.class, enabled);
  }

  @Override public void write(Profile.Section section) {
    section.put("MainFormX", x);
    section.put("MainFormY", y);
    section.put("GetStart", enabled);
  }
}
