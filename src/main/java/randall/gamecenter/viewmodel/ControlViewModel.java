package randall.gamecenter.viewmodel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import randall.gamecenter.model.ConfigModel;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Getter
public class ControlViewModel {
  private final ConfigModel configModel;
}
