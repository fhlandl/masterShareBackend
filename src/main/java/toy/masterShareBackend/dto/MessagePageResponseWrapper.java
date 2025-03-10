package toy.masterShareBackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MessagePageResponseWrapper {

    @Schema(example = "true")
    private boolean success;

    private PageResponseDto<MessageDto> data;

    @Schema(example = "null")
    private ErrorInfo error;

    public static ResponseEntity<MessagePageResponseWrapper> success(PageResponseDto<MessageDto> data) {
        return ResponseEntity.ok(MessagePageResponseWrapper.builder()
                .success(true)
                .data(data)
                .error(null)
                .build());
    }

    public static ResponseEntity<MessagePageResponseWrapper> failAuth(int code, String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MessagePageResponseWrapper.builder()
                .success(false)
                .data(null)
                .error(new ErrorInfo(code, message))
                .build());
    }
}
