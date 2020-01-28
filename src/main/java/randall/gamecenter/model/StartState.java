package randall.gamecenter.model;

public enum StartState {
  STOPPED("启动游戏"),
  STARTING("取消启动"),
  CANCEL_START("继续启动"),
  RUNNING("停止游戏"),
  STOPPING("取消停止"),
  CANCEL_STOP("继续停止"),
  ;

  private final String text;

  StartState(String text) {
    this.text = text;
  }

  @Override public String toString() {
    return text;
  }
}
