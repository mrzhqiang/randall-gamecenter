package randall.gamecenter.viewmodel;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import randall.common.ui.Dialogs;
import randall.gamecenter.viewmodel.config.ProgramViewModel;

@Slf4j(topic = "randall")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class ConfigViewModel {
  private final StringProperty homePath = new SimpleStringProperty("");
  private final StringProperty homeDatabase = new SimpleStringProperty("");
  private final StringProperty homeName = new SimpleStringProperty("");
  private final StringProperty homeHost = new SimpleStringProperty("");
  private final ObjectProperty<Integer> portOffset = new SimpleObjectProperty<>(0);
  private final BooleanProperty wuxingEnabled = new SimpleBooleanProperty(true);

  private final ProgramViewModel loginGateVM;
  private final ProgramViewModel roleGateVM;
  private final ProgramViewModel runGateVM;
  private final ProgramViewModel accountServerVM;
  private final ProgramViewModel databaseServerVM;
  private final ProgramViewModel loggerServerVM;
  private final ProgramViewModel coreServerVM;
  private final ProgramViewModel topPlugVM;

  private final CompositeDisposable disposable = new CompositeDisposable();

  public void bindPortOffset(Spinner<Integer> spinner) {
    spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99, 0));
    spinner.getValueFactory().valueProperty().bindBidirectional(portOffset);
    disposable.add(JavaFxObservable.valuesOf(portOffset).subscribe(this::changeAllPort));
  }

  private void changeAllPort(Integer newValue) {
  }

  public void onHomeDefault() {
  }

  public void onReloadAll() {

  }

  public void onHomeNext(SelectionModel<Tab> model) {
    String gameDir = gameDirTextField.getText().trim();
    if (Strings.isNullOrEmpty(gameDir)) {
      Dialogs.warn("游戏目录输入不正确！！").show();
      gameDirTextField.requestFocus();
      return;
    }
    if (!gameDir.endsWith("\\")) {
      Dialogs.warn("游戏目录必须以“\\”结尾！！").show();
      gameDirTextField.requestFocus();
      return;
    }
    String gameName = gameNameTextField.getText().trim();
    if (Strings.isNullOrEmpty(gameName)) {
      Dialogs.warn("游戏名称输入不正确！！").show();
      gameNameTextField.requestFocus();
      return;
    }
    String dbName = dbNameTextField.getText().trim();
    if (Strings.isNullOrEmpty(dbName)) {
      Dialogs.warn("数据库名称输入不正确！！").show();
      dbNameTextField.requestFocus();
      return;
    }
    String ipAddress1 = primaryAddressTextField.getText().trim();
    if (Strings.isNullOrEmpty(ipAddress1) || !Networks.isAddressV4(ipAddress1)) {
      Dialogs.warn("游戏 IP 地址输入不正确！！").show();
      primaryAddressTextField.requestFocus();
      return;
    }

    share.gameDirectory = gameDir;
    share.gameName = gameName;
    share.heroDBName = dbName;
    share.extIPAddr = ipAddress1;
    share.closeWuXinEnabled = closeWuxingCheckBox.isSelected();

    model.selectNext();
  }

  public void bindHomePath(TextField textField) {
    textField.textProperty().bindBidirectional(homePath);
  }

  public void bindHomeDatabase(TextField textField) {
    textField.textProperty().bindBidirectional(homeDatabase);
  }

  public void bindHomeName(TextField textField) {
    textField.textProperty().bindBidirectional(homeName);
  }

  public void bindHomeHost(TextField textField) {
    textField.textProperty().bindBidirectional(homeHost);
  }

  public void bindWuxing(CheckBox checkBox) {
    checkBox.selectedProperty().bindBidirectional(wuxingEnabled);
  }

  public void bindLoginX(TextField textField) {
    loginGateVM.bindX(textField);
  }

  public void bindLoginY(TextField textField) {
    loginGateVM.bindY(textField);
  }

  public void bindLoginPort(TextField textField) {
    loginGateVM.bindPort(textField);
  }

  public void bindLoginEnabled(CheckBox checkBox) {
    loginGateVM.bindEnabled(checkBox);
  }

  public void bindRoleX(TextField textField) {
    loginGateVM.bindX(textField);
  }

  public void bindRoleY(TextField textField) {
    loginGateVM.bindY(textField);
  }

  public void bindRolePort(TextField textField) {
    loginGateVM.bindPort(textField);
  }

  public void bindRoleEnabled(CheckBox checkBox) {
    loginGateVM.bindEnabled(checkBox);
  }

  public void bindRunX(TextField textField) {
    loginGateVM.bindX(textField);
  }

  public void bindRunY(TextField textField) {
    loginGateVM.bindY(textField);
  }

  public void bindRunPort(TextField textField) {
    loginGateVM.bindPort(textField);
  }

  public void bindRunEnabled(CheckBox checkBox) {
    loginGateVM.bindEnabled(checkBox);
  }

  public void bindAccountX(TextField textField) {
    loginGateVM.bindX(textField);
  }

  public void bindAccountY(TextField textField) {
    loginGateVM.bindY(textField);
  }

  public void bindAccountPort(TextField textField) {
    loginGateVM.bindPort(textField);
  }

  public void bindAccountServerPort(TextField textField) {
    loginGateVM.bindServerPort(textField);
  }

  public void bindAccountMonitorPort(TextField textField) {
    loginGateVM.bindMonitorPort(textField);
  }

  public void bindAccountEnabled(CheckBox checkBox) {
    loginGateVM.bindEnabled(checkBox);
  }

  public void bindDatabaseX(TextField textField) {
    loginGateVM.bindX(textField);
  }

  public void bindDatabaseY(TextField textField) {
    loginGateVM.bindY(textField);
  }

  public void bindDatabasePort(TextField textField) {
    loginGateVM.bindPort(textField);
  }

  public void bindDatabaseServerPort(TextField textField) {
    loginGateVM.bindServerPort(textField);
  }

  public void bindDatabaseEnabled(CheckBox checkBox) {
    loginGateVM.bindEnabled(checkBox);
  }

  public void bindLoggerX(TextField textField) {
    loginGateVM.bindX(textField);
  }

  public void bindLoggerY(TextField textField) {
    loginGateVM.bindY(textField);
  }

  public void bindLoggerPort(TextField textField) {
    loginGateVM.bindPort(textField);
  }

  public void bindLoggerEnabled(CheckBox checkBox) {
    loginGateVM.bindEnabled(checkBox);
  }

  public void bindCoreX(TextField textField) {
    loginGateVM.bindX(textField);
  }

  public void bindCoreY(TextField textField) {
    loginGateVM.bindY(textField);
  }

  public void bindCorePort(TextField textField) {
    loginGateVM.bindPort(textField);
  }

  public void bindCoreServerPort(TextField textField) {
    loginGateVM.bindServerPort(textField);
  }

  public void bindCoreEnabled(CheckBox checkBox) {
    loginGateVM.bindEnabled(checkBox);
  }

  public void bindTopX(TextField textField) {
    loginGateVM.bindX(textField);
  }

  public void bindTopY(TextField textField) {
    loginGateVM.bindY(textField);
  }

  public void bindTopEnabled(CheckBox checkBox) {
    loginGateVM.bindEnabled(checkBox);
  }
}
