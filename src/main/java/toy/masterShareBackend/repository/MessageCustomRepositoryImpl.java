package toy.masterShareBackend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import toy.masterShareBackend.domain.Message;
import toy.masterShareBackend.dto.MessageSearchCondition;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MessageCustomRepositoryImpl implements MessageCustomRepository {

    private final EntityManager em;

    @Override
    public Page<Message> findByBoardIdAndCondition(Long boardId, MessageSearchCondition condition, Pageable pageable) {

        String conditionQuery = buildConditionQuery(condition);
        String sortQuery = buildSortQuery(pageable);

        String jpql = "select m from Message m where m.board.id = :boardId" + conditionQuery + sortQuery;
        String countJpql = "select count(m) from Message m where m.board.id = :boardId" + conditionQuery;

        log.info("jpql={}", jpql);
        log.info("countJpql={}", countJpql);

        TypedQuery<Message> query = em.createQuery(jpql, Message.class);
        setQueryParams(query, boardId, condition);

        // 페이징
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);
        setQueryParams(countQuery, boardId, condition);

        Long totalElements = countQuery.getSingleResult();
        List<Message> contents = query.getResultList();

        return new PageImpl<>(contents, pageable, totalElements);
    }

    private String buildConditionQuery(MessageSearchCondition condition) {
        StringBuilder sb = new StringBuilder();

        if (condition.getOpened() != null) {
            sb.append(" and m.opened = :opened");
        }

        if (condition.getDeleted() != null) {
            sb.append(" and m.deleted = :deleted");
        }

        return sb.toString();
    }

    private String buildSortQuery(Pageable pageable) {
        StringBuilder sb = new StringBuilder();
        if (!pageable.getSort().isEmpty()) {
            sb.append(" order by ");
            boolean commaFlag = false;
            for (Sort.Order order : pageable.getSort()) {
                if (commaFlag) {
                    sb.append(", ");
                }
                sb.append("m.")
                        .append(order.getProperty())
                        .append(" ")
                        .append(order.getDirection().name());
                commaFlag = true;
            }
        }

        return sb.toString();
    }

    private void setQueryParams(TypedQuery<?> query, Long boardId, MessageSearchCondition condition) {
        query.setParameter("boardId", boardId);
        if (condition.getOpened() != null) {
            query.setParameter("opened", condition.getOpened());
        }
        if (condition.getDeleted() != null) {
            query.setParameter("deleted", condition.getDeleted());
        }
    }
}
