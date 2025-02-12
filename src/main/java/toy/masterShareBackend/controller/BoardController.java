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
@RequestMapping("/api/v1")
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "게시판 목록 가져오기", description = "userKey를 가진 회원의 게시판 정보를 가져옴")
    @ApiResponse(responseCode = "200")
    @GetMapping("/users/{userKey}/boards")
    public ResponseEntity<ResponseWrapper<UserBoardsResponse>> board(
            @Parameter(example = "1111")
            @PathVariable String userKey) {

        UserBoardsResponse userBoardsResponse = boardService.findAllBoards(userKey);

        return ResponseWrapper.success(userBoardsResponse);
    }

    @Operation(summary = "메시지 목록 가져오기", description = "boardId에 해당하는 게시판의 메시지 목록을 가져옴")
    @ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = ResponseWrapper.class),
            examples = @ExampleObject(value = "{\"success\":true,\"data\":{\"dataList\":[{\"messageId\":1111,\"sender\":\"트리티티\",\"title\":\"메시지제목\",\"content\":\"null\",\"opened\":false,\"createdAt\":\"2024.12.1921:45\"}],\"pageRequest\":{\"page\":1,\"size\":10},\"hasPrev\":true,\"hasNext\":true,\"totalDataCount\":0,\"currentPage\":0,\"prevPage\":0,\"nextPage\":0,\"lastPage\":0},\"error\":null}")
    ))
    @GetMapping("/boards/{boardId}/messages")
    public ResponseEntity<ResponseWrapper<PageResponseDto<MessageDto>>> messages(
            @Parameter(example = "1111") @PathVariable Long boardId,
            @ModelAttribute PageRequestDto pageRequestDto,
            @Parameter(description = "open 여부", example = "true") @RequestParam(required = false) Boolean opened,
            @Parameter(description = "delete 여부", example = "false") @RequestParam(required = false, defaultValue = "false") Boolean deleted) {

        MessageSearchCondition condition = new MessageSearchCondition(opened, deleted);
        PageResponseDto<MessageDto> messageList = boardService.findMessageList(boardId, condition, pageRequestDto);

        return ResponseWrapper.success(messageList);
    }

    @Operation(summary = "메시지 하나 가져오기", description = "messageId를 가진 메시지를 가져옴(open 상태가 아닌 경우 오류 발생)")
    @ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = ResponseWrapper.class),
            examples = @ExampleObject(value = "{\"success\":true,\"data\":{\"messageId\": 1111,\"sender\": \"트리티티\",\"title\": \"메시지 제목\",\"content\": \"메시지 내용\",\"opened\": true,\"createdAt\": \"2024.12.19 21:45\"},\"error\":null}")
    ))
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<ResponseWrapper<MessageDto>> getMessage(
            @Parameter(example = "1111") @PathVariable Long messageId) {

        try {
            MessageDto messageDto = boardService.readMessage(messageId);
            return ResponseWrapper.success(messageDto);

        } catch (RuntimeException e) {

            return ResponseWrapper.fail(1234, e.getMessage());
        }
    }

    @Operation(summary = "메시지 업데이트", description = "messageId를 가진 메시지를 업데이트", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = ResponseWrapper.class),
            examples = @ExampleObject(value = "{\"success\":true,\"data\":{\"messageId\": 1111,\"sender\": \"트리티티\",\"title\": \"메시지 제목\",\"content\": \"메시지 내용\",\"opened\": true,\"createdAt\": \"2024.12.19 21:45\"},\"error\":null}")
    ))
    @ApiResponse(responseCode = "403", content = @Content(
            schema = @Schema(implementation = ResponseWrapper.class),
            examples = @ExampleObject(value = "{\"success\":false,\"data\":null,\"error\":{\"code\":4321,\"message\":\"Message access denied\"}}")
    ))
    @PutMapping("/messages/{messageId}")
    public ResponseEntity<ResponseWrapper<MessageDto>> updateMessage(
            @Parameter(example = "1111")
            @PathVariable Long messageId,
            @RequestBody MessageUpdateDto dto) {

        MessageDto messageDto = boardService.updateMessage(messageId, dto);

        return ResponseWrapper.success(messageDto);
    }

    @Operation(summary = "메시지 생성", description = "boardId에 해당하는 게시판에 메시지를 생성함")
    @ApiResponse(responseCode = "200")
    @PostMapping("/boards/{boardId}/messages")
    public ResponseEntity<ResponseWrapper<MessageDto>> createMessage(
            @Parameter(description = "게시판의 boardId", example = "1111")
            @PathVariable Long boardId,
            @RequestBody CreateMessageRequest dto) {

        MessageDto messageDto = boardService.createMessage(boardId, dto.getSender(), dto.getTitle(), dto.getContent());

        return ResponseWrapper.success(messageDto);
    }

}
