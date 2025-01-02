package toy.masterShareBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserTokenInfo {
    private UserInfo userInfo;
    private String accessToken;
    private String refreshToken;
}
