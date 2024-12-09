package toy.masterShareBackend.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    void joinAndLoginSuccess() {
        // given
        String username = "user1";
        String password = "password1234567";
        String email = "masterShare@abc.com";

        // when
        userService.join(username, password, email);

        // then
        userService.login(username, password);
    }

    @Test
    void joinFail() {
        // given
        String username = "user1";
        String password = "password1234567";
        String email = "masterShare@abc.com";

        // when
        userService.join(username, password, email);

        // then
        assertThatThrownBy(() -> {
            userService.join(username, password, email);
        });
    }

    @Test
    void joinSuccessAndLoginFail() {
        // given
        String username = "user1";
        String password = "password1234567";
        String email = "masterShare@abc.com";

        // when
        userService.join(username, password, email);

        // then
        assertThatThrownBy(() -> {
            userService.login("invalid-user", password);
        });

        assertThatThrownBy(() -> {
            userService.login(username, "invalid-password");
        });
    }
}