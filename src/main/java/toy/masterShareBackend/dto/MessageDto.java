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

    @Schema(example = "MH5tjB2yoshlnMDDbPdM")
    private String messageId;

    @Schema(example = "트리티티")
    private String sender;

    @Schema(example = "메시지 제목")
    private String title;

    @Schema(example = "null", nullable = true)
    private String content;

    @Schema(example = "false")
    private Boolean opened;

    @Schema(example = "2024.12.19 21:45")
    private String createdAt;
}
