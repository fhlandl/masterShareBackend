package toy.masterShareBackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMessageRequest {

    @Schema(example = "트리티티")
    private String sender;

    @Schema(example = "메시지 제목")
    private String title;

    @Schema(example = "메시지 내용")
    private String content;
}
