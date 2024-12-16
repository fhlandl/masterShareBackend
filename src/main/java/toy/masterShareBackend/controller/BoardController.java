package toy.masterShareBackend.controller;

import lombok.RequiredArgsConstructor;
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

    @GetMapping("/{username}/board")
    public BoardResponse board(@PathVariable String username) {

        BoardResponse boardResponse = boardService.findBoard(username);

        return boardResponse;
    }

    @GetMapping("/{username}/board/messages")
    public PageResponseDto<MessageDto> messages(@PathVariable String username, @ModelAttribute PageRequestDto pageRequestDto) {
        return boardService.findMessageList(username, pageRequestDto);
    }
}
