package randall.gamecenter.model.profile;

import lombok.Data;
import org.ini4j.Profile;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("gamecenter.account")
public class AccountProfile implements IniReader, IniWriter {
  private Integer x;
  private Integer y;
  private Integer port;
  private Integer serverPort;
  private Integer monitorPort;
  private Boolean enabled;
  private String path;

  @Override public void read(Profile.Section section) {
    x = section.get("MainFormX", Integer.class, x);
    y = section.get("MainFormY", Integer.class, y);
    port = section.get("GatePort", Integer.class, port);
    serverPort = section.get("ServerPort", Integer.class, serverPort);
    monitorPort = section.get("MonPort", Integer.class, monitorPort);
    enabled = section.get("GetStart", Boolean.class, enabled);
  }

  @Override public void write(Profile.Section section) {
    section.put("MainFormX", x);
    section.put("MainFormY", y);
    section.put("GatePort", port);
    section.put("ServerPort", serverPort);
    section.put("MonPort", monitorPort);
    section.put("GetStart", enabled);
  }
}
