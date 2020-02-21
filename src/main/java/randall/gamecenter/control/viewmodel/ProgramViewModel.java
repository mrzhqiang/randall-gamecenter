package randall.gamecenter.control.viewmodel;

import com.google.common.collect.Lists;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.scene.control.CheckBox;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import randall.gamecenter.config.model.Config;
import randall.gamecenter.control.model.Program;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Service
public class ProgramViewModel {
  private final Program database = Program.ofName("数据库服务器");
  private final Program account = Program.ofName("账号服务器");
  private final Program core = Program.ofName("核心服务器");
  private final Program logger = Program.ofName("日志服务器");
  private final Program run = Program.ofName("运行网关");
  private final Program role = Program.ofName("角色网关");
  private final Program login = Program.ofName("登录网关");
  private final Program top = Program.ofName("排行榜插件");

  private final CompositeDisposable disposable = new CompositeDisposable();

  private final Config config;

  @PreDestroy void onDestroy() {
    disposable.clear();
  }

  public void bindDatabase(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(config.database.enabledProperty())
        .observeOn(JavaFxScheduler.platform())
        .subscribe(box::setSelected));
  }

  public void bindAccount(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(config.account.enabledProperty())
        .observeOn(JavaFxScheduler.platform())
        .subscribe(box::setSelected));
  }

  public void bindCore(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(config.core.enabledProperty())
        .observeOn(JavaFxScheduler.platform())
        .subscribe(box::setSelected));
  }

  public void bindLogger(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(config.logger.enabledProperty())
        .observeOn(JavaFxScheduler.platform())
        .subscribe(box::setSelected));
  }

  public void bindRun(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(config.run.enabledProperty())
        .observeOn(JavaFxScheduler.platform())
        .subscribe(box::setSelected));
  }

  public void bindRole(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(config.role.enabledProperty())
        .observeOn(JavaFxScheduler.platform())
        .subscribe(box::setSelected));
  }

  public void bindLogin(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(config.login.enabledProperty())
        .observeOn(JavaFxScheduler.platform())
        .subscribe(box::setSelected));
  }

  public void bindTop(CheckBox box) {
    disposable.add(JavaFxObservable.valuesOf(config.top.enabledProperty())
        .observeOn(JavaFxScheduler.platform())
        .subscribe(box::setSelected));
  }

  public void update() {
    database.update(config.database);
    account.update(config.account);
    core.update(config.core);
    logger.update(config.logger);
    run.update(config.run);
    role.update(config.role);
    login.update(config.login);
    top.update(config.top);
  }

  public Observable<String> start() {
    return Observable.just(database, account, core, logger, run, role, login, top)
        .subscribeOn(Schedulers.io())
        .filter(Program::isEnabled)
        .filter(Program::stopped)
        .doOnNext(program -> program.execute(config.home.getPath()))
        .flatMap(Program::listener)
        .observeOn(JavaFxScheduler.platform());
  }

  public boolean allStarted() {
    return Lists.newArrayList(database, account, core, logger, run, role, login, top)
        .stream()
        .allMatch(Program::started);
  }

  public Observable<String> check() {
    return Observable.just(database, account, core, logger, run, role, login, top)
        .subscribeOn(Schedulers.io())
        .filter(Program::isEnabled)
        .filter(Program::started)
        .filter(Program::closed)
        .doOnNext(program -> program.execute(config.home.getPath()))
        .map(program -> String.format("检测到 [%s] 被关闭，已重新启动！", program.getName()))
        .observeOn(JavaFxScheduler.platform());
  }

  public Observable<String> stop() {
    return Observable.just(database, account, core, logger, run, role, login, top)
        .subscribeOn(Schedulers.io())
        .filter(Program::isEnabled)
        .filter(Program::started)
        .doOnNext(Program::stop)
        .map(program -> String.format("正在停止 [%s]..", program.getName()))
        .observeOn(JavaFxScheduler.platform());
  }

  public boolean allStopped() {
    return Lists.newArrayList(database, account, core, logger, run, role, login, top)
        .stream()
        .allMatch(Program::stopped);
  }
}
