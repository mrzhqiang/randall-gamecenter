package randall.gamecenter.model.config;

import org.ini4j.Ini;

public interface IniAdapter {
  void read(Ini ini);

  void write(Ini ini);
}
