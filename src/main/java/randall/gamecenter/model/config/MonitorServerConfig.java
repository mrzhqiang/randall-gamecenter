package randall.gamecenter.model.config;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.ini4j.Profile;

public class MonitorServerConfig extends ServerConfig {
  private final IntegerProperty monitorPort = new SimpleIntegerProperty(0);

  public MonitorServerConfig(String sectionName) {
    super(sectionName);
  }

  @Override protected void readSection(Profile.Section section) {
    super.readSection(section);
    setMonitorPort(section.get("MonPort", Integer.class, getMonitorPort()));
  }

  @Override protected void writeSection(Profile.Section section) {
    super.writeSection(section);
    section.put("MonPort", getMonitorPort());
  }

  public int getMonitorPort() {
    return monitorPort.get();
  }

  public IntegerProperty monitorPortProperty() {
    return monitorPort;
  }

  public void setMonitorPort(int monitorPort) {
    this.monitorPort.set(monitorPort);
  }
}
