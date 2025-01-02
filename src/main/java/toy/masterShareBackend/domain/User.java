package toy.masterShareBackend.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import toy.masterShareBackend.util.IdUtil;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId = IdUtil.generateUniqueId();

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private String email;

    @Column
    private String nickname;

    @OneToMany(mappedBy = "owner")
    private List<Board> boards = new ArrayList<>();

    // 내가 작성한 메시지 목록
    @OneToMany(mappedBy = "author")
    private List<Message> messages = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Builder
    public User(String username, String password, String email, String nickname) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username) && password.equals(user.password) && Objects.equals(email, user.email) && Objects.equals(nickname, user.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, email, nickname);
    }

    public Map<String, Object> toClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("password", password);
        claims.put("email", email);
        claims.put("nickname", nickname);
        claims.put("roles", getAuthorities());

        return claims;
    }
}
