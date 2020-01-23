package randall.gamecenter.model.config;

import com.google.common.base.Preconditions;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.RequiredArgsConstructor;
import org.ini4j.Ini;
import org.ini4j.Profile;

@RequiredArgsConstructor
public class PlugConfig implements IniReader, IniWriter {
  // 注意！一定不要用 bind 方法去绑定数据，这只能是单方向并且一次只能绑定一个监听器实例
  // 如果是单纯的数据绑定，就用双向绑定的方法，但是不能修改数据
  // 如果是需要处理一下数据，那就用 RxJavaFx 库的相关 API
  private final IntegerProperty x = new SimpleIntegerProperty(0);
  private final IntegerProperty y = new SimpleIntegerProperty(0);
  private final BooleanProperty enabled = new SimpleBooleanProperty(false);
  private final StringProperty path = new SimpleStringProperty("");

  private final String sectionName;

  @Override public void read(Ini ini) {
    Preconditions.checkNotNull(ini, "ini == null");
    if (ini.containsKey(sectionName)) {
      Profile.Section section = ini.get(sectionName);
      readSection(section);
    }
  }

  protected void readSection(Profile.Section section) {
    setX(section.get("MainFormX", Integer.class, getX()));
    setY(section.get("MainFormY", Integer.class, getY()));
    setEnabled(section.get("GetStart", Boolean.class, isEnabled()));
  }

  @Override public void write(Ini ini) {
    Preconditions.checkNotNull(ini, "ini == null");
    if (ini.containsKey(sectionName)) {
      Profile.Section section = ini.get(sectionName);
      writeSection(section);
    }
  }

  protected void writeSection(Profile.Section section) {
    section.put("MainFormX", getX());
    section.put("MainFormY", getY());
    section.put("GetStart", isEnabled());
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

  public StringProperty pathProperty() {
    return path;
  }

  public void setPath(String path) {
    this.path.set(path);
  }
}
