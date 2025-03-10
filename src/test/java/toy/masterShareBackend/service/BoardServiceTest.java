package toy.masterShareBackend.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.masterShareBackend.domain.Board;
import toy.masterShareBackend.domain.Message;
import toy.masterShareBackend.domain.User;
import toy.masterShareBackend.dto.*;
import toy.masterShareBackend.repository.MessageRepository;
import toy.masterShareBackend.util.TestUtil;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static toy.masterShareBackend.domain.UserRole.*;

@SpringBootTest
@Transactional
@Slf4j
class BoardServiceTest {

    @Autowired
    BoardService boardService;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    TestUtil testUtil;

    @PersistenceContext
    EntityManager entityManager;

    final int MESSAGE_COUNT = 40;
    final int OPENED_MESSAGE_COUNT = 25;
    final int DELETED_MESSAGE_COUNT = 10;
    final int PUBLIC_MESSAGE_COUNT = 30;

    final int RANDOM_MESSAGE_COUNT = 10;


    private String[][] getMessageContentsForTest() {
        String[][] messageContents = new String[MESSAGE_COUNT][2];
        for (int i = 0; i < messageContents.length; i++) {
            messageContents[i][0] = "제목" + i;
            messageContents[i][1] = "내용" + i;
        }
        return messageContents;
    }

    private String[][] getRandomMessageContentsForTest() {
        String[][] messageContents = new String[RANDOM_MESSAGE_COUNT][2];
        for (int i = 0; i < messageContents.length; i++) {
            messageContents[i][0] = "랜덤" + i;
            messageContents[i][1] = "랜덤 메시지 내용" + i;
        }
        return messageContents;
    }

    private Set<Integer> getOpenedMessageIndexSet() {
        return testUtil.pickRandomNumbers(0, MESSAGE_COUNT - 1, OPENED_MESSAGE_COUNT);
    }

    private Set<Integer> getDeletedMessageIndexSet() {
        return testUtil.pickRandomNumbers(0, MESSAGE_COUNT - 1, DELETED_MESSAGE_COUNT);
    }

    private Set<Integer> getPublicMessageIndexSet() {
        return testUtil.pickRandomNumbers(0, MESSAGE_COUNT - 1, PUBLIC_MESSAGE_COUNT);
    }

    private void initMessageTestData(
            String[][] messageContents,
            Set<Integer> openedMsgs, Set<Integer> deletedMsgs, Set<Integer> publicMsgs,
            Board board, User author, String sender) {

        for (int i = 0; i < messageContents.length; i++) {
            String[] msgSrc = messageContents[i];
            boolean opened = openedMsgs.contains(i);
            boolean deleted = deletedMsgs.contains(i);
            boolean isPublic = publicMsgs.contains(i);

            testUtil.createMessage(board, author, sender, msgSrc[0], msgSrc[1], opened, deleted, isPublic);
        }

        entityManager.flush();
        entityManager.clear();
    }

