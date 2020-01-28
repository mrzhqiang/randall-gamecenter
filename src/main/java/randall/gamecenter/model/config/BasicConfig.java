package randall.gamecenter.model.config;

import com.google.common.base.Preconditions;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.ini4j.Ini;
import org.ini4j.Profile;

public class BasicConfig implements IniReader, IniWriter {
  private static final String SECTION_NAME = "GameConfig";

  private final StringProperty path = new SimpleStringProperty();
  private final StringProperty name = new SimpleStringProperty();
  private final StringProperty dbName = new SimpleStringProperty();
  private final StringProperty host = new SimpleStringProperty();
  private final BooleanProperty backup = new SimpleBooleanProperty();
  private final BooleanProperty wuxing = new SimpleBooleanProperty();

  @Override public void read(Ini ini) {
    Preconditions.checkNotNull(ini, "ini == null");
    if (ini.containsKey(SECTION_NAME)) {
      Profile.Section section = ini.get(SECTION_NAME);
      setPath(section.get("GameDirectory", getPath()));
      setName(section.get("GameName", getName()));
      setDbName(section.get("HeroDBName", getDbName()));
      setHost(section.get("ExtIPaddr", getHost()));
      setBackup(section.get("AutoRunBak", Boolean.class, isBackup()));
      setWuxing(section.get("CloseWuXin", Boolean.class, isWuxing()));
    }
  }

  @Override public void write(Ini ini) {
    Preconditions.checkNotNull(ini, "ini == null");
    if (ini.containsKey(SECTION_NAME)) {
      Profile.Section section = ini.get(SECTION_NAME);
      section.put("GameDirectory", getPath());
      section.put("GameName", getName());
      section.put("HeroDBName", getDbName());
      section.put("ExtIPaddr", getHost());
      section.put("AutoRunBak", isBackup());
      section.put("CloseWuXin", isWuxing());
    }
  }

  public String getPath() {
    return path.get();
  }

  public StringProperty pathProperty() {
    return path;
  }

  public void setPath(String path) {
    this.path.set(path);
  }

  public String getName() {
    return name.get();
  }

  public StringProperty nameProperty() {
    return name;
  }

  public void setName(String name) {
    this.name.set(name);
  }

  public String getDbName() {
    return dbName.get();
  }

  public StringProperty dbNameProperty() {
    return dbName;
  }

  public void setDbName(String dbName) {
    this.dbName.set(dbName);
  }

  public String getHost() {
    return host.get();
  }

  public StringProperty hostProperty() {
    return host;
  }

  public void setHost(String host) {
    this.host.set(host);
  }

  public boolean isBackup() {
    return backup.get();
  }

  public BooleanProperty backupProperty() {
    return backup;
  }

  public void setBackup(boolean backup) {
    this.backup.set(backup);
  }

  public boolean isWuxing() {
    return wuxing.get();
  }

  public BooleanProperty wuxingProperty() {
    return wuxing;
  }

  public void setWuxing(boolean wuxing) {
    this.wuxing.set(wuxing);
  }
}
