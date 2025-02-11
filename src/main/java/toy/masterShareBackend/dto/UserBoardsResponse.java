package toy.masterShareBackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserBoardsResponse {

    @Schema(example = "test1234")
    private String username;

    @Schema(example = "트리티티")
    private String nickname;

    private List<BoardDto> boards;

    @Builder
    public UserBoardsResponse(String username, String nickname, List<BoardDto> boards) {
        this.username = username;
        this.nickname = nickname;
        this.boards = boards;
    }
}
