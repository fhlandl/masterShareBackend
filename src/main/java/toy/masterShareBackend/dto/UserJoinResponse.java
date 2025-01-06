package toy.masterShareBackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserJoinResponse {
    private UserInfo userInfo;

    @Schema(example = "eyJ0eXAiOiJKV1QiL...")
    private String accessToken;

    @Schema(example = "eyJ0eXAiOiJKV1QiL...")
    private String refreshToken;

    @Builder
    public UserJoinResponse(UserInfo userInfo, String accessToken, String refreshToken) {
        this.userInfo = userInfo;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
