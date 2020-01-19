package randall.gamecenter.model.profile;

import org.ini4j.Profile;

public interface IniWriter {
  void write(Profile.Section section);
}
