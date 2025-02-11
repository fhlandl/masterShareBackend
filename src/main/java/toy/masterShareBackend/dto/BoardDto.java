package toy.masterShareBackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardDto {

    @Schema(example = "9fcU9rdGc-wDQ74GiOnc")
    private String boardKey;

    @Schema(example = "10")
    private Integer maxSize;
    // ToDo: 기타 설정값들 추가
}
