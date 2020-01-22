package randall.gamecenter.model.config;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.ini4j.Profile;

public class GateConfig extends PlugConfig {
  private final IntegerProperty port = new SimpleIntegerProperty(0);

  public GateConfig(String sectionName) {
    super(sectionName);
  }

  @Override protected void readSection(Profile.Section section) {
    super.readSection(section);
    if (section.containsKey("GatePort")) {
      setPort(section.get("GatePort", Integer.class, getPort()));
    }
  }

  @Override protected void writeSection(Profile.Section section) {
    super.writeSection(section);
    if (section.containsKey("GatePort")) {
      section.put("GatePort", getPort());
    }
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
}
