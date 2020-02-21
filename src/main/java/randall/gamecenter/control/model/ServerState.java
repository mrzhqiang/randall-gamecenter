package randall.gamecenter.control.model;

public enum ServerState {
  STOPPED("启动服务"),
  STARTING("取消启动"),
  CANCEL_START("继续启动"),
  RUNNING("停止服务"),
  STOPPING("取消停止"),
  CANCEL_STOP("继续停止"),
  ;

  private final String text;

  ServerState(String text) {
    this.text = text;
  }

  public String text() {
    return text;
  }
}
