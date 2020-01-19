package randall.gamecenter.model.profile;

import com.google.common.base.Preconditions;
import lombok.Data;
import org.ini4j.Ini;
import org.ini4j.Profile;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("gamecenter.role")
public class RoleProfile implements IniReader, IniWriter {
  private static final String SECTION_NAME = "SelGate";

  private Integer x;
  private Integer y;
  private Integer port;
  private Boolean enabled;
  private String path;

  @Override public void read(Ini ini) {
    Preconditions.checkNotNull(ini, "ini == null");
    if (ini.containsKey(SECTION_NAME)) {
      Profile.Section section = ini.get(SECTION_NAME);
      x = section.get("MainFormX", Integer.class, x);
      y = section.get("MainFormY", Integer.class, y);
      port = section.get("GatePort", Integer.class, port);
      enabled = section.get("GetStart", Boolean.class, enabled);
    }
  }

  @Override public void write(Ini ini) {
    Preconditions.checkNotNull(ini, "ini == null");
    if (ini.containsKey(SECTION_NAME)) {
      Profile.Section section = ini.get(SECTION_NAME);
      section.put("MainFormX", x);
      section.put("MainFormY", y);
      section.put("GatePort", port);
      section.put("GetStart", enabled);
    }
  }
}
