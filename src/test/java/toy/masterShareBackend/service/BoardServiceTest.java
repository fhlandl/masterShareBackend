package toy.masterShareBackend.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.masterShareBackend.domain.Board;
import toy.masterShareBackend.domain.Message;
import toy.masterShareBackend.domain.User;
import toy.masterShareBackend.dto.BoardResponse;
import toy.masterShareBackend.dto.MessageDto;
import toy.masterShareBackend.dto.PageRequestDto;
import toy.masterShareBackend.dto.PageResponseDto;
import toy.masterShareBackend.repository.BoardRepository;
import toy.masterShareBackend.repository.MessageRepository;
import toy.masterShareBackend.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Slf4j
class BoardServiceTest {

    @Autowired
    BoardService boardService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    MessageRepository messageRepository;

    private User createUser(String username) {
        return userRepository.save(User.builder()
                .username(username)
                .password(username + "_pw")
                .email(username + "@abc.com")
                .nickname(username + "_nick")
                .build());
    }

    private Board createBoard(User owner, int maxSize) {
        Board newBoard = Board.builder()
                .maxSize(maxSize)
                .build();
        newBoard.setOwner(owner);
        return boardRepository.save(newBoard);
    }

    private Message createMessage(Board board, User author, String sender, String title, String content, boolean opened) {
        Message message = Message.builder()
                .sender(sender)
                .title(title)
                .content(content)
                .build();
        message.setBoard(board);
        if (author != null) {
            message.setAuthor(author);
        }
        if (opened) {
            message.open();
        }
        return messageRepository.save(message);
    }

    @Test
    void findBoard() {
        // given
        User owner = createUser("test");
        Board board = createBoard(owner, 10);

        // when
        BoardResponse response = boardService.findBoard(owner.getUserKey());

        // then
        assertThat(response.getUsername()).isEqualTo(owner.getUsername());
        assertThat(response.getNickname()).isEqualTo(owner.getNickname());
        assertThat(response.getMaxSize()).isEqualTo(board.getMaxSize());
    }

    @Test
    void findMessageList() {
        // given
        User owner = createUser("test");
        User author = createUser("guest");
        Board board = createBoard(owner, 10);

        String[][] messageContents = {
                {"제목1", "아름다운 이 땅에 금수강산에 단군 할아버지가 터 잡으시고"},
                {"제목2", "홍익인간 뜻으로 나라 세우니 대대손손 훌륭한 인물도 많아"},
                {"제목3", "고구려 세운 동명왕 백제 온조왕 알에서 나온 혁거세"},
                {"제목4", "만주 벌판 달려라 광개토대왕 신라 장군 이사부"},
                {"제목5", "백결선생 떡방아 삼천궁녀 의자왕"},
                {"제목6", "황산벌의 계백 맞서 싸운 관창 역사는 흐른다!"},
                {"제목7", "말 목 자른 김유신 통일 문무왕 원효대사 해골물 혜초 천축국"},
                {"제목8", "바다의 왕자 장보고 발해 대조영 귀주대첩 강감찬 서희 거란족"},
                {"제목9", "무단정치 정중부 화포 최무선 죽림칠현 김부식"},
                {"제목10", "지눌 국사 조계종 의천 천태종 대마도 정벌 이종무"},
                {"제목11", "일편단심 정몽주 목화 씨는 문익점"},
                {"제목12", "해동공자 최충 삼국유사 일연 역사는 흐른다!"},
                {"제목13", "황금 보기를 돌 같이 하라 최영 장군의 말씀 받들자"},
                {"제목14", "황희 정승 맹사성 과학 장영실 신숙주와 한명회 역사는 안다"},
                {"제목15", "십만 양병 이율곡 주리 이퇴계 신사임당 오죽헌"},
                {"제목16", "잘 싸운다 곽재우 조헌 김시민 나라 구한 이순신"},
                {"제목17", "태정태세문단세 사육신과 생육신"},
                {"제목18", "몸 바쳐서 논개 행주치마 권율 역사는 흐른다!"},
                {"제목19", "번쩍번쩍 홍길동 의적 임꺽정 대쪽 같은 삼학사 어사 박문수"},
                {"제목20", "삼 년 공부 한석봉 단원 풍속도 방랑 시인 김삿갓 지도 김정호"}
        };

        for (String[] msgSrc : messageContents) {
            createMessage(board, author, author.getNickname(), msgSrc[0], msgSrc[1], false);
        }

        // when
        int pageNum = 2;
        int pageSize= 5;
        PageResponseDto<MessageDto> response = boardService.findMessageList(owner.getUserKey(), new PageRequestDto(pageNum, pageSize));

        // then
        for (int i = 0; i < response.getDataList().size(); i++) {
            MessageDto messageDto = response.getDataList().get(i);
            assertThat(messageDto.getTitle()).isEqualTo(messageContents[14 - i][0]);
            assertThat(messageDto.getSender()).isEqualTo(author.getNickname());
        }

        assertThat(response.getCurrentPage()).isEqualTo(pageNum);
        assertThat(response.getPrevPage()).isEqualTo(pageNum - 1);
        assertThat(response.getNextPage()).isEqualTo(pageNum + 1);
    }

