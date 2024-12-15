package toy.masterShareBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MessageDto {

    private String sender;

    private String title;

    private String content;
}
