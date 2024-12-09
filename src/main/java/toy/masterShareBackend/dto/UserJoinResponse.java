package toy.masterShareBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserJoinResponse {
    private boolean success;
    private String message;
}
