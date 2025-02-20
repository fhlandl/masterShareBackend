package toy.masterShareBackend;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import toy.masterShareBackend.domain.Board;
import toy.masterShareBackend.domain.Message;
import toy.masterShareBackend.domain.User;
import toy.masterShareBackend.repository.BoardRepository;
import toy.masterShareBackend.repository.MessageRepository;
import toy.masterShareBackend.repository.UserRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("default")
public class TestDataInit {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final MessageRepository messageRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {

        initMessages();
    }

    private void initMessages() {
        User test = userRepository.save(User.builder()
                .username("test")
                .password(passwordEncoder.encode("test_pw"))
                .email("test@abc.com")
                .nickname("test_nick")
                .build());

        Board newTestBoard = Board.builder()
                .maxSize(10)
                .build();
        newTestBoard.setOwner(test);
        Board testBoard = boardRepository.save(newTestBoard);

        User guest = userRepository.save(User.builder()
                .username("guest")
                .password(passwordEncoder.encode("guest_pw"))
                .email("guest@abc.com")
                .nickname("guest_nick")
                .build());

        Board newGuestBoard = Board.builder()
                .maxSize(10)
                .build();
        newGuestBoard.setOwner(guest);
        Board guestBoard = boardRepository.save(newGuestBoard);

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
            Message message = Message.builder()
                    .sender(guest.getNickname())
                    .title(msgSrc[0])
                    .content(msgSrc[1])
                    .build();
            message.setAuthor(guest);
            message.setBoard(testBoard);

            if (openedMsgs.contains(i)) {
                message.open();
            }

            messageRepository.save(message);
        }
    }
}
