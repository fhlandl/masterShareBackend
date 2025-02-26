package toy.masterShareBackend.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.masterShareBackend.domain.User;
import toy.masterShareBackend.domain.UserRole;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void save() {
        // given
        User user = User.builder()
                .username("master_share")
                .password("master_share_pw")
                .email("master@abc.com")
                .nickname("master_nick")
                .build();

        // when
        User savedUser = userRepository.save(user);

        // then
        User foundUser = userRepository.findById(savedUser.getId()).get();
        assertThat(foundUser).isEqualTo(savedUser);
        assertThat(foundUser.getUsername()).isEqualTo("master_share");
        assertThat(foundUser.getPassword()).isEqualTo("master_share_pw");
        assertThat(foundUser.getEmail()).isEqualTo("master@abc.com");
        assertThat(foundUser.getNickname()).isEqualTo("master_nick");
    }

    @Test
    public void findByRolesContaining() {
        // given
        User test = userRepository.save(User.builder()
                .username("test")
                .password("test_pw")
                .email("test@abc.com")
                .nickname("test_nick")
                .build());

        User admin = userRepository.save(User.builder()
                .username("admin")
                .password("admin_pw")
                .email("admin@abc.com")
                .nickname("admin_nick")
                .roles(List.of(UserRole.USER, UserRole.ADMIN, UserRole.MANAGER))
                .build());

        // when
        User foundAdmin = userRepository.findByRolesContaining(UserRole.ADMIN).orElseThrow();

        // then
        assertThat(foundAdmin).isEqualTo(admin);
        assertThat(foundAdmin.getRoles().size()).isEqualTo(admin.getRoles().size());
        assertThat(foundAdmin.getRoles()).contains(UserRole.USER, UserRole.ADMIN, UserRole.MANAGER);
    }
}
