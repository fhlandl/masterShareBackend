package toy.masterShareBackend.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import toy.masterShareBackend.dto.ResponseWrapper;
import toy.masterShareBackend.repository.MessageRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageAccessInterceptor implements HandlerInterceptor {

    private final MessageRepository messageRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info("Check url for message access: {}", requestURI);

        boolean hasAccess = checkMessageAuthorization(request);

        if (!hasAccess) {
            log.info("Message access denied");

            ResponseWrapper<?> responseDto = ResponseWrapper.failResponse(4321, "Message access denied");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(responseDto));
            return false;
        }

        return true;
    }

    private long getMessageIdFromRequest(HttpServletRequest request) {
        long messageId = -1L;
        String[] urlArr = request.getRequestURI().split("/");

        if (isUpdateMessage(request)) {
            messageId = Long.parseLong(urlArr[urlArr.length - 1]);
        } else if (isGetMessage(request)) {
            messageId = Long.parseLong(urlArr[urlArr.length - 2]);
        }

        return messageId;
    }

    private boolean checkMessageAuthorization(HttpServletRequest request) {

        long messageId = getMessageIdFromRequest(request);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("************* username:{}", username);

        boolean hasAccess = false;

        if (isUpdateMessage(request)) {
            // 메시지 수정은 attribute에 따라 권한이 다름. 일단 작성자 또는 소유자가 아닌 경우는 차단
            hasAccess = isMessageAuthor(username, messageId) || isMessageOwner(username, messageId);
        } else if (isGetMessage(request)) {
            // 메시지 조회는 작성자, 소유자 모두 가능
            hasAccess = isMessageAuthor(username, messageId) || isMessageOwner(username, messageId);
        }

        return hasAccess;
    }

    // 메시지 업데이트
    private boolean isUpdateMessage(HttpServletRequest request) {
        return "PUT".equals(request.getMethod()) &&
                request.getRequestURI().matches("^/api/v1/messages/[0-9]+$");
    }

    // 메시지 가져오기
    private boolean isGetMessage(HttpServletRequest request) {
        return "GET".equals(request.getMethod()) &&
                request.getRequestURI().matches("^/api/v1/messages/[0-9]+/member$");
    }

    private boolean isMessageOwner(String username, long messageId) {
        return messageRepository.findByIdWithBoardOwner(messageId)
                .map(message -> username.equals(message.getBoard().getOwner().getUsername()))
                .orElse(false);
    }

    private boolean isMessageAuthor(String username, long messageId) {
        return messageRepository.findByIdWithAuthor(messageId)
                .map(message -> username.equals(message.getAuthor().getUsername()))
                .orElse(false);
    }
}
