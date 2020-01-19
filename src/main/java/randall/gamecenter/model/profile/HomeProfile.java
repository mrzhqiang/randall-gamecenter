package randall.gamecenter.model.profile;

import lombok.Data;
import org.ini4j.Profile;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("gamecenter.home")
public class HomeProfile implements IniReader, IniWriter {
  private String path;
  private String name;
  private String alias;
  private String version;
  private String fullName;
  private String database;
  private String host;
  private Boolean backup;
  private Boolean wuxing;

  @Override public void read(Profile.Section section) {
    // TODO mrzhqiang: 自定义配置文件的 section key，不再依赖旧引擎版本
    path = section.get("GameDirectory", path);
    name = section.get("GameName", name);
    database = section.get("HeroDBName", database);
    host = section.get("ExtIPaddr", host);
    backup = section.get("AutoRunBak", Boolean.class, backup);
    wuxing = section.get("CloseWuXin", Boolean.class, wuxing);
  }

  @Override public void write(Profile.Section section) {
    // TODO mrzhqiang: 自定义配置文件的 section key，不再依赖旧引擎版本
    section.put("GameDirectory", path);
    section.put("GameName", name);
    section.put("HeroDBName", database);
    section.put("ExtIPaddr", host);
    section.put("AutoRunBak", backup);
    section.put("CloseWuXin", wuxing);
  }
}
