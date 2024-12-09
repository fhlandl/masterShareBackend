package toy.masterShareBackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import toy.masterShareBackend.domain.User;
import toy.masterShareBackend.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void join(String username, String password, String email) {
        validateDuplicateUsername(username);

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .build();
        userRepository.save(user);
    }

    private void validateDuplicateUsername(String username) {
        Optional<User> findUser = userRepository.findByUsername(username);
        if (findUser.isPresent()) {
            throw new IllegalStateException("User name duplicated");
        }
    }

    public User login(String username, String password) {

        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElseThrow();
    }
}