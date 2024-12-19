package toy.masterShareBackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageDto {

    private String messageId;

    private String sender;

    private String title;

    private String content;

    private boolean opened;

    private String createdAt;
}
