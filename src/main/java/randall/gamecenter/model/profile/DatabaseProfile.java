package randall.gamecenter.model.profile;

import com.google.common.base.Preconditions;
import lombok.Data;
import org.ini4j.Ini;
import org.ini4j.Profile;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("gamecenter.database")
public class DatabaseProfile implements IniReader, IniWriter {
  private static final String SECTION_NAME = "DBServer";

  private Integer x;
  private Integer y;
  private Integer port;
  private Integer serverPort;
  private Boolean enabled;
  private String path;

  @Override public void read(Ini ini) {
    Preconditions.checkNotNull(ini, "ini == null");
    if (ini.containsKey(SECTION_NAME)) {
      Profile.Section section = ini.get(SECTION_NAME);
      x = section.get("MainFormX", Integer.class, x);
      y = section.get("MainFormY", Integer.class, y);
      port = section.get("GatePort", Integer.class, port);
      serverPort = section.get("ServerPort", Integer.class, serverPort);
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
      section.put("ServerPort", serverPort);
      section.put("GetStart", enabled);
    }
  }
}
