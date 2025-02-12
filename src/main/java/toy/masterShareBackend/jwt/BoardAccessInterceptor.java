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
import org.springframework.web.servlet.HandlerMapping;
import toy.masterShareBackend.domain.User;
import toy.masterShareBackend.dto.ResponseWrapper;
import toy.masterShareBackend.repository.UserRepository;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BoardAccessInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info("Check url for board access: {}", requestURI);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        boolean hasAccess = false;

        // /api/v1/users/{userKey}/boards
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVariables != null) {
            String userKey = pathVariables.get("userKey");
            if (userKey.equals(user.getUserKey())) {
                hasAccess = true;
            }
        }

        if (!hasAccess) {
            log.info("Boards access denied");

            ResponseWrapper<?> responseDto = ResponseWrapper.failResponse(4321, "Boards access denied");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(responseDto));
            return false;
        }

        return true;
    }
}
