package randall.gamecenter.control.model;

import java.time.LocalDateTime;

public enum StartMode {
  NORMAL("正常启动"),
  DELAY("延时启动") {
    @Override public LocalDateTime compute(int hours, int minutes) {
      return super.compute(hours, minutes).plusHours(hours).plusMinutes(minutes);
    }
  },
  TIMING("定时启动") {
    @Override public LocalDateTime compute(int hours, int minutes) {
      final LocalDateTime now = super.compute(hours, minutes);
      LocalDateTime target = now.withHour(hours).withMinute(minutes);
      if (target.isBefore(now)) {
        target = target.plusDays(1);
      }
      return target;
    }
  },
  ;

  public final String text;

  StartMode(String text) {
    this.text = text;
  }

  public LocalDateTime compute(int hours, int minutes) {
    return LocalDateTime.now();
  }

  @Override
  public String toString() {
    return text;
  }
}
