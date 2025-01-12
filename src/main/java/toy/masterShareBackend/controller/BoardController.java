package toy.masterShareBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toy.masterShareBackend.dto.*;
import toy.masterShareBackend.service.BoardService;

@Tag(name = "Board API", description = "게시판 및 메시지에 대한 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/boards/v1")
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "게시판 가져오기", description = "userId를 가진 회원의 게시판 정보를 가져옴")
    @ApiResponse(responseCode = "200")
    @GetMapping("/{userId}/board")
    public ResponseEntity<ResponseWrapper<BoardResponse>> board(
            @Parameter(example = "9fcU9rdGc-wDQ74GiOnc")
            @PathVariable String userId) {

        BoardResponse boardResponse = boardService.findBoard(userId);

        return ResponseWrapper.success(boardResponse);
    }

    @Operation(summary = "메시지 목록 가져오기", description = "userId를 가진 회원의 메시지 목록을 가져옴(내용은 포함하지 않음)")
    @ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = ResponseWrapper.class),
            examples = @ExampleObject(value = "{\"success\":true,\"data\":{\"dataList\":[{\"messageId\":\"MH5tjB2yoshlnMDDbPdM\",\"sender\":\"트리티티\",\"title\":\"메시지제목\",\"content\":\"null\",\"opened\":false,\"createdAt\":\"2024.12.1921:45\"}],\"pageRequest\":{\"page\":1,\"size\":10},\"hasPrev\":true,\"hasNext\":true,\"totalDataCount\":0,\"currentPage\":0,\"prevPage\":0,\"nextPage\":0,\"lastPage\":0},\"error\":null}")
    ))
    @GetMapping("/{userId}/board/messages")
    public ResponseEntity<ResponseWrapper<PageResponseDto<MessageDto>>> messages(
            @Parameter(example = "9fcU9rdGc-wDQ74GiOnc")
            @PathVariable String userId,
            @ModelAttribute PageRequestDto pageRequestDto) {

        PageResponseDto<MessageDto> messageList = boardService.findMessageList(userId, pageRequestDto);

        return ResponseWrapper.success(messageList);
    }

    @Operation(summary = "메시지 하나 가져오기", description = "messageId를 가진 메시지를 가져옴(open 상태가 아닌 경우 오류 발생)")
    @ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = ResponseWrapper.class),
            examples = @ExampleObject(value = "{\"success\":true,\"data\":{\"messageId\": \"MH5tjB2yoshlnMDDbPdM\",\"sender\": \"트리티티\",\"title\": \"메시지 제목\",\"content\": \"메시지 내용\",\"opened\": true,\"createdAt\": \"2024.12.19 21:45\"},\"error\":null}")
    ))
    @GetMapping("/message/{messageId}")
    public ResponseEntity<ResponseWrapper<MessageDto>> getMessage(
            @Parameter(example = "MH5tjB2yoshlnMDDbPdM")
            @PathVariable String messageId) {

        try {
            MessageDto messageDto = boardService.readMessage(messageId);
            return ResponseWrapper.success(messageDto);

        } catch (RuntimeException e) {

            return ResponseWrapper.fail(1234, e.getMessage());
        }
    }

    @Operation(summary = "메시지 열기", description = "messageId를 가진 메시지의 open 상태를 true로 변경", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = ResponseWrapper.class),
            examples = @ExampleObject(value = "{\"success\":true,\"data\":{\"messageId\": \"MH5tjB2yoshlnMDDbPdM\",\"sender\": \"트리티티\",\"title\": \"메시지 제목\",\"content\": \"메시지 내용\",\"opened\": true,\"createdAt\": \"2024.12.19 21:45\"},\"error\":null}")
    ))
    @ApiResponse(responseCode = "403", content = @Content(
            schema = @Schema(implementation = ResponseWrapper.class),
            examples = @ExampleObject(value = "{\"success\":false,\"data\":null,\"error\":{\"code\":4321,\"message\":\"Message access denied\"}}")
    ))
    @PatchMapping("/message/open/{messageId}")
    public ResponseEntity<ResponseWrapper<MessageDto>> openMessage(
            @Parameter(example = "MH5tjB2yoshlnMDDbPdM")
            @PathVariable String messageId) {

        MessageDto messageDto = boardService.openMessage(messageId);

        return ResponseWrapper.success(messageDto);
    }

    @Operation(summary = "메시지 생성", description = "userId를 가진 회원의 게시판에 메시지를 생성함")
    @ApiResponse(responseCode = "200")
    @PostMapping("/{userId}/board/message/new")
    public ResponseEntity<ResponseWrapper<MessageDto>> createMessage(
            @Parameter(description = "게시판 소유자의 userId", example = "9fcU9rdGc-wDQ74GiOnc")
            @PathVariable String userId,
            @RequestBody CreateMessageRequest dto) {

        MessageDto messageDto = boardService.createMessage(userId, dto.getSender(), dto.getTitle(), dto.getContent());

        return ResponseWrapper.success(messageDto);
    }

    @Operation(summary = "메시지 삭제", description = "messageId를 가진 메시지의 deleted 상태를 true로 변경", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = ResponseWrapper.class),
            examples= @ExampleObject(value = "{\"success\":true,\"data\":{\"messageId\":\"MH5tjB2yoshlnMDDbPdM\"},\"error\":null}")
    ))
    @ApiResponse(responseCode = "403", content = @Content(
            schema = @Schema(implementation = ResponseWrapper.class),
            examples = @ExampleObject(value = "{\"success\":false,\"data\":null,\"error\":{\"code\":4321,\"message\":\"Message access denied\"}}")
    ))
    @PatchMapping("/message/delete/{messageId}")
    public ResponseEntity<ResponseWrapper<MessageDto>> deleteMessage(
            @Parameter(example = "MH5tjB2yoshlnMDDbPdM")
            @PathVariable String messageId) {

        boardService.deleteMessage(messageId);
        return ResponseWrapper.success(MessageDto.builder()
                .messageId(messageId)
                .build());
    }

}
