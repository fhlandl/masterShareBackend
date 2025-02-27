package toy.masterShareBackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.masterShareBackend.domain.User;
import toy.masterShareBackend.dto.UserInfo;
import toy.masterShareBackend.dto.UserUpdateDto;
import toy.masterShareBackend.repository.UserRepository;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User join(String username, String password, String email, String nickname) {
        validateIllegalUsername(username);
        validateDuplicateUsername(username);

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .nickname(nickname)
                .build();

        return userRepository.save(user);
    }

    private void validateIllegalUsername(String username) {
        if (username.equals("anonymousUser")) {
            throw new IllegalArgumentException("Illegal user name");
        }
    }

    private void validateDuplicateUsername(String username) {
        Optional<User> findUser = userRepository.findByUsername(username);
        if (findUser.isPresent()) {
            throw new IllegalArgumentException("User name duplicated");
        }
    }

//    @Transactional(readOnly = true)
//    public User login(String username, String password) {
//
//        return userRepository.findByUsername(username)
//                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
//                .orElseThrow();
//    }

    public UserInfo findUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();

        return new UserInfo(user.getUserKey(), user.getUsername(), user.getEmail(), user.getNickname());
    }

    public UserInfo updateUser(Long userId, UserUpdateDto dto) {
        User user = userRepository.findById(userId).orElseThrow();

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }

        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }

        return new UserInfo(user.getUserKey(), user.getUsername(), user.getEmail(), user.getNickname());
    }
}
