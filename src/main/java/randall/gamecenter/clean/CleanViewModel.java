package randall.gamecenter.clean;

import helper.Explorer;
import helper.javafx.model.Status;
import helper.javafx.ui.Dialogs;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ini4j.Ini;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import randall.common.ServerPath;
import randall.gamecenter.config.model.Config;
import randall.gamecenter.util.IniLoader;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class CleanViewModel {
  private final Status startStatus = new Status();
  private final BooleanProperty databaseData = new SimpleBooleanProperty();
  private final BooleanProperty accountData = new SimpleBooleanProperty();
  private final BooleanProperty guildData = new SimpleBooleanProperty();
  private final BooleanProperty shabakeData = new SimpleBooleanProperty();
  private final BooleanProperty globalVariate = new SimpleBooleanProperty();
  private final BooleanProperty itemIdCount = new SimpleBooleanProperty();
  private final BooleanProperty relationData = new SimpleBooleanProperty();
  private final BooleanProperty makeData = new SimpleBooleanProperty();
  private final BooleanProperty emailData = new SimpleBooleanProperty();
  private final BooleanProperty accountLogger = new SimpleBooleanProperty();
  private final BooleanProperty coreLogger = new SimpleBooleanProperty();
  private final BooleanProperty gameLogger = new SimpleBooleanProperty();

  private final CompositeDisposable disposable = new CompositeDisposable();

  private final Config config;

  @PreDestroy void onDestroy() {
    disposable.clear();
  }

  public void bindStart(Button button) {
    disposable.add(startStatus.observe().subscribe(button::setDisable));
  }

  public void bindDatabase(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(box.selectedProperty())
        .subscribe(databaseData::setValue));
  }

  public void bindAccount(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(box.selectedProperty())
        .subscribe(accountData::setValue));
  }

  public void bindGuild(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(box.selectedProperty())
        .subscribe(guildData::setValue));
  }

  public void bindShabake(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(box.selectedProperty())
        .subscribe(shabakeData::setValue));
  }

  public void bindGlobal(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(box.selectedProperty())
        .subscribe(globalVariate::setValue));
  }

  public void bindItem(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(box.selectedProperty())
        .subscribe(itemIdCount::setValue));
  }

  public void bindRelation(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(box.selectedProperty())
        .subscribe(relationData::setValue));
  }

  public void bindMake(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(box.selectedProperty())
        .subscribe(makeData::setValue));
  }

  public void bindEmail(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(box.selectedProperty())
        .subscribe(emailData::setValue));
  }

  public void bindAccountLogger(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(box.selectedProperty())
        .subscribe(accountLogger::setValue));
  }

  public void bindCoreLogger(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(box.selectedProperty())
        .subscribe(coreLogger::setValue));
  }

  public void bindGameLogger(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(box.selectedProperty())
        .subscribe(gameLogger::setValue));
  }

  /**
   * todo refactor here
   */
  public void clean() {
    startStatus.running();
    Path root = Paths.get(config.home.getPath());
    if (databaseData.get()) {
      Path dataDir = root.resolve(config.database.getPath())
          .resolve(ServerPath.DATABASE)
          .resolve(ServerPath.DATA_DIR);
      Explorer.delete(dataDir.resolve("Hum.DB"));
      Explorer.delete(dataDir.resolve("Mir.DB"));
      Explorer.delete(dataDir.resolve("Mir.DB.idx"));
    }
    if (accountData.get()) {
      Path dataDir = root.resolve(config.account.getPath())
          .resolve(ServerPath.ACCOUNT)
          .resolve(ServerPath.DATA_DIR);
      Explorer.delete(dataDir.resolve("Id.DB"));
      Explorer.delete(dataDir.resolve("Id.DB.idx"));
    }
    if (guildData.get()) {
      Path coreDir = root.resolve(config.core.getPath()).resolve(ServerPath.CORE);
      Explorer.deleteAll(coreDir.resolve(ServerPath.Core.GUILD_DIR_VALUE));
      Path guildListPath = coreDir.resolve(ServerPath.Core.GUILD_FILE_VALUE);
      if (Files.exists(guildListPath)) {
        Explorer.write(guildListPath, "");
      }
    }
    if (shabakeData.get()) {
      Path coreDir = root.resolve(config.core.getPath()).resolve(ServerPath.CORE);
      List<String> castleList = Explorer.lines(coreDir.resolve("Castle\\List.txt"));
      castleList.stream()
          .map(s -> String.format("\\Castle\\%s\\AttackSabukWall.txt", s))
          .map(coreDir::resolve)
          .forEach(file -> Explorer.write(file, ""));
      castleList.stream()
          .map(s -> String.format("\\Castle\\%s\\SabukW.txt", s))
          .map(coreDir::resolve)
          .filter(path -> Files.exists(path))
          .forEach(file -> {
            try {
              Ini ini = IniLoader.load(file);
              ini.put("Setup", "OwnGuild", "");
              ini.put("Setup", "ChangeDate", "");
              ini.put("Setup", "WarDate", "");
              ini.put("Setup", "IncomeToday", "");
              ini.put("Setup", "TotalGold", "");
              ini.put("Setup", "TodayIncome", "");

              ini.put("Defense", "MainDoorHP", "10000");
              ini.put("Defense", "LeftWallHP", "5000");
              ini.put("Defense", "CenterWallHP", "5000");
              ini.put("Defense", "RightWallHP", "5000");
              ini.put("Defense", "Archer_1_HP", "2000");
              ini.put("Defense", "Archer_2_HP", "2000");
              ini.put("Defense", "Archer_3_HP", "2000");
              ini.put("Defense", "Archer_4_HP", "2000");
              ini.put("Defense", "Archer_5_HP", "2000");
              ini.put("Defense", "Archer_6_HP", "2000");
              ini.put("Defense", "Archer_7_HP", "2000");
              ini.put("Defense", "Archer_8_HP", "2000");
              ini.put("Defense", "Archer_9_HP", "2000");
              ini.put("Defense", "Archer_10_HP", "2000");
              ini.put("Defense", "Archer_11_HP", "2000");
              ini.put("Defense", "Archer_12_HP", "2000");
              ini.store();
            } catch (IOException e) {
              log.error("清理沙巴克数据的配置文件出错！", e);
            }
          });
    }
    if (globalVariate.get()) {
      Path coreDir = root.resolve(config.core.getPath()).resolve(ServerPath.CORE);
      Explorer.delete(coreDir.resolve("\\Global.ini"));
    }
    if (itemIdCount.get()) {
      Path coreDir = root.resolve(config.core.getPath()).resolve(ServerPath.CORE);
      Path mir2SetupFile = coreDir.resolve("\\!Setup.txt");
      if (Files.exists(mir2SetupFile)) {
        try {
          Ini ini = IniLoader.load(mir2SetupFile);
          ini.put("Setup", "ItemNumber", 10000);
          ini.put("Setup", "ItemNumberEx", 2000000000);
          ini.store();
        } catch (IOException e) {
          log.error("复位物品 ID 计数出错！", e);
        }
      }
    }
    if (relationData.get()) {
      Path coreDir = root.resolve(config.core.getPath()).resolve(ServerPath.CORE);
      Explorer.write(coreDir.resolve("\\Envir\\UnForceMaster.txt"), "");
      Explorer.write(coreDir.resolve("\\Envir\\UnFriend.txt"), "");
      Explorer.write(coreDir.resolve("\\Envir\\UnMarry.txt"), "");
      Explorer.write(coreDir.resolve("\\Envir\\UnMaster.txt"), "");
    }
    if (makeData.get()) {
      Path coreDir = root.resolve(config.core.getPath()).resolve(ServerPath.CORE);
      Explorer.deleteAll(coreDir.resolve("\\Envir\\Market_Upg\\"));
    }
    if (emailData.get()) {
      Path coreDir = root.resolve(config.core.getPath()).resolve(ServerPath.CORE);
      Explorer.delete(coreDir.resolve("\\EMail\\EMailData.dat"));
      Explorer.delete(coreDir.resolve("\\EMail\\EMailName.txt"));
    }
    if (accountLogger.get()) {
      Path accountDir = root.resolve(config.account.getPath()).resolve(ServerPath.ACCOUNT);
      Explorer.deleteAll(accountDir.resolve("\\ChrLog\\"));
    }
    if (coreLogger.get()) {
      Path coreDir = root.resolve(config.core.getPath()).resolve(ServerPath.CORE);
      Explorer.deleteAll(coreDir.resolve("\\Log\\"));
      Explorer.deleteAll(coreDir.resolve("\\ConLog\\"));
    }
    if (gameLogger.get()) {
      Path loggerDir = root.resolve(config.logger.getPath()).resolve(ServerPath.LOGGER);
      Explorer.deleteAll(loggerDir.resolve("\\BaseDir\\"));
    }
    startStatus.finished();
    Dialogs.info("全部清理完成！").show();
  }
}
