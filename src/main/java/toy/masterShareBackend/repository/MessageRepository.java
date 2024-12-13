package toy.masterShareBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.masterShareBackend.domain.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
