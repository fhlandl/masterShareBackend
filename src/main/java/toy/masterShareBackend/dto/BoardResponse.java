package toy.masterShareBackend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardResponse {

    private String username;
    private String nickname;
    private Integer maxSize;
    // ToDo: 기타 설정값들 추가

    @Builder
    public BoardResponse(String username, String nickname, int maxSize) {
        this.username = username;
        this.nickname = nickname;
        this.maxSize = maxSize;
    }
}
