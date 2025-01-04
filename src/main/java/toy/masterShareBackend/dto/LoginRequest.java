package toy.masterShareBackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @Schema(example = "test1234")
    private String username;

    @Schema(example = "q1w2e3r4")
    private String password;
}
