package toy.masterShareBackend.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.masterShareBackend.domain.Board;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
class BoardRepositoryTest {

    @Autowired
    BoardRepository boardRepository;

    @Test
    void save() {
        // given
        int MAX_SIZE = 15;
        Board board = Board.builder()
                .maxSize(MAX_SIZE)
                .build();

        // when
        Board savedBoard = boardRepository.save(board);

        // then
        Board foundBoard = boardRepository.findById(savedBoard.getId()).get();
        assertThat(foundBoard).isEqualTo(savedBoard);
        assertThat(foundBoard.getMaxSize()).isEqualTo(MAX_SIZE);
    }

    @Test
    void update() {
        // given
        int MAX_SIZE_BEFORE = 15;
        int MAX_SIZE_AFTER = 20;
        Board board = Board.builder()
                .maxSize(MAX_SIZE_BEFORE)
                .build();
        Board savedBoard = boardRepository.save(board);
        log.info("savedGuestBook maxSize: {}", savedBoard.getMaxSize());

        // when
        Board foundBoard = boardRepository.findById(savedBoard.getId()).get();
        foundBoard.changeMaxSize(MAX_SIZE_AFTER);
        boardRepository.flush();

        // then
        Board updatedBoard = boardRepository.findById(foundBoard.getId()).get();
        log.info("updatedGuestBook maxSize: {}", updatedBoard.getMaxSize());
        assertThat(updatedBoard.getMaxSize()).isEqualTo(MAX_SIZE_AFTER);
    }
}
