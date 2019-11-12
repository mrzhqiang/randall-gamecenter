package randall.gamecenter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import randall.gamecenter.util.Compressor;
import randall.gamecenter.util.Files;

/**
 * 备份管理器。
 *
 * @author mrzhqiang
 */
public final class BackupManager {
  private static final Logger LOGGER = LoggerFactory.getLogger("randall");

  public final ObservableList<BackupObject> backupList = FXCollections.observableArrayList();
  public Timer startTimer = new Timer();
  private Boolean started = false;
  private final Object lock = new Object();

  public void start() {
    clearStack();
    if (startTimer == null) {
      startTimer = new Timer();
    }
    startTimer.schedule(new TimerTask() {
      @Override public void run() {
        BackupManager.this.run();
        if (!started) {
          started = true;
        }
      }
    }, 1000, 100);
  }

  public void pause() {
    if (started) {
      stop();
    } else {
      start();
    }
  }

  public void stop() {
    if (startTimer != null) {
      startTimer.cancel();
    }
    startTimer = null;
    started = false;
  }

  public void addToList(BackupObject object) {
    synchronized (lock) {
      backupList.add(object);
    }
  }

  public void clearObject() {
    synchronized (lock) {
      backupList.clear();
    }
  }

  public void clearStack() {
    synchronized (lock) {
      backupList.forEach(object -> object.startBackupTick = System.currentTimeMillis());
    }
  }

  public Optional<BackupObject> findObject(String source) {
    synchronized (lock) {
      return backupList.stream().filter(object -> object.sourceDir.get().equals(source)).findAny();
    }
  }

  public boolean deleteObject(String source) {
    synchronized (lock) {
      return backupList.stream()
          .filter(object -> object.sourceDir.get().equals(source))
          .findAny()
          .map(backupList::remove)
          .orElse(false);
    }
  }

  public void run() {
    synchronized (lock) {
      backupList.forEach(BackupObject::run);
    }
  }

  public static class BackupObject implements Runnable {
    public Integer index = -1;
    public StringProperty sourceDir = new SimpleStringProperty("", "数据目录");
    public StringProperty destinationDir = new SimpleStringProperty("", "备份目录");
    public int backupMode = 0;
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
      return new File(sourceDir.get()).getName();
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
                  if (!sourceDir.get().endsWith(File.separator)) {
                    sourceDir.set(sourceDir.get() + File.separator);
                  }
                  if (!destinationDir.get().endsWith(File.separator)) {
                    destinationDir.set(destinationDir.get() + File.separator);
                  }
                  backupFileList.clear();

                  if (compressEnabled) {
                    if (errorCount < 2) {
                      String destination = destinationDir.get();
                      Files.mkdir(new File(destination));
                      destination = destination + lastDirName() + "-" + formatDate() + ".zip";
                      try {
                        Compressor.zipCompress(sourceDir.get(), destination);
                      } catch (Exception e) {
                        LOGGER.error("ZIP 压缩 " + sourceDir.get() + " 目录出错！", e);
                        errorCount++;
                      }
                    }
                    status = 2;
                    return;
                  }

                  searchFile(sourceDir.get());
                  String destination = destinationDir.get() + formatDate() + File.separator;
                  for (String filename : backupFileList) {
                    if (stopSearchEnabled) {
                      return;
                    }
                    String newFilename = filename.replace(sourceDir.get(), destination);
                    //String dirName = getDirName(newFilename);
                    //Files.mkdir(new File(dirName));
                    try {
                      java.nio.file.Files.copy(Paths.get(filename), Paths.get(newFilename),
                          StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                    } catch (IOException ignore) {
                    }
                  }
                }
                status = 2;
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
            switch (status) {
              case 0:
                long duration = TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes);
                if ((System.currentTimeMillis() - startBackupTick) > duration) {
                  startBackupTick = System.currentTimeMillis();
                  status = 1;
                  if (!sourceDir.get().endsWith(File.separator)) {
                    sourceDir.set(sourceDir.get() + File.separator);
                  }
                  if (!destinationDir.get().endsWith(File.separator)) {
                    destinationDir.set(destinationDir.get() + File.separator);
                  }
                  backupFileList.clear();
                  if (compressEnabled) {
                    if (errorCount < 2) {
                      String destination = destinationDir.get();
                      Files.mkdir(new File(destination));
                      destination = destination + lastDirName() + "-" + formatDate() + ".zip";
                      try {
                        Compressor.zipCompress(sourceDir.get(), destination);
                      } catch (Exception e) {
                        LOGGER.error("ZIP 压缩 " + sourceDir.get() + " 目录出错！", e);
                        errorCount++;
                      }
                    }
                    status = 2;
                    return;
                  }

                  searchFile(sourceDir.get());
                  String destination = destinationDir.get() + formatDate() + File.separator;
                  for (String filename : backupFileList) {
                    if (stopSearchEnabled) {
                      return;
                    }
                    String newFilename = filename.replace(sourceDir.get(), destination);
                    //String dirName = getDirName(newFilename);
                    //Files.mkdir(new File(dirName));
                    try {
                      java.nio.file.Files.copy(Paths.get(filename), Paths.get(newFilename),
                          StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                    } catch (IOException ignore) {
                    }
                  }
                }
                status = 2;
                break;
              case 2:
                status = 0;
                errorCount = 0;
                break;
            }
            break;
        }
      } catch (Exception ignore) {
      }
    }
  }
}
