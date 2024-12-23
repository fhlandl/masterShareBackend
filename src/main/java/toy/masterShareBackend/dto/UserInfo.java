package toy.masterShareBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserInfo {

  private String userId;
  private String username;
  private String email;
  private String nickname;

}
