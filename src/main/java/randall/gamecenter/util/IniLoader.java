package randall.gamecenter.util;

import com.google.common.base.Preconditions;
import helper.Explorer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.ini4j.Ini;
import org.ini4j.Wini;
import org.springframework.core.io.Resource;

@Slf4j
public enum IniLoader {
  ;

  public static Ini load(Resource resource) {
    Preconditions.checkNotNull(resource, "resource == null");
    try {
      return new Wini(resource.getFile());
    } catch (IOException e) {
      log.error("从资源加载配置出错！", e);
      throw new RuntimeException(e);
    }
  }

  public static Ini load(Path path) {
    Preconditions.checkNotNull(path, "path == null");
    notExistsAndCreate(path);
    try {
      return new Wini(path.toFile());
    } catch (IOException e) {
      log.error("从文件加载配置出错！", e);
      throw new RuntimeException(e);
    }
  }

  private static void notExistsAndCreate(Path path) {
    if (Files.notExists(path)) {
      Explorer.mkdir(path.getParent());
      Explorer.create(path);
    }
  }
}
