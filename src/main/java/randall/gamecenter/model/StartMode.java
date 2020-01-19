package randall.gamecenter.model;

public enum StartMode {
  NORMAL("正常启动"),
  DELAY("延时启动"),
  TIMING("定时启动"),
  ;

  public final String text;

  StartMode(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }
}
