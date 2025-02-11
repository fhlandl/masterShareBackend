package toy.masterShareBackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageUpdateDto {

    @Schema(example = "메시지 제목")
    private String title;

    @Schema(example = "null", nullable = true)
    private String content;

    @Schema(example = "false")
    private Boolean opened;

    @Schema(example = "false")
    private Boolean deleted;
}
