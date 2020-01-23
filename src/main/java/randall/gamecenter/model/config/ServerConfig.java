package randall.gamecenter.model.config;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.ini4j.Profile;

public class ServerConfig extends GateConfig {
  private final IntegerProperty serverPort = new SimpleIntegerProperty(0);

  public ServerConfig(String sectionName) {
    super(sectionName);
  }

  @Override protected void readSection(Profile.Section section) {
    super.readSection(section);
    if (section.containsKey("Port")) {
      setPort(section.get("Port", Integer.class, getPort()));
    }
    if (section.containsKey("MsgSrvPort")) {
      setServerPort(section.get("MsgSrvPort", Integer.class, getServerPort()));
    }
    if (section.containsKey("ServerPort")) {
      setServerPort(section.get("ServerPort", Integer.class, getServerPort()));
    }
  }

  @Override protected void writeSection(Profile.Section section) {
    super.writeSection(section);
    if (section.containsKey("Port")) {
      section.put("Port", getPort());
    }
    if (section.containsKey("MsgSrvPort")) {
      section.put("MsgSrvPort", getServerPort());
    }
    if (section.containsKey("ServerPort")) {
      section.put("ServerPort", getServerPort());
    }
  }

  public int getServerPort() {
    return serverPort.get();
  }

  public IntegerProperty serverPortProperty() {
    return serverPort;
  }

  public void setServerPort(int serverPort) {
    this.serverPort.set(serverPort);
  }
}
