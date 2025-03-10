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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import toy.masterShareBackend.domain.User;
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
            @Parameter(example = "9fcU9rdGc-wDQ74GiOnc")
            @PathVariable String userKey) {

        UserBoardsResponse userBoardsResponse = boardService.findAllBoards(userKey);

        return ResponseWrapper.success(userBoardsResponse);
    }

    @Operation(summary = "메시지 목록 가져오기 (member)", description = "boardId에 해당하는 게시판의 메시지 목록을 가져옴(not opened의 경우 내용 표시 안됨)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200")
    @GetMapping("/boards/{boardId}/messages/member")
    public ResponseEntity<MessagePageResponseWrapper> memberMessages(
            @Parameter(example = "1111") @PathVariable Long boardId,
            @ModelAttribute PageRequestDto pageRequestDto,
            @Parameter(description = "open 여부", example = "true") @RequestParam(required = false) Boolean opened,
            @Parameter(description = "delete 여부", example = "false") @RequestParam(required = false) Boolean deleted,
            @Parameter(description = "public 여부", example = "true") @RequestParam(required = false) Boolean isPublic) {

        MessageSearchCondition condition = new MessageSearchCondition(opened, deleted, isPublic);
        PageResponseDto<MessageDto> messageList = boardService.findMessageList(boardId, false, condition, pageRequestDto);

        return MessagePageResponseWrapper.success(messageList);
    }

    @Operation(summary = "메시지 목록 가져오기 (guest)", description = "boardId에 해당하는 게시판의 메시지 목록을 가져옴(deleted 또는 not opened 또는 private 상태의 경우 내용 표시 안됨)")
    @ApiResponse(responseCode = "200")
    @GetMapping("/boards/{boardId}/messages/guest")
    public ResponseEntity<MessagePageResponseWrapper> guestMessages(
            @Parameter(example = "1111") @PathVariable Long boardId,
            @ModelAttribute PageRequestDto pageRequestDto) {

        MessageSearchCondition condition = new MessageSearchCondition(null, false, null);
        PageResponseDto<MessageDto> messageList = boardService.findMessageList(boardId, true, condition, pageRequestDto);

        return MessagePageResponseWrapper.success(messageList);
    }

    @Operation(summary = "내가 작성한 메시지 목록 가져오기", description = "내가 작성한 메시지 목록을 가져옴(메시지 property에 상관 없이 내용 확인 가능)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200")
    @GetMapping("/users/{userKey}/messages")
    public ResponseEntity<MessagePageResponseWrapper> writtenMessages(
            @Parameter(example = "1111") @PathVariable String userKey,
            @ModelAttribute PageRequestDto pageRequestDto,
            @Parameter(description = "open 여부", example = "true") @RequestParam(required = false) Boolean opened,
            @Parameter(description = "delete 여부", example = "false") @RequestParam(required = false) Boolean deleted,
            @Parameter(description = "public 여부", example = "true") @RequestParam(required = false) Boolean isPublic,
            Authentication authentication) {

        try {
            User user = (User) authentication.getPrincipal();
            if (!userKey.equals(user.getUserKey())) {
                throw new RuntimeException("invalid access");
            }
            MessageSearchCondition condition = new MessageSearchCondition(opened, deleted, isPublic);
            PageResponseDto<MessageDto> messageList = boardService.findUserWriteMessageList(user.getId(), condition, pageRequestDto);

            return MessagePageResponseWrapper.success(messageList);
        } catch (RuntimeException e) {

            return MessagePageResponseWrapper.failAuth(1234, e.getMessage());
        }
    }

    @Operation(summary = "메시지 하나 가져오기 (메시지 소유자 or 작성자)", description = "messageId를 가진 메시지를 가져옴(not opened의 경우 내용 표시 안됨)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200")
    @GetMapping("/messages/{messageId}/member")
    public ResponseEntity<ResponseWrapper<MessageDto>> memberMessage(
            @Parameter(example = "1111") @PathVariable Long messageId) {

        try {
            MessageDto messageDto = boardService.readMessage(messageId, false);
            return ResponseWrapper.success(messageDto);

        } catch (RuntimeException e) {

            return ResponseWrapper.fail(1234, e.getMessage());
        }
    }

    @Operation(summary = "메시지 하나 가져오기 (guest)", description = "messageId를 가진 메시지를 가져옴(deleted 또는 not opened 또는 private 상태의 경우 내용 표시 안됨)")
    @ApiResponse(responseCode = "200")
    @GetMapping("/messages/{messageId}/guest")
    public ResponseEntity<ResponseWrapper<MessageDto>> guestMessage(
            @Parameter(example = "1111") @PathVariable Long messageId) {

        try {
            MessageDto messageDto = boardService.readMessage(messageId, true);
            return ResponseWrapper.success(messageDto);

        } catch (RuntimeException e) {

            return ResponseWrapper.fail(1234, e.getMessage());
        }
    }

    @Operation(summary = "랜덤 메시지 하나 가져오기", description = "random board의 메시지를 가져옴")
    @ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = ResponseWrapper.class),
            examples = @ExampleObject(value = "{\"success\":true,\"data\":{\"messageId\": 1111,\"sender\": \"트리티티\",\"title\": \"메시지 제목\",\"content\": \"메시지 내용\",\"opened\": true,\"createdAt\": \"2024.12.19 21:45\"},\"error\":null}")
    ))
    @GetMapping("/boards/random/messages")
    public ResponseEntity<ResponseWrapper<MessageDto>> getRandomMessage() {

        try {
            MessageDto messageDto = boardService.readRandomMessage();
            return ResponseWrapper.success(messageDto);

        } catch (RuntimeException e) {

            return ResponseWrapper.fail(1234, e.getMessage());
        }
    }

    @Operation(summary = "메시지 업데이트", description = "messageId를 가진 메시지를 업데이트", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200")
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

    @Operation(summary = "메시지 생성", description = "boardId에 해당하는 게시판에 메시지를 생성함", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200")
    @PostMapping("/boards/{boardId}/messages")
    public ResponseEntity<ResponseWrapper<MessageDto>> createMessage(
            @Parameter(description = "게시판의 boardId", example = "1111")
            @PathVariable Long boardId,
            @RequestBody CreateMessageRequest dto,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        MessageDto messageDto = boardService.createMessage(boardId, user.getId(), dto);

        return ResponseWrapper.success(messageDto);
    }

    @Operation(summary = "랜덤 메시지 생성", description = "랜덤 게시판에 메시지를 생성함(isPublic은 무조건 true로 설정되므로 생략 가능)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200")
    @PostMapping("/boards/random/messages")
    public ResponseEntity<ResponseWrapper<MessageDto>> createRandomMessage(@RequestBody CreateMessageRequest dto, Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        MessageDto messageDto = boardService.createRandomMessage(user.getId(), dto);

        return ResponseWrapper.success(messageDto);
    }

}
