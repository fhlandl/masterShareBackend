package toy.masterShareBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageSearchCondition {

    private Boolean opened;

    private Boolean deleted;
}
