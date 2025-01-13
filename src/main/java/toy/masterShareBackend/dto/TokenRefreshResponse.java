package toy.masterShareBackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TokenRefreshResponse {

    @Schema(example = "eyJ0eXAiOiJKV1QiL...")
    private String accessToken;

    @Schema(example = "eyJ0eXAiOiJKV1QiL...")
    private String refreshToken;
}
