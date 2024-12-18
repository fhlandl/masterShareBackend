package toy.masterShareBackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toy.masterShareBackend.dto.BoardResponse;
import toy.masterShareBackend.dto.MessageDto;
import toy.masterShareBackend.dto.PageRequestDto;
import toy.masterShareBackend.dto.PageResponseDto;
import toy.masterShareBackend.service.BoardService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards/v1")
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/{userId}/board")
    public BoardResponse board(@PathVariable String userId) {

        BoardResponse boardResponse = boardService.findBoard(userId);

        return boardResponse;
    }

    @GetMapping("/{userId}/board/messages")
    public PageResponseDto<MessageDto> messages(@PathVariable String userId, @ModelAttribute PageRequestDto pageRequestDto) {
        return boardService.findMessageList(userId, pageRequestDto);
    }

    @GetMapping("/message/{messageId}")
    public ResponseEntity<?> getMessage(@PathVariable String messageId) {

        try {
            MessageDto messageDto = boardService.readMessage(messageId);
            return ResponseEntity.ok(messageDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/message/open/{messageId}")
    public MessageDto openMessage(@PathVariable String messageId) {

        MessageDto messageDto = boardService.openMessage(messageId);
        return messageDto;
    }
}
