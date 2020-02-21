package randall.gamecenter.backup.model;

import javafx.scene.paint.Color;

public enum BackupFunctionStatus {
  STOPPED("启动", "数据备份功能已停止..", Color.RED),
  STARTED("停止", "数据备份功能启动中..", Color.GREEN),
  ;

  private final String label;
  private final String message;
  private final Color color;

  BackupFunctionStatus(String label, String message, Color color) {
    this.label = label;
    this.message = message;
    this.color = color;
  }

  public String getLabel() {
    return label;
  }

  public String getMessage() {
    return message;
  }

  public Color getColor() {
    return color;
  }
}
