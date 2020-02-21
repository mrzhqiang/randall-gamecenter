package randall.gamecenter.backup.model;

import com.google.common.collect.Lists;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data(staticConstructor = "create")
public class BackupData {
  // 源头，表示需要备份的目录
  private final StringProperty source = new SimpleStringProperty();
  // 目标，表示备份目录的拷贝
  private final StringProperty destination = new SimpleStringProperty();
  private boolean enabled = true;
  private BackupMode mode = BackupMode.TIMING;
  private boolean compressEnabled = true;
  private int hours = 0;
  private int minutes = 0;
  private BackupState state = BackupState.DEFAULT;
  private int errorCount = 0;
  private LocalDateTime startTimestamp = LocalDateTime.now();
  private LocalDate today = LocalDate.now();
  private final List<String> backupFiles = Lists.newArrayList();
  private boolean stopSearchEnabled = false;

  public String getSource() {
    return source.get();
  }

  public StringProperty sourceProperty() {
    return source;
  }

  public void setSource(String source) {
    this.source.set(source);
  }

  public String getDestination() {
    return destination.get();
  }

  public StringProperty destinationProperty() {
    return destination;
  }

  public void setDestination(String destination) {
    this.destination.set(destination);
  }
}
