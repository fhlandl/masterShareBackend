package toy.masterShareBackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    private boolean success;
    private String message;
    private UserInfo userInfo;
    private String accessToken;
    private String refreshToken;

    @Builder
    public LoginResponse(boolean success, String message, UserInfo userInfo, String accessToken, String refreshToken) {
        this.success = success;
        this.message = message;
        this.userInfo = userInfo;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
