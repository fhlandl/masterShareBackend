package toy.masterShareBackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private boolean success;
    private String message;
    private UserInfo userInfo;

    public LoginResponse(boolean success, String message, UserInfo userInfo) {
        this.success = success;
        this.message = message;
        this.userInfo = userInfo;
    }

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