    @Test
    void findOpenedMessageList() {
        // given
        User owner = createUser("test");
        User author = createUser("guest");
        Board board = createBoard(owner, 10);

        String[][] messageContents = {
                {"제목1", "아름다운 이 땅에 금수강산에 단군 할아버지가 터 잡으시고"},
                {"제목2", "홍익인간 뜻으로 나라 세우니 대대손손 훌륭한 인물도 많아"},
                {"제목3", "고구려 세운 동명왕 백제 온조왕 알에서 나온 혁거세"},
                {"제목4", "만주 벌판 달려라 광개토대왕 신라 장군 이사부"},
                {"제목5", "백결선생 떡방아 삼천궁녀 의자왕"},
                {"제목6", "황산벌의 계백 맞서 싸운 관창 역사는 흐른다!"},
                {"제목7", "말 목 자른 김유신 통일 문무왕 원효대사 해골물 혜초 천축국"},
                {"제목8", "바다의 왕자 장보고 발해 대조영 귀주대첩 강감찬 서희 거란족"},
                {"제목9", "무단정치 정중부 화포 최무선 죽림칠현 김부식"},
                {"제목10", "지눌 국사 조계종 의천 천태종 대마도 정벌 이종무"},
                {"제목11", "일편단심 정몽주 목화 씨는 문익점"},
                {"제목12", "해동공자 최충 삼국유사 일연 역사는 흐른다!"},
                {"제목13", "황금 보기를 돌 같이 하라 최영 장군의 말씀 받들자"},
                {"제목14", "황희 정승 맹사성 과학 장영실 신숙주와 한명회 역사는 안다"},
                {"제목15", "십만 양병 이율곡 주리 이퇴계 신사임당 오죽헌"},
                {"제목16", "잘 싸운다 곽재우 조헌 김시민 나라 구한 이순신"},
                {"제목17", "태정태세문단세 사육신과 생육신"},
                {"제목18", "몸 바쳐서 논개 행주치마 권율 역사는 흐른다!"},
                {"제목19", "번쩍번쩍 홍길동 의적 임꺽정 대쪽 같은 삼학사 어사 박문수"},
                {"제목20", "삼 년 공부 한석봉 단원 풍속도 방랑 시인 김삿갓 지도 김정호"}
        };

        List<Integer> openedMsgs = List.of(5, 12, 19);
        for (int i = 0; i < messageContents.length; i++) {
            String[] msgSrc = messageContents[i];
            createMessage(board, author, author.getNickname(), msgSrc[0], msgSrc[1], openedMsgs.contains(i));
        }

        // when
        int pageNum = 1;
        int pageSize= 5;
        PageResponseDto<MessageDto> response = boardService.findOpenedMessageList(board.getBoardKey(), new PageRequestDto(pageNum, pageSize));

        // then
        for (int i = 0; i < response.getDataList().size(); i++) {
            int contentIdx = openedMsgs.get(2 - i);
            MessageDto messageDto = response.getDataList().get(i);
            assertThat(messageDto.getTitle()).isEqualTo(messageContents[contentIdx][0]);
            assertThat(messageDto.getContent()).isEqualTo(messageContents[contentIdx][1]);
            assertThat(messageDto.getSender()).isEqualTo(author.getNickname());
            assertThat(messageDto.isOpened()).isTrue();
        }

        assertThat(response.getCurrentPage()).isEqualTo(pageNum);
        assertThat(response.getPrevPage()).isEqualTo(null);
        assertThat(response.getNextPage()).isEqualTo(null);
    }

    @Test
    void readMessageSuccess() {
        // given
        User owner = createUser("test");
        User author = createUser("guest");
        Board board = createBoard(owner, 10);

        String title = "제목";
        String content = "내용";
        String sender = author.getNickname();

        Message message = createMessage(board, author, sender, title, content, true);

        // when
        MessageDto messageDto = boardService.readMessage(message.getMessageKey());

        // then
        assertThat(messageDto.getSender()).isEqualTo(sender);
        assertThat(messageDto.getTitle()).isEqualTo(title);
        assertThat(messageDto.getContent()).isEqualTo(content);
        assertThat(messageDto.isOpened()).isTrue();
    }

    @Test
    void readMessageFail() {
        // given
        User owner = createUser("test");
        User author = createUser("guest");
        Board board = createBoard(owner, 10);

        String title = "제목";
        String content = "내용";
        String sender = author.getNickname();

        Message message = createMessage(board, author, sender, title, content, false);

        // when, then
        assertThatThrownBy(() -> {
            boardService.readMessage(message.getMessageKey());
        });
    }

    @Test
    void openMessage() {
        // given
        User owner = createUser("test");
        User author = createUser("guest");
        Board board = createBoard(owner, 10);

        String title = "제목";
        String content = "내용";
        String sender = author.getNickname();

        Message message = createMessage(board, author, sender, title, content, false);

        // when
        MessageDto messageDto = boardService.openMessage(message.getMessageKey());

        // then
        assertThat(messageDto.getSender()).isEqualTo(sender);
        assertThat(messageDto.getTitle()).isEqualTo(title);
        assertThat(messageDto.getContent()).isEqualTo(content);
        assertThat(messageDto.isOpened()).isTrue();
    }

    @Test
    void createMessage() {
        // given
        User owner = createUser("test");
        Board board = createBoard(owner, 10);

        String sender = "보낸사람";
        String title = "제목";
        String content = "내용";

        // when
        MessageDto messageDto = boardService.createMessage(owner.getUserKey(), sender, title, content);

        // then
        log.info("Message {} created - {}", messageDto.getMessageKey(), messageDto.getCreatedAt());
        assertThat(messageDto.getSender()).isEqualTo(sender);
        assertThat(messageDto.getTitle()).isEqualTo(title);
        assertThat(messageDto.getContent()).isNull();
        assertThat(messageDto.isOpened()).isFalse();
    }

    @Test
    void deleteMessage() {
        // given
        User owner = createUser("test");
        Board board = createBoard(owner, 10);

        String sender = "보낸사람";
        String title = "제목";
        String content = "내용";

        Message message = createMessage(board, null, sender, title, content, false);

        // when
        boardService.deleteMessage(message.getMessageKey());

        // then
        Message foundMessage = messageRepository.findById(message.getId()).get();
        assertThat(foundMessage.isDeleted()).isTrue();
    }
}
