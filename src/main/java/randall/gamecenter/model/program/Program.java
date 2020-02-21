package randall.gamecenter.model.program;

import io.reactivex.Observable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import randall.common.Signal;
import randall.gamecenter.model.config.ServerConfig;

@Slf4j
@RequiredArgsConstructor(staticName = "ofName")
public class Program implements Executor {
  private final String name;

  private int x;
  private int y;
  private boolean enabled;
  private String directory;
  private String filename;
  private ProgramState state = ProgramState.STOPPED;
  private Process process;
  private InputStream inputStream;
  private OutputStream outputStream;

  public void update(ServerConfig config) {
    x = config.getX();
    y = config.getY();
    enabled = config.isEnabled();
    directory = config.getPath();
    filename = config.getFilename();
  }

  public String getName() {
    return name;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public boolean stopped() {
    return ProgramState.STOPPED.equals(state);
  }

  public boolean started() {
    return ProgramState.STARTED.equals(state);
  }

  public boolean closed() {
    return process == null || !process.isAlive();
  }

  @Override public Process execute(String homePath) throws IOException {
    process = Runtime.getRuntime().exec(command(homePath));
    inputStream = process.getInputStream();
    outputStream = process.getOutputStream();
    return process;
  }

  private String command(String home) {
    return String.format("%s x=%d y=%d", Paths.get(home, directory, filename), x, y);
  }

  public Observable<String> listener() {
    state = ProgramState.WAITING;
    return Observable.create(emitter -> {
      emitter.onNext(String.format("正在启动 [%s]..", name));
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        String line;
        while (!ProgramState.STOPPED.equals(state) && (line = reader.readLine()) != null) {
          if (line.contains(Signal.FINISHED)) {
            emitter.onNext(String.format("[%s] 启动完毕！", name));
            break;
          }
        }
      }
      state = ProgramState.STARTED;
      emitter.onComplete();
    });
  }

  public void sendMessage(String message) {
    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
      writer.write(message);
      writer.flush();
    } catch (IOException e) {
      log.error("发送消息到子进程失败！", e);
    }
  }

  public void stop() {
    if (process != null && process.isAlive()) {
      process.destroy();
      process = null;
    }
    state = ProgramState.STOPPED;
  }
}
