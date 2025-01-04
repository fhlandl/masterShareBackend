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
    @Schema(example = "true")
    private boolean success;

    @Schema(example = "JOIN_SUCCESS")
    private String message;

    private UserInfo userInfo;

    @Schema(example = "eyJ0eXAiOiJKV1QiL...")
    private String accessToken;

    @Schema(example = "eyJ0eXAiOiJKV1QiL...")
    private String refreshToken;

    @Builder
    public UserJoinResponse(boolean success, String message, UserInfo userInfo, String accessToken, String refreshToken) {
        this.success = success;
        this.message = message;
        this.userInfo = userInfo;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
