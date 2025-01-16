package toy.masterShareBackend.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import toy.masterShareBackend.dto.ResponseWrapper;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/h2-console") ||
                requestURI.startsWith("/swagger-ui") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/favicon.ico") ||
                requestURI.startsWith("/error")) {
            return true;
        }

        if (requestURI.startsWith("/auth/v1/") ||
                requestURI.startsWith("/auth/v1/token/refresh") ||
                requestURI.matches("^/boards/v1/[a-zA-Z0-9_-]+/board$") ||
                requestURI.matches("^/boards/v1/[a-zA-Z0-9_-]+/board/messages$") ||
                requestURI.matches("^/boards/v1/board/[a-zA-Z0-9_-]+/messages/opened$") ||
                requestURI.matches("^/boards/v1/message/[a-zA-Z0-9_-]+$") ||
                requestURI.matches("^/boards/v1/[a-zA-Z0-9_-]+/board/message/new$")) {

            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);

        try {
            String accessToken = getAccessToken(authorizationHeader);

            Map<String, Object> claims = jwtUtil.validateToken(accessToken);
            Authentication authentication = jwtUtil.getAuthentication(claims);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("INVALID_ACCESS_TOKEN");
            log.error(e.getMessage());

            ResponseWrapper<Object> responseDto = ResponseWrapper.failResponse(1111, "INVALID_ACCESS_TOKEN");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(responseDto));
        }

    }

    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
