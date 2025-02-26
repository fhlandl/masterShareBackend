package toy.masterShareBackend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import toy.masterShareBackend.domain.Board;
import toy.masterShareBackend.domain.Message;
import toy.masterShareBackend.domain.User;
import toy.masterShareBackend.domain.UserRole;
import toy.masterShareBackend.repository.BoardRepository;
import toy.masterShareBackend.repository.MessageRepository;
import toy.masterShareBackend.repository.UserRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Profile({"default", "test"})
public class TestUtil {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final MessageRepository messageRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(String username) {
        return userRepository.save(User.builder()
                .username(username)
                .password(passwordEncoder.encode(username + "_pw"))
                .email(username + "@abc.com")
                .nickname(username + "_nick")
                .build());
    }

    public User createUserWithRoles(String username, List<UserRole> roles) {
        return userRepository.save(User.builder()
                .username(username)
                .password(passwordEncoder.encode(username + "_pw"))
                .email(username + "@abc.com")
                .nickname(username + "_nick")
                .roles(roles)
                .build());
    }

    public Board createBoard(User owner, int maxSize) {
        Board newBoard = Board.builder()
                .maxSize(maxSize)
                .build();
        newBoard.setOwner(owner);
        return boardRepository.save(newBoard);
    }

    public Message createMessage(Board board, User author, String sender, String title, String content, boolean opened, boolean deleted) {
        Message message = Message.builder()
                .sender(sender)
                .title(title)
                .content(content)
                .build();
        message.setBoard(board);
        if (author != null) {
            message.setAuthor(author);
        }
        if (opened) {
            message.open();
        }
        if (deleted) {
            message.delete();
        }
        return messageRepository.save(message);
    }
}
