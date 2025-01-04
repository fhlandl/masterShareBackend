package toy.masterShareBackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardResponse {

    @Schema(example = "test1234")
    private String username;

    @Schema(example = "트리티티")
    private String nickname;

    @Schema(example = "10")
    private Integer maxSize;
    // ToDo: 기타 설정값들 추가

    @Builder
    public BoardResponse(String username, String nickname, int maxSize) {
        this.username = username;
        this.nickname = nickname;
        this.maxSize = maxSize;
    }
}
