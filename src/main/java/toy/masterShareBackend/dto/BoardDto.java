package toy.masterShareBackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardDto {

    @Schema(example = "1111")
    private Long boardId;

    @Schema(example = "10")
    private Integer maxSize;
    // ToDo: 기타 설정값들 추가
}
