package randall.gamecenter.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import org.junit.Test;

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
}