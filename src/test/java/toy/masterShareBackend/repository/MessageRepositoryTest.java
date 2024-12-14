package toy.masterShareBackend.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.masterShareBackend.domain.Board;
import toy.masterShareBackend.domain.Message;
import toy.masterShareBackend.domain.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
class MessageRepositoryTest {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @Test
    void save() {
        // given
        User author = userRepository.save(
                User.builder()
                        .username("author")
                        .password("author_pw")
                        .email("author@abc.com")
                        .nickname("author_nick")
                        .build()
        );

        User owner = userRepository.save(
                User.builder()
                        .username("owner")
                        .password("owner_pw")
                        .email("owner@abc.com")
                        .nickname("owner_nick")
                        .build()
        );

        Board newBoard = Board.builder()
                .maxSize(15)
                .build();
        newBoard.setOwner(owner);
        Board board = boardRepository.save(newBoard);

        String title = "sample title";
        String content = "sample content";
        Message message = Message.builder()
                .title(title)
                .content(content)
                .build();
        LocalDateTime createdAt = message.getCreatedAt();

        message.setAuthor(author);
        message.setBoard(board);

        // when
        Message savedMessage = messageRepository.save(message);

        // then
        Message foundMessage = messageRepository.findById(message.getId()).get();
        assertThat(foundMessage).isEqualTo(savedMessage);
        assertThat(foundMessage.getTitle()).isEqualTo(title);
        assertThat(foundMessage.getContent()).isEqualTo(content);
        assertThat(foundMessage.getCreatedAt()).isEqualTo(createdAt);
    }
}
