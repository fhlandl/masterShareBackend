package toy.masterShareBackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {

    @Schema(example = "test1234@gmail.com")
    private String email;

    @Schema(example = "트리티티")
    private String nickname;
}
