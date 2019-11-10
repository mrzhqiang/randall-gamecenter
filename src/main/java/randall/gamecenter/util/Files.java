package randall.gamecenter.util;

import com.google.common.base.Preconditions;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件工具。
 *
 * @author mrzhqiang
 */
public enum Files {
  ;

  private static final Logger LOGGER = LoggerFactory.getLogger("randall");

  public static String filename(String name) {
    Preconditions.checkNotNull(name, "name == null");
    int i = name.lastIndexOf(File.separator);
    if (i == -1) {
      return name;
    }
    if (name.endsWith(File.separator)) {
      return "";
    }
    return name.substring(i + 1);
  }

  public static void mkdir(File directory) {
    Preconditions.checkNotNull(directory, "directory == null");
    try {
      if (directory.mkdirs()) {
        LOGGER.info("创建新目录：{}", directory.getCanonicalPath());
      }
    } catch (IOException e) {
      String message = String.format("创建失败 [%s]", directory.getPath());
      throw new RuntimeException(message, e);
    }
  }

  public static void create(File file) {
    Preconditions.checkNotNull(file, "file == null");
    if (file.exists()) {
      return;
    }
    try {
      // 必须是有文件名后缀才是文件
      if (file.createNewFile()) {
        LOGGER.info("创建新文件：{}", file.getCanonicalPath());
      }
    } catch (SecurityException e) {
      String message = String.format("无法读写 [%s]", file.getPath());
      throw new RuntimeException(message, e);
    } catch (IOException e) {
      String message = String.format("创建失败 [%s]", file.getPath());
      throw new RuntimeException(message, e);
    }
  }

  public static void delete(File file) {
    Preconditions.checkNotNull(file, "file == null");
    if (!file.exists()) {
      return;
    }
    try {
      if (file.delete()) {
        LOGGER.info("已删除：{}", file.getCanonicalPath());
      }
    } catch (IOException e) {
      String message = String.format("删除 [%s] 失败", file.getPath());
      throw new RuntimeException(message, e);
    }
  }

  public static void onceWrite(File file, String content) {
    Preconditions.checkNotNull(content, "content == null");
    create(file);
    try (FileWriter writer = new FileWriter(file)) {
      writer.write(content);
      writer.flush();
    } catch (IOException e) {
      String message = String.format("无法写入到 [%s]", file.getPath());
      throw new RuntimeException(message, e);
    }
  }

  public static void appleWrite(File file, String content) {
    Preconditions.checkNotNull(content, "content == null");
    create(file);
    try (FileWriter writer = new FileWriter(file, true)) {
      writer.write(content);
      writer.flush();
    } catch (IOException e) {
      String message = String.format("无法追加到 [%s]", file.getPath());
      throw new RuntimeException(message, e);
    }
  }
}
