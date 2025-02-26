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
import toy.masterShareBackend.domain.UserRole;
import toy.masterShareBackend.dto.*;
import toy.masterShareBackend.repository.BoardRepository;
import toy.masterShareBackend.repository.MessageRepository;
import toy.masterShareBackend.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public void createBoard(User owner, int maxSize) {

        Board newBoard = Board.builder()
                .maxSize(maxSize)
                .build();
        newBoard.setOwner(owner);
        boardRepository.save(newBoard);
    }

    @Transactional(readOnly = true)
    public UserBoardsResponse findAllBoards(String userKey) {
        User user = userRepository.findByUserKey(userKey).orElseThrow();
        List<BoardDto> boards = user.getBoards().stream()
                .map(e -> new BoardDto(e.getId(), e.getMaxSize()))
                .collect(Collectors.toList());

        return UserBoardsResponse.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .boards(boards)
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponseDto<MessageDto> findMessageList(Long boardId, MessageSearchCondition condition, PageRequestDto pageRequestDto) {

        PageRequest pageable = PageRequest.of(
                pageRequestDto.getPage() - 1,
                pageRequestDto.getSize(),
                Sort.by("createdAt").descending()
        );

        Page<Message> result = messageRepository.findByBoardIdAndCondition(boardId, condition, pageable);

        List<MessageDto> dtoList = result.getContent().stream()
                .map(msg -> convertMessageToMessageDto(msg))
                .collect(Collectors.toList());

        long totalCount = result.getTotalElements();

        PageResponseDto pageResponseDto = new PageResponseDto(dtoList, pageRequestDto, totalCount);
        return pageResponseDto;
    }

    @Transactional(readOnly = true)
    public PageResponseDto<MessageDto> findUserWriteMessageList(Long authorId, MessageSearchCondition condition, PageRequestDto pageRequestDto) {

        PageRequest pageable = PageRequest.of(
                pageRequestDto.getPage() - 1,
                pageRequestDto.getSize(),
                Sort.by("createdAt").descending()
        );

        Page<Message> result = messageRepository.findByAuthorIdAndCondition(authorId, condition, pageable);

        List<MessageDto> dtoList = result.getContent().stream()
                .map(msg -> convertMessageToMessageDto(msg))
                .collect(Collectors.toList());

        long totalCount = result.getTotalElements();

        PageResponseDto pageResponseDto = new PageResponseDto(dtoList, pageRequestDto, totalCount);
        return pageResponseDto;
    }

    @Transactional(readOnly = true)
    public MessageDto readMessage(long messageId) {

        Message message = messageRepository.findById(messageId).orElseThrow();

        return convertMessageToMessageDto(message);
    }

    public MessageDto updateMessage(long messageId, MessageUpdateDto dto) {

        Message message = messageRepository.findById(messageId).orElseThrow();

        if (dto.getOpened() != null && dto.getOpened()) {
            message.open();
        }
        if (dto.getDeleted() != null && dto.getDeleted()) {
            message.delete();
        }

        message.setLastModifiedAt(LocalDateTime.now());

        return convertMessageToMessageDto(message);
    }

    public MessageDto createMessage(Long boardId, String sender, String title, String content, Long authorId) {

        User author = userRepository.findById(authorId).orElseThrow();
        Board board = boardRepository.findById(boardId).orElseThrow();

        Message newMessage = Message.builder()
                .author(author)
                .sender(sender)
                .title(title)
                .content(content)
                .build();
        newMessage.setBoard(board);
        newMessage.setAuthor(author);

        Message message = messageRepository.save(newMessage);

        return convertMessageToMessageDto(message);
    }

    public MessageDto createRandomMessage(String sender, String title, String content, Long authorId) {

        User admin = userRepository.findByRolesContaining(UserRole.ADMIN).orElseThrow();
        Board randomBoard = admin.getBoards().get(0);

        User author = userRepository.findById(authorId).orElseThrow();

        Message newMessage = Message.builder()
                .author(author)
                .sender(sender)
                .title(title)
                .content(content)
                .build();
        newMessage.setBoard(randomBoard);
        newMessage.setAuthor(author);
        newMessage.open();

        Message message = messageRepository.save(newMessage);

        return convertMessageToMessageDto(message);
    }

    @Transactional(readOnly = true)
    public MessageDto readRandomMessage() {
        User admin = userRepository.findByRolesContaining(UserRole.ADMIN).orElseThrow();
        Board randomBoard = admin.getBoards().get(0);
        Message randomMessage = messageRepository.findRandomMessageByBoardId(randomBoard.getId()).orElseThrow();

        return convertMessageToMessageDto(randomMessage);
    }

    private MessageDto convertMessageToMessageDto(Message message) {
        String content = message.isOpened() ? message.getContent() : null;
        return MessageDto.builder()
                .messageId(message.getId())
                .sender(message.getSender())
                .title(message.getTitle())
                .content(content)
                .opened(message.isOpened())
                .deleted(message.isDeleted())
                .createdAt(message.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")))
                .build();
    }
}
