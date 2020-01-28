package randall.gamecenter.viewmodel.config;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Scope("prototype")
@Service
public class ProgramViewModel {
  private final StringProperty x = new SimpleStringProperty("0");
  private final StringProperty y = new SimpleStringProperty("0");
  private final StringProperty port = new SimpleStringProperty("0");
  private final StringProperty serverPort = new SimpleStringProperty("0");
  private final StringProperty monitorPort = new SimpleStringProperty("0");
  private final BooleanProperty enabled = new SimpleBooleanProperty(false);

  public void bindX(TextField textField) {
    textField.textProperty().bindBidirectional(x);
  }

  public void bindY(TextField textField) {
    textField.textProperty().bindBidirectional(y);
  }

  public void bindPort(TextField textField) {
    textField.textProperty().bindBidirectional(port);
  }

  public void bindServerPort(TextField textField) {
    textField.textProperty().bindBidirectional(serverPort);
  }

  public void bindMonitorPort(TextField textField) {
    textField.textProperty().bindBidirectional(monitorPort);
  }

  public void bindEnabled(CheckBox checkBox) {
    checkBox.selectedProperty().bindBidirectional(enabled);
  }
}
