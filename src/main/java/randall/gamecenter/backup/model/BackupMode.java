package randall.gamecenter.backup.model;

import java.time.LocalTime;

public enum BackupMode {
  DELAY {
    @Override public LocalTime compute(LocalTime time, int hours, int minutes) {
      return super.compute(time, hours, minutes).plusHours(hours).plusMinutes(minutes);
    }
  },
  TIMING {
    @Override public LocalTime compute(LocalTime time, int hours, int minutes) {
      LocalTime now = super.compute(time, hours, minutes);
      return now.withHour(hours).withMinute(minutes);
    }
  },
  ;

  public LocalTime compute(LocalTime time, int hours, int minutes) {
    return time;
  }
}
