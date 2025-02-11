package toy.masterShareBackend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import toy.masterShareBackend.domain.Message;
import toy.masterShareBackend.dto.MessageSearchCondition;

public interface MessageCustomRepository {

    Page<Message> findByBoardIdAndCondition(Long boardId, MessageSearchCondition condition, Pageable pageable);
}
