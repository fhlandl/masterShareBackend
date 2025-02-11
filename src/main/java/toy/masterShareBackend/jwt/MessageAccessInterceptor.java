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

        String[] urlArr = requestURI.split("/");
        long messageId = Long.parseLong(urlArr[urlArr.length - 1]);

        if (shouldNotIntercept(request, messageId)) {
            return true;
        }

        boolean hasAccess = checkMessageAuthorization(messageId);
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

    private boolean checkMessageAuthorization(long messageId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("************* username:{}", username);

        Boolean hasAccess = messageRepository.findByIdWithBoardOwner(messageId)
                .map(message -> message.getBoard().getOwner().getUsername().equals(username))
                .orElse(false);

        return hasAccess;
    }

    private boolean shouldNotIntercept(HttpServletRequest request, long messageId) {
        // 메시지 하나 가져오기
        if ("GET".equals(request.getMethod())) {
            if ("true".equals(request.getParameter("deleted"))) {
                return false;
            }
            Boolean isDeleted = messageRepository.findById(messageId)
                    .map(message -> message.isDeleted())
                    .orElse(false);

            return !isDeleted;
        }

        // 메시지 업데이트
        return false;
    }
}
