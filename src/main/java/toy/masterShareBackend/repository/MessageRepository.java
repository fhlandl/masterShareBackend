package toy.masterShareBackend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import toy.masterShareBackend.domain.Message;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByBoardIdAndDeletedFalse(Long boardId, Pageable pageable);

    Page<Message> findByBoardIdAndDeletedFalseAndOpenedTrue(Long boardId, Pageable pageable);

    Optional<Message> findByMessageId(String messageId);

    @Query("select m from Message m join fetch m.board b join fetch b.owner where m.messageId = :messageId")
    Optional<Message> findByMessageIdWithBoardOwner(String messageId);
}
