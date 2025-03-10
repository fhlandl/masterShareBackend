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

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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

    public Message createMessage(Board board, User author, String sender, String title, String content, boolean opened, boolean deleted, boolean isPublic) {
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
        message.setPublic(isPublic);
        return messageRepository.save(message);
    }

    public Set<Integer> pickRandomNumbers(int min, int max, int count) {
        if (count > max - min + 1) {
            throw new IllegalArgumentException("Count should be less than the range");
        }

        Set<Integer> randomNumbers = new HashSet<>();
        Random random = new Random();

        while (randomNumbers.size() < count) {
            randomNumbers.add(random.nextInt(max - min + 1) + min);
        }

        return randomNumbers;
    }
}
