package toy.masterShareBackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.masterShareBackend.domain.Board;
import toy.masterShareBackend.domain.Message;
import toy.masterShareBackend.domain.User;
import toy.masterShareBackend.dto.BoardResponse;
import toy.masterShareBackend.dto.MessageDto;
import toy.masterShareBackend.dto.PageRequestDto;
import toy.masterShareBackend.dto.PageResponseDto;
import toy.masterShareBackend.repository.BoardRepository;
import toy.masterShareBackend.repository.MessageRepository;
import toy.masterShareBackend.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final MessageRepository messageRepository;

    @Transactional(readOnly = true)
    public BoardResponse findBoard(String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow();
        Board board = user.getBoards().stream()
                .findFirst()
                .get();

        return BoardResponse.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .maxSize(board.getMaxSize())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponseDto<MessageDto> findMessageList(String userId, PageRequestDto pageRequestDto) {

        User user = userRepository.findByUserId(userId).orElseThrow();
        Board board = user.getBoards().stream()
                .findFirst()
                .get();

        PageRequest pageable = PageRequest.of(
                pageRequestDto.getPage() - 1,
                pageRequestDto.getSize(),
                Sort.by("createdAt").descending()
        );

        Page<Message> result = messageRepository.findByBoardId(board.getId(), pageable);
        List<MessageDto> dtoList = result.getContent().stream()
                .map(msg -> new MessageDto(msg.getMessageId(), msg.getSender(), msg.getTitle(), msg.getContent()))
                .collect(Collectors.toList());

        long totalCount = result.getTotalElements();

        PageResponseDto pageResponseDto = new PageResponseDto(dtoList, pageRequestDto, totalCount);
        return pageResponseDto;
    }
}
