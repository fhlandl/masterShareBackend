package toy.masterShareBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.masterShareBackend.domain.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {

}