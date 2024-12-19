package toy.masterShareBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private boolean success;
    private String message;
    private UserInfo userInfo;

    @Builder
    public LoginResponse(boolean success, String message, String userId, String username, String email, String nickname) {
        this.success = success;
        this.message = message;
        this.userInfo = new UserInfo(userId, username, email, nickname);
    }

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    @AllArgsConstructor
    @Getter
    static class UserInfo {
        String userId;
        String username;
        String email;
        String nickname;
    }
}
