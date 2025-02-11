package toy.masterShareBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import toy.masterShareBackend.domain.Message;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long>, MessageCustomRepository {

    @Query("select m from Message m join fetch m.board b join fetch b.owner where m.id = :messageId")
    Optional<Message> findByIdWithBoardOwner(long messageId);
}
