package toy.masterShareBackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserJoinResponse {
    private boolean success;
    private String message;
    private UserInfo userInfo;

    public UserJoinResponse(boolean success, String message, UserInfo userInfo) {
        this.success = success;
        this.message = message;
        this.userInfo = userInfo;
    }

    public UserJoinResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
