package randall.gamecenter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import randall.gamecenter.util.Compressor;
import randall.gamecenter.util.Files;

/**
 * @author mrzhqiang
 */
public final class DataBackup {
  private static final Logger LOGGER = LoggerFactory.getLogger("randall");

  public static class BackupObject implements Runnable {
    public Integer index = -1;
    public String sourceDir = "";
    public String destinationDir = "";
    public byte backupMode = 0;
    public boolean backupEnabled = true;
    public int hours = 0;
    public int minutes = 0;
    public boolean compressEnabled = true;
    public int status = 0;
    public int backupCount = 0;
    public int errorCount = 0;
    public long backupTick = System.currentTimeMillis();
    public long backupTime = 0;
    public long startBackupTick = System.currentTimeMillis();
    public LocalDateTime today = LocalDateTime.now();
    public List<String> backupFileList = Lists.newArrayList();
    public boolean stopSearchEnabled = false;

    private boolean isToday() {
      return LocalDate.now().equals(today.toLocalDate());
    }

    private boolean canBackup() {
      return LocalTime.now().equals(LocalTime.of(hours, minutes));
    }

    private String formatDate() {
      return DateTimeFormatter.ofPattern("yyyy-MM-dd.HH-mm").format(LocalDateTime.now());
    }

    private String lastDirName() {
      String[] split = sourceDir.split(File.separator);
      if (split.length > 0) {
        return split[split.length - 1];
      }
      return formatDate();
    }

    private String getDirName(String filename) {
      String parent = new File(filename).getParent();
      return parent == null ? "" : parent;
    }

    private void searchFile(String directory) {
      Preconditions.checkNotNull(directory, "directory == null");
      if (!directory.endsWith(File.separator)) {
        directory += File.separator;
      }
      File dirFile = new File(directory);
      for (File file : Objects.requireNonNull(dirFile.listFiles())) {
        if (file.isDirectory()) {
          searchFile(file.getPath());
        } else {
          backupFileList.add(file.getPath());
        }
        if (stopSearchEnabled) {
          return;
        }
      }
    }

    @Override public void run() {
      try {
        if (!backupEnabled) {
          return;
        }
        // todo need refactor
        switch (backupMode) {
          case 0:
            switch (status) {
              case 0:
                if (canBackup()) {
                  status = 1;
                  if (!sourceDir.endsWith(File.separator)) {
                    sourceDir += File.separator;
                  }
                  if (!destinationDir.endsWith(File.separator)) {
                    destinationDir += File.separator;
                  }
                  backupFileList.clear();

                  if (compressEnabled) {
                    if (errorCount < 2) {
                      String destination = destinationDir + formatDate() + File.separator;
                      Files.mkdir(new File(destination));
                      destination = destination + lastDirName() + ".zip";
                      try {
                        Compressor.zipCompress(sourceDir, destination);
                      } catch (Exception e) {
                        LOGGER.error("ZIP 压缩 " + sourceDir + " 目录出错！", e);
                        errorCount++;
                      }
                    }
                  } else {
                    searchFile(sourceDir);
                    String destination = destinationDir + formatDate() + File.separator;
                    for (String filename : backupFileList) {
                      if (stopSearchEnabled) {
                        return;
                      }
                      String newFilename = filename.replace(sourceDir, destination);
                      String dirName = getDirName(filename);
                      Files.mkdir(new File(dirName));
                      try {
                        java.nio.file.Files.copy(Paths.get(filename), Paths.get(newFilename));
                      } catch (IOException ignore) {
                      }
                    }
                  }
                  status = 2;
                }
                break;
              case 2:
                if (!isToday()) {
                  status = 0;
                }
                errorCount = 0;
                break;
            }
            break;
          case 1:

            break;
        }
      } catch (Exception ignore) {
      }
    }
  }
}
