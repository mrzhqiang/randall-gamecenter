package randall.gamecenter.util;

import io.reactivex.observers.DefaultObserver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import org.junit.Test;
import randall.gamecenter.Share;

import static org.junit.Assert.assertTrue;

/**
 * @author mrzhqiang
 */
public class ProgramsTest {

  @Test
  public void executePing() throws IOException {
    Process process = Runtime.getRuntime().exec("ping 127.0.0.1");
    assertTrue(process.isAlive());
    InputStream inputStream = process.getInputStream();
    // 如果不是 Windows 系统，可能 GBK 编码无法通过测试，请改为 UTF-8 试试
    Charset charset = Charset.forName("GBK");
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));
    String line;
    while ((line = reader.readLine()) != null) {
      System.out.println(line);
    }
    //        int code = process.waitFor();
    //        System.out.println("code: " + code);
  }

  @Test
  public void start() throws Exception {
    Share.Program program = new Share.Program();
    program.mainFormX = 100;
    program.mainFormY = 100;
    program.directory = "D:\\randall-m2\\MirServer\\DBServer";
    program.programFile = "DBServer.exe";
    program.start()
        .subscribe(new DefaultObserver<String>() {
          @Override public void onNext(String s) {
            System.out.println(s);
          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
          }

          @Override public void onComplete() {
            System.out.println("Finish.");
          }
        });
    Thread.sleep(10000);
  }
}