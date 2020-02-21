package randall.gamecenter.model.program;

import java.io.IOException;

public interface Executor {
  Process execute(String homePath) throws IOException;
}
