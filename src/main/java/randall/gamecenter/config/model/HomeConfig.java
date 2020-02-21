package randall.gamecenter.config.model;

import com.google.common.base.Preconditions;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.RequiredArgsConstructor;
import org.ini4j.Ini;
import org.ini4j.Profile;

@RequiredArgsConstructor(staticName = "ofSection")
public class HomeConfig implements IniAdapter {
  private final String section;

  private final StringProperty path = new SimpleStringProperty();
  private final StringProperty database = new SimpleStringProperty();
  private final StringProperty name = new SimpleStringProperty();
  private final StringProperty version = new SimpleStringProperty();
  private final StringProperty host = new SimpleStringProperty();
  private final BooleanProperty backup = new SimpleBooleanProperty();
  private final BooleanProperty wuxing = new SimpleBooleanProperty();

  @Override public void read(Ini ini) {
    Preconditions.checkNotNull(ini, "ini == null");
    Optional.ofNullable(ini.get(section)).ifPresent(section -> {
      path.setValue(section.get("path", path.getValue()));
      name.setValue(section.get("name", name.getValue()));
      database.setValue(section.get("database", database.getValue()));
      version.setValue(section.get("version", version.getValue()));
      host.setValue(section.get("host", host.getValue()));
      backup.setValue(section.get("backup", Boolean.class, backup.getValue()));
      wuxing.setValue(section.get("wuxing", Boolean.class, wuxing.getValue()));
    });
  }

  @Override public void write(Ini ini) {
    Preconditions.checkNotNull(ini, "ini == null");
    Profile.Section section =
        Optional.ofNullable(ini.get(this.section)).orElse(ini.add(this.section));
    section.put("path", path.getValue());
    section.put("name", name.getValue());
    section.put("database", database.getValue());
    section.put("version", version.getValue());
    section.put("host", host.getValue());
    section.put("backup", backup.getValue());
    section.put("wuxing", wuxing.getValue());
  }

  public String getSection() {
    return section;
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

  public String getDatabase() {
    return database.get();
  }

  public StringProperty databaseProperty() {
    return database;
  }

  public void setDatabase(String database) {
    this.database.set(database);
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
