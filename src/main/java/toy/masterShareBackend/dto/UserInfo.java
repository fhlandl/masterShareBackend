package toy.masterShareBackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserInfo {

  @Schema(example = "1111")
  private Long userId;

  @Schema(example = "test1234")
  private String username;

  @Schema(example = "test1234@gmail.com")
  private String email;

  @Schema(example = "트리티티")
  private String nickname;

}
