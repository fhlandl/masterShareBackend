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
public class ResponseWrapper<T> {

    @Schema(example = "true")
    private boolean success;

    @Schema(oneOf = {
            UserJoinResponse.class,
            LoginResponse.class,
            MessageDto.class,
            PageResponseDto.class,
            BoardResponse.class
    })
    private T data;

    @Schema(example = "null")
    private ErrorInfo error;

    public static <T> ResponseEntity<ResponseWrapper<T>> success(T data) {
        return ResponseEntity.ok(ResponseWrapper.<T>builder()
                .success(true)
                .data(data)
                .error(null)
                .build());
    }

    public static <T> ResponseEntity<ResponseWrapper<T>> fail(int code, String message) {
        return ResponseEntity.badRequest().body(ResponseWrapper.<T>builder()
                .success(false)
                .data(null)
                .error(new ErrorInfo(code, message))
                .build());
    }

    public static <T> ResponseEntity<ResponseWrapper<T>> failAuth(int code, String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseWrapper.<T>builder()
                .success(false)
                .data(null)
                .error(new ErrorInfo(code, message))
                .build());
    }
}
