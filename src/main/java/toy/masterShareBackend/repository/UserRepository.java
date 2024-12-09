package toy.masterShareBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.masterShareBackend.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
