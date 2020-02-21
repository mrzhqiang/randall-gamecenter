package randall.gamecenter.config.model;

import com.google.common.base.Preconditions;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.RequiredArgsConstructor;
import org.ini4j.Ini;
import org.ini4j.Profile;

@RequiredArgsConstructor(staticName = "ofSection")
public class ServerConfig implements IniAdapter {
  private final String section;

  private final IntegerProperty x = new SimpleIntegerProperty();
  private final IntegerProperty y = new SimpleIntegerProperty();
  private final IntegerProperty port = new SimpleIntegerProperty();
  private final IntegerProperty server = new SimpleIntegerProperty();
  private final IntegerProperty monitor = new SimpleIntegerProperty();
  private final BooleanProperty enabled = new SimpleBooleanProperty();
  private final StringProperty path = new SimpleStringProperty();
  private final StringProperty filename = new SimpleStringProperty();

  @Override public void read(Ini ini) {
    Preconditions.checkNotNull(ini, "ini == null");
    Optional.ofNullable(ini.get(section)).ifPresent(section -> {
      x.setValue(section.get("x", Integer.class, x.getValue()));
      y.setValue(section.get("y", Integer.class, y.getValue()));
      port.setValue(section.get("port", Integer.class, port.getValue()));
      server.setValue(section.get("server", Integer.class, server.getValue()));
      monitor.setValue(section.get("monitor", Integer.class, monitor.getValue()));
      enabled.setValue(section.get("enabled", Boolean.class, enabled.getValue()));
      path.setValue(section.get("path", path.getValue()));
      filename.setValue(section.get("filename", filename.getValue()));
    });
  }

  @Override public void write(Ini ini) {
    Preconditions.checkNotNull(ini, "ini == null");
    Profile.Section section =
        Optional.ofNullable(ini.get(this.section)).orElse(ini.add(this.section));
    section.put("x", x.getValue());
    section.put("y", y.getValue());
    Optional.ofNullable(port.getValue()).ifPresent(integer -> section.put("port", integer));
    Optional.ofNullable(server.getValue()).ifPresent(integer -> section.put("server", integer));
    Optional.ofNullable(monitor.getValue()).ifPresent(integer -> section.put("monitor", integer));
    section.put("enabled", enabled.getValue());
    section.put("path", path.getValue());
    section.put("filename", filename.getValue());
  }

  public String getSection() {
    return section;
  }

  public int getX() {
    return x.get();
  }

  public IntegerProperty xProperty() {
    return x;
  }

  public void setX(int x) {
    this.x.set(x);
  }

  public int getY() {
    return y.get();
  }

  public IntegerProperty yProperty() {
    return y;
  }

  public void setY(int y) {
    this.y.set(y);
  }

  public int getPort() {
    return port.get();
  }

  public IntegerProperty portProperty() {
    return port;
  }

  public void setPort(int port) {
    this.port.set(port);
  }

  public int getServer() {
    return server.get();
  }

  public IntegerProperty serverProperty() {
    return server;
  }

  public void setServer(int server) {
    this.server.set(server);
  }

  public int getMonitor() {
    return monitor.get();
  }

  public IntegerProperty monitorProperty() {
    return monitor;
  }

  public void setMonitor(int monitor) {
    this.monitor.set(monitor);
  }

  public boolean isEnabled() {
    return enabled.get();
  }

  public BooleanProperty enabledProperty() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled.set(enabled);
  }

  public String getPath() {
    return path.get();
  }

  public String getFilename() {
    return filename.get();
  }
}
