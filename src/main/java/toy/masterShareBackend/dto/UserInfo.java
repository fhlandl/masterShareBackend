package toy.masterShareBackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserInfo {

  @Schema(example = "9fcU9rdGc-wDQ74GiOnc")
  private String userKey;

  @Schema(example = "test1234")
  private String username;

  @Schema(example = "test1234@gmail.com")
  private String email;

  @Schema(example = "트리티티")
  private String nickname;

}
