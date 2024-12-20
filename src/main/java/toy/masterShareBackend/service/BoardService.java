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

        Page<Message> result = messageRepository.findByBoardIdAndDeletedFalse(board.getId(), pageable);
        List<MessageDto> dtoList = result.getContent().stream()
                .map(msg -> convertMessageToMessageDto(msg, false))
                .collect(Collectors.toList());

        long totalCount = result.getTotalElements();

        PageResponseDto pageResponseDto = new PageResponseDto(dtoList, pageRequestDto, totalCount);
        return pageResponseDto;
    }

    @Transactional(readOnly = true)
    public MessageDto readMessage(String messageId) {

        Message message = messageRepository.findByMessageId(messageId).orElseThrow();
        if (!message.isOpened()) {
            throw new RuntimeException("Message has not been opened yet");
        }

        return convertMessageToMessageDto(message, true);
    }

    public MessageDto openMessage(String messageId) {

        Message message = messageRepository.findByMessageId(messageId).orElseThrow();
        message.open();

        return convertMessageToMessageDto(message, true);
    }

    public MessageDto createMessage(String ownerId, String sender, String title, String content) {

        User owner = userRepository.findByUserId(ownerId).orElseThrow();
        Board board = owner.getBoards().stream()
                .findFirst()
                .get();

        Message newMessage = Message.builder()
                .sender(sender)
                .title(title)
                .content(content)
                .build();
        newMessage.setBoard(board);

        Message message = messageRepository.save(newMessage);

        return convertMessageToMessageDto(message, false);
    }

    public void deleteMessage(String messageId) {
        Message message = messageRepository.findByMessageId(messageId).orElseThrow();
        message.delete();
    }

    private MessageDto convertMessageToMessageDto(Message message, boolean includeContent) {
        String content = includeContent ? message.getContent() : null;
        return MessageDto.builder()
                .messageId(message.getMessageId())
                .sender(message.getSender())
                .title(message.getTitle())
                .content(content)
                .opened(message.isOpened())
                .createdAt(message.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")))
                .build();
    }
}
