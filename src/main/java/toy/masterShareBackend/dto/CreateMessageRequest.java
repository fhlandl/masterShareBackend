package toy.masterShareBackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMessageRequest {

    private String sender;

    private String title;

    private String content;
}