    private void initRandomMessageTestData() {
        // 일반 게시판 데이터
        User test = testUtil.createUser("test");
        Board testBoard = testUtil.createBoard(test, 10);

        User guest = testUtil.createUser("guest");
        Board guestBoard = testUtil.createBoard(guest, 10);

        String[][] messageContents = getMessageContentsForTest();
        Set<Integer> openedMsgs = getOpenedMessageIndexSet();
        Set<Integer> deletedMsgs = getDeletedMessageIndexSet();
        Set<Integer> publicMsgs = getPublicMessageIndexSet();

        for (int i = 0; i < messageContents.length; i++) {
            String[] msgSrc = messageContents[i];
            boolean opened = openedMsgs.contains(i);
            boolean deleted = deletedMsgs.contains(i);
            boolean isPublic = publicMsgs.contains(i);

            testUtil.createMessage(testBoard, guest, guest.getNickname(), msgSrc[0], msgSrc[1], opened, deleted, isPublic);
        }

        // admin 사용자 추가
        User admin = testUtil.createUserWithRoles("admin", List.of(USER, MANAGER, ADMIN));
        Board randomBoard = testUtil.createBoard(admin, 10);

        User randomAuthor = testUtil.createUser("random");

        String[][] randomMessageContents = getRandomMessageContentsForTest();

        for (int i = 0; i < randomMessageContents.length; i++) {
            String[] msgSrc = randomMessageContents[i];
            testUtil.createMessage(randomBoard, randomAuthor, randomAuthor.getNickname(), msgSrc[0], msgSrc[1], true, false, true);
        }

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void findAllBoards() {
        // given
        User owner = testUtil.createUser("test");
        Board board1 = testUtil.createBoard(owner, 10);
        Board board2 = testUtil.createBoard(owner, 20);

        // when
        UserBoardsResponse response = boardService.findAllBoards(owner.getUserKey());

        // then
        assertThat(response.getUsername()).isEqualTo(owner.getUsername());
        assertThat(response.getNickname()).isEqualTo(owner.getNickname());
        assertThat(response.getBoards().size()).isEqualTo(2);
        assertThat(response.getBoards().get(0).getMaxSize()).isEqualTo(board1.getMaxSize());
        assertThat(response.getBoards().get(1).getMaxSize()).isEqualTo(board2.getMaxSize());
    }

    private Object[] getTotalCountAndExpectation(
            String[][] messageContents,
            Set<Integer> openedSet, Set<Integer> deletedSet, Set<Integer> publicSet,
            boolean isGuestMode, boolean isShowAllContents, MessageSearchCondition condition,
            int pageNum, int pageSize) {

        Boolean opened = condition.getOpened();
        Boolean deleted = condition.getDeleted();
        Boolean isPublic = condition.getIsPublic();

        List<String[]> result = new ArrayList<>();
        int count = 0;

        for (int idx = MESSAGE_COUNT - 1; idx >= 0; idx--) {
            if ((Boolean.TRUE.equals(opened) && !openedSet.contains(idx)) ||
                    Boolean.FALSE.equals(opened) && openedSet.contains(idx)) {
                continue;
            }
            if ((Boolean.TRUE.equals(deleted) && !deletedSet.contains(idx)) ||
                    Boolean.FALSE.equals(deleted) && deletedSet.contains(idx)) {
                continue;
            }
            if ((Boolean.TRUE.equals(isPublic) && !publicSet.contains(idx)) ||
                    Boolean.FALSE.equals(isPublic) && publicSet.contains(idx)) {
                continue;
            }

            String title = messageContents[idx][0];
            String content;
            if (isShowAllContents) {
                content = messageContents[idx][1];
            } else {
                content = openedSet.contains(idx) ? messageContents[idx][1] : null;
                if (isGuestMode && !publicSet.contains(idx)) {
                    content = null;
                }
            }
            result.add(new String[]{title, content});

            count++;
        }

        int fromIdx = pageSize * (pageNum - 1);
        int toIdx = Math.min(fromIdx + pageSize - 1, count - 1);

        if (count < fromIdx + 1) {
            log.info("{} page doesn't exist", pageNum);
            return new Object[]{count, new String[][]{}};
        }

        String[][] expectation = result.subList(fromIdx, toIdx + 1).stream().toArray(String[][]::new);

        return new Object[]{count, expectation};
    }

    private void compareMessageResponse(PageResponseDto<MessageDto> response, Object[] totalCountAndExpectation, String sender, int curPage, int pageSize) {
        int totalCount = (int) totalCountAndExpectation[0];
        String[][] expectation = (String[][]) totalCountAndExpectation[1];

        for (int i = 0; i < response.getDataList().size(); i++) {
            MessageDto messageDto = response.getDataList().get(i);
            assertThat(messageDto.getTitle()).isEqualTo(expectation[i][0]);
            assertThat(messageDto.getContent()).isEqualTo(expectation[i][1]);
            assertThat(messageDto.getSender()).isEqualTo(sender);
        }

        Integer nextPage = totalCount > curPage * pageSize ? curPage + 1 : null;

        assertThat(response.getCurrentPage()).isEqualTo(curPage);
        assertThat(response.getPrevPage()).isEqualTo(curPage - 1);
        assertThat(response.getNextPage()).isEqualTo(nextPage);
    }

    @Test
    void findMessageList() {
        // given
        User owner = testUtil.createUser("test");
        User author = testUtil.createUser("guest");
        Board board = testUtil.createBoard(owner, 10);

        String[][] messageContents = getMessageContentsForTest();
        Set<Integer> openedMsgs = getOpenedMessageIndexSet();
        Set<Integer> deletedMsgs = getDeletedMessageIndexSet();
        Set<Integer> publicMsgs = getPublicMessageIndexSet();

        String sender = author.getNickname();

        initMessageTestData(messageContents, openedMsgs, deletedMsgs, publicMsgs, board, author, sender);

        entityManager.flush();
        entityManager.clear();

        int pageNum = 2;
        int pageSize= 2;
        PageRequestDto pageRequestDto = new PageRequestDto(pageNum, pageSize);

        MessageSearchCondition condition;
        PageResponseDto<MessageDto> response;
        Object[] totalCountAndExpectation;

        for (Boolean opened : new Boolean[]{null, true, false}) {
            for (Boolean deleted : new Boolean[]{null, true, false}) {
                for (Boolean isPublic : new Boolean[]{null, true, false}) {
                    log.info("CASE: opened={}, deleted={}, isPublic={}, isGuestMode={}", opened, deleted, isPublic, false);
                    condition = new MessageSearchCondition(opened, deleted, isPublic);
                    totalCountAndExpectation = getTotalCountAndExpectation(messageContents, openedMsgs, deletedMsgs, publicMsgs, false, false, condition, pageNum, pageSize);
                    response = boardService.findMessageList(board.getId(), false, condition, pageRequestDto);
                    compareMessageResponse(response, totalCountAndExpectation, sender, pageNum, pageSize);
                }
            }
        }

        // CASE: opened=null, deleted=false, isPublic=null, isGuestMode=true
        log.info("CASE: opened=null, deleted=false, isPublic=null, isGuestMode=true");
        condition = new MessageSearchCondition(null, false, null);
        totalCountAndExpectation = getTotalCountAndExpectation(messageContents, openedMsgs, deletedMsgs, publicMsgs, true, false, condition, pageNum, pageSize);
        response = boardService.findMessageList(board.getId(), true, condition, pageRequestDto);
        compareMessageResponse(response, totalCountAndExpectation, sender, pageNum, pageSize);
    }

    @Test
    void readMessageSuccess() {
        // given
        User owner = testUtil.createUser("test");
        User author = testUtil.createUser("guest");
        Board board = testUtil.createBoard(owner, 10);

        String title = "제목";
        String content = "내용";
        String sender = author.getNickname();

        Message message = testUtil.createMessage(board, author, sender, title, content, true, false, false);

        log.info("CASE: isGuestMode=false");
        MessageDto messageDto = boardService.readMessage(message.getId(), false);

        assertThat(messageDto.getSender()).isEqualTo(sender);
        assertThat(messageDto.getTitle()).isEqualTo(title);
        assertThat(messageDto.getContent()).isEqualTo(content);
        assertThat(messageDto.isOpened()).isTrue();

        log.info("CASE: isGuestMode=true");
        messageDto = boardService.readMessage(message.getId(), true);

        assertThat(messageDto.getSender()).isEqualTo(sender);
        assertThat(messageDto.getTitle()).isEqualTo(title);
        assertThat(messageDto.getContent()).isNull();
        assertThat(messageDto.isOpened()).isTrue();
    }

    @Test
    void updateMessage_open() {
        // given
        User owner = testUtil.createUser("test");
        User author = testUtil.createUser("guest");
        Board board = testUtil.createBoard(owner, 10);

        String title = "제목";
        String content = "내용";
        String sender = author.getNickname();

        Message message = testUtil.createMessage(board, author, sender, title, content, false, false, false);

        // when
        MessageUpdateDto messageUpdateDto = new MessageUpdateDto();
        messageUpdateDto.setOpened(true);
        MessageDto messageDto = boardService.updateMessage(message.getId(), messageUpdateDto);

        // then
        assertThat(messageDto.getSender()).isEqualTo(sender);
        assertThat(messageDto.getTitle()).isEqualTo(title);
        assertThat(messageDto.getContent()).isEqualTo(content);
        assertThat(messageDto.isOpened()).isTrue();
    }

    @Test
    void updateMessage_delete() {
        // given
        User owner = testUtil.createUser("test");
        Board board = testUtil.createBoard(owner, 10);

        String sender = "보낸사람";
        String title = "제목";
        String content = "내용";

        Message message = testUtil.createMessage(board, null, sender, title, content, false, false, false);

        // when
        MessageUpdateDto messageUpdateDto = new MessageUpdateDto();
        messageUpdateDto.setDeleted(true);
        MessageDto messageDto = boardService.updateMessage(message.getId(), messageUpdateDto);

        // then
        MessageDto foundMessage = boardService.readMessage(messageDto.getMessageId(), true);
        assertThat(foundMessage.isDeleted()).isTrue();
    }

    @Test
    void createMessage_test() {
        // given
        User owner = testUtil.createUser("test");
        Board board = testUtil.createBoard(owner, 10);

        User author = testUtil.createUser("author");
        CreateMessageRequest dto = new CreateMessageRequest();
        dto.setSender("보낸사람");
        dto.setTitle("제목");
        dto.setContent("내용");

        // when
        MessageDto messageDto = boardService.createMessage(board.getId(), author.getId(), dto);

        // then
        log.info("Message {} created - {}", messageDto.getMessageId(), messageDto.getCreatedAt());
        assertThat(messageDto.getSender()).isEqualTo(dto.getSender());
        assertThat(messageDto.getTitle()).isEqualTo(dto.getTitle());
        assertThat(messageDto.getContent()).isNull();
        assertThat(messageDto.isOpened()).isFalse();
    }

    @Test
    void readRandomMessage() {
        // given
        initRandomMessageTestData();

        String[][] randomMessageContents = getRandomMessageContentsForTest();

        String[] titles = Arrays.stream(randomMessageContents).map(arr -> arr[0]).toArray(String[]::new);
        String[] contents = Arrays.stream(randomMessageContents).map(arr -> arr[1]).toArray(String[]::new);

        // when, then
        MessageDto messageDto = boardService.readRandomMessage();
        assertThat(titles).contains(messageDto.getTitle());
        assertThat(contents).contains(messageDto.getContent());
//        log.info("messageId: {}, title: {}, content: {}",
//                messageDto.getMessageId(),
//                messageDto.getTitle(),
//                messageDto.getContent());

        messageDto = boardService.readRandomMessage();
        assertThat(titles).contains(messageDto.getTitle());
        assertThat(contents).contains(messageDto.getContent());

        messageDto = boardService.readRandomMessage();
        assertThat(titles).contains(messageDto.getTitle());
        assertThat(contents).contains(messageDto.getContent());

        messageDto = boardService.readRandomMessage();
        assertThat(titles).contains(messageDto.getTitle());
        assertThat(contents).contains(messageDto.getContent());

        messageDto = boardService.readRandomMessage();
        assertThat(titles).contains(messageDto.getTitle());
        assertThat(contents).contains(messageDto.getContent());
    }

    @Test
    void createRandomMessage() {
        // given
        initRandomMessageTestData();

        User author = testUtil.createUser("author");

        CreateMessageRequest dto = new CreateMessageRequest();
        dto.setSender("랜덤 메시지 보낸사람");
        dto.setTitle("랜덤 메시지 제목");
        dto.setContent("랜덤 메시지 내용");

        // when
        MessageDto messageDto = boardService.createRandomMessage(author.getId(), dto);

        entityManager.flush();
        entityManager.clear();

        // then
        Message message = messageRepository.findById(messageDto.getMessageId()).orElseThrow();

        assertThat(message.getSender()).isEqualTo(dto.getSender());
        assertThat(message.getTitle()).isEqualTo(dto.getTitle());
        assertThat(message.getContent()).isEqualTo(dto.getContent());
        assertThat(message.isOpened()).isTrue();
        assertThat(message.isDeleted()).isFalse();
        assertThat(message.getBoard().getOwner().getRoles()).contains(ADMIN);
    }

    @Test
    void findUserWriteMessageList() {
        // given
        User owner = testUtil.createUser("test");
        User author = testUtil.createUser("guest");
        Board board = testUtil.createBoard(owner, 10);

        String[][] messageContents = getMessageContentsForTest();
        Set<Integer> openedMsgs = getOpenedMessageIndexSet();
        Set<Integer> deletedMsgs = getDeletedMessageIndexSet();
        Set<Integer> publicMsgs = getPublicMessageIndexSet();

        String sender = author.getNickname();

        initMessageTestData(messageContents, openedMsgs, deletedMsgs, publicMsgs, board, author, sender);

        int pageNum = 2;
        int pageSize= 2;
        PageRequestDto pageRequestDto = new PageRequestDto(pageNum, pageSize);

        MessageSearchCondition condition;
        PageResponseDto<MessageDto> response;
        Object[] totalCountAndExpectation;

        for (Boolean opened : new Boolean[]{null, true, false}) {
            for (Boolean deleted : new Boolean[]{null, true, false}) {
                for (Boolean isPublic : new Boolean[]{null, true, false}) {
                    log.info("CASE: opened={}, deleted={}, isPublic={}, isGuestMode={}", opened, deleted, isPublic, false);
                    condition = new MessageSearchCondition(opened, deleted, isPublic);
                    totalCountAndExpectation = getTotalCountAndExpectation(messageContents, openedMsgs, deletedMsgs, publicMsgs, false, true, condition, pageNum, pageSize);
                    response = boardService.findUserWriteMessageList(author.getId(), condition, pageRequestDto);
                    compareMessageResponse(response, totalCountAndExpectation, sender, pageNum, pageSize);
                }
            }
        }
    }
}
