package randall.gamecenter.model.profile;

import org.ini4j.Profile;

public interface IniReader {
  void read(Profile.Section section);
}
