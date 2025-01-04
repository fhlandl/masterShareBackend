package toy.masterShareBackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDto {
    @Schema(example = "1")
    private int page = 1;

    @Schema(example = "10")
    private int size = 10;
}
