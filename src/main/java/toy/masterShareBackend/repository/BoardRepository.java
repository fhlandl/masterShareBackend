package toy.masterShareBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.masterShareBackend.domain.Board;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Optional<Board> findByBoardKey(String boardKey);
}
