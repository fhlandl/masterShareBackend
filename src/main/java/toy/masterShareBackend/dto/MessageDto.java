package toy.masterShareBackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageDto {

    @Schema(example = "1111")
    private Long messageId;

    @Schema(example = "트리티티")
    private String sender;

    @Schema(example = "메시지 제목")
    private String title;

    @Schema(example = "null", nullable = true)
    private String content;

    @Schema(example = "false")
    private boolean opened;

    @Schema(example = "false")
    private boolean deleted;

    @Schema(example = "2024.12.19 21:45")
    private String createdAt;
}
