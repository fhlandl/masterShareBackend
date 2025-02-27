package toy.masterShareBackend.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.masterShareBackend.domain.User;
import toy.masterShareBackend.dto.UserInfo;
import toy.masterShareBackend.dto.UserUpdateDto;
import toy.masterShareBackend.util.TestUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    TestUtil testUtil;

    @Test
    void joinAndLoginSuccess() {
        // given
        String username = "user1";
        String password = "password1234567";
        String email = "masterShare@abc.com";
        String nickname = "master_nick";

        // when
        userService.join(username, password, email, nickname);

        // then
//        userService.login(username, password);
    }

    @Test
    void joinFail() {
        // given
        String username = "user1";
        String password = "password1234567";
        String email = "masterShare@abc.com";
        String nickname = "master_nick";

        // when
        userService.join(username, password, email, nickname);

        // then
        assertThatThrownBy(() -> {
            userService.join(username, password, email, nickname);
        });
    }

    @Test
    void joinSuccessAndLoginFail() {
        // given
        String username = "user1";
        String password = "password1234567";
        String email = "masterShare@abc.com";
        String nickname = "master_nick";

        // when
        userService.join(username, password, email, nickname);

        // then
//        assertThatThrownBy(() -> {
//            userService.login("invalid-user", password);
//        });

//        assertThatThrownBy(() -> {
//            userService.login(username, "invalid-password");
//        });
    }

    @Test
    void findUser() {
        User user = testUtil.createUser("testuser");

        UserInfo userInfo = userService.findUser(user.getId());

        assertThat(userInfo.getUserKey()).isEqualTo(user.getUserKey());
        assertThat(userInfo.getUsername()).isEqualTo(user.getUsername());
        assertThat(userInfo.getEmail()).isEqualTo(user.getEmail());
        assertThat(userInfo.getNickname()).isEqualTo(user.getNickname());
    }

    @Test
    void updateUser() {
        User user = testUtil.createUser("testuser");
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setEmail("abcd@gmail.com");
        userUpdateDto.setNickname("abcd_nick");

        UserInfo userInfo = userService.updateUser(user.getId(), userUpdateDto);

        assertThat(userInfo.getUserKey()).isEqualTo(user.getUserKey());
        assertThat(userInfo.getUsername()).isEqualTo(user.getUsername());
        assertThat(userInfo.getEmail()).isEqualTo(user.getEmail());
        assertThat(userInfo.getNickname()).isEqualTo(user.getNickname());
    }
}
