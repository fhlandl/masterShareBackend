package toy.masterShareBackend.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import toy.masterShareBackend.domain.User;
import toy.masterShareBackend.dto.*;
import toy.masterShareBackend.jwt.JwtUtil;
import toy.masterShareBackend.service.BoardService;
import toy.masterShareBackend.service.UserService;

import java.util.Date;
import java.util.Map;

@Tag(name = "Auth API", description = "로그인, 회원가입, 권한 등 인증 및 인가에 대한 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final BoardService boardService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private static final int REFRESH_TOKEN_THRESHOLD_MINUTES = 60;

    @Operation(summary = "회원가입")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "400", description = "회원가입 실패", content = @Content(
            schema = @Schema(implementation = ResponseWrapper.class),
            examples = @ExampleObject(value = "{\"success\":false,\"data\":null,\"error\":{\"code\":1001,\"message\":\"Join failed: invalid user info\"}}")
    ))
    @PostMapping("/join")
    public ResponseEntity<ResponseWrapper<UserJoinResponse>> join(@RequestBody UserJoinRequest dto) {

        try {
            User user = userService.join(dto.getUsername(), dto.getPassword(), dto.getEmail(), dto.getNickname());
            boardService.createBoard(user, 10);

            UserTokenInfo userTokenInfo = authenticateAndGenerateToken(dto.getUsername(), dto.getPassword());

            UserJoinResponse userJoinResponse = UserJoinResponse.builder()
                    .userInfo(userTokenInfo.getUserInfo())
                    .accessToken(userTokenInfo.getAccessToken())
                    .refreshToken(userTokenInfo.getRefreshToken())
                    .build();

            return ResponseWrapper.success(userJoinResponse);

        } catch (RuntimeException e) {

            return ResponseWrapper.fail(1001, "Join failed: invalid user info");
        }
    }

//    @PostMapping("/login")
//    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest dto) {
//
//        try {
//            User user = userService.login(dto.getUsername(), dto.getPassword());
//            UserInfo userInfo = new UserInfo(user.getUserId(), user.getUsername(), user.getEmail(), user.getNickname());
//
//            return ResponseEntity.ok(new LoginResponse(
//                true,
//                "Login successful!",
//                userInfo
//            ));
//
//        } catch (NoSuchElementException e) {
//
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(false, "Login failed: invalid username or password"));
//        }
//    }

    @Operation(summary = "로그인")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "401", description = "로그인 실패", content = @Content(
            schema = @Schema(implementation = ResponseWrapper.class),
            examples = @ExampleObject(value = "{\"success\":false,\"data\":null,\"error\":{\"code\":1002,\"message\":\"Login failed: invalid username or password\"}}")
    ))
    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<LoginResponse>> login(@RequestBody LoginRequest dto) {

        try {
            UserTokenInfo userTokenInfo = authenticateAndGenerateToken(dto.getUsername(), dto.getPassword());
            LoginResponse loginResponse = LoginResponse.builder()
                    .userInfo(userTokenInfo.getUserInfo())
                    .accessToken(userTokenInfo.getAccessToken())
                    .refreshToken(userTokenInfo.getRefreshToken())
                    .build();

            return ResponseWrapper.success(loginResponse);

        } catch (RuntimeException e) {

            return ResponseWrapper.failAuth(1002, "Login failed: invalid username or password");
        }
    }

    private UserTokenInfo authenticateAndGenerateToken(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        User user = (User) authentication.getPrincipal();
        UserInfo userInfo = new UserInfo(user.getId(), user.getUsername(), user.getEmail(), user.getNickname());

        String accessToken = jwtUtil.generateAccessToken(user.toClaims());
        String refreshToken = jwtUtil.generateRefreshToken(user.toClaims());

        return new UserTokenInfo(userInfo, accessToken, refreshToken);
    }

    @Operation(summary = "토큰 재발급", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "400", description = "refresh token 만료", content = @Content(
            schema = @Schema(implementation = ResponseWrapper.class),
            examples = @ExampleObject(value = "{\"success\":false,\"data\":null,\"error\":{\"code\":3333,\"message\":\"INVALID_REFRESH_TOKEN\"}}")
    ))
    @GetMapping("/token/refresh")
    public ResponseEntity<ResponseWrapper<TokenRefreshResponse>> refresh(
            @Parameter(example = "eyJ0eXAiOiJKV1QiL...")
            @RequestHeader("Authorization") String authHeader,
            @Parameter(example = "eyJ0eXAiOiJKV1QiL...")
            @RequestParam String refreshToken) {

        String TOKEN_PREFIX = "Bearer ";

        // refreshToken이 null인 경우
        if (refreshToken == null) {
            throw new RuntimeException("NULL_REFRESH_TOKEN");
        }

        // Authorization 헤더가 null이거나 유효하지 않은 경우
        if (authHeader == null || authHeader.length() < TOKEN_PREFIX.length()) {
            throw new RuntimeException("INVALID_AUTHORIZATION_HEADER");
        }

        String accessToken = authHeader.substring(TOKEN_PREFIX.length());

        // accessToken이 아직 유효하면 기존 accessToken과 refreshToken 그대로 반환
        try {
            jwtUtil.validateToken(accessToken);
            log.info("Both access token and refresh token are still valid");
            return ResponseWrapper.success(new TokenRefreshResponse(accessToken, refreshToken));
        } catch (ExpiredJwtException e) {
            log.info("Access token has been expired");
        }

        try {
            Map<String, Object> claims = jwtUtil.validateToken(refreshToken);

            String newAccessToken = jwtUtil.generateAccessToken(claims);
            log.info("New access token has been generated");
            // refreshToken 유효 시간이 얼마 남지 않을 경우 재발급
            boolean needsNewRefreshToken = checkTimeLeft((Integer) claims.get("exp"));
            String newRefreshToken = needsNewRefreshToken ? jwtUtil.generateRefreshToken(claims) : refreshToken;
            if (needsNewRefreshToken) {
                log.info("New refresh token has been generated");
            }

            return ResponseWrapper.success(new TokenRefreshResponse(newAccessToken, newRefreshToken));
        } catch (ExpiredJwtException e) {
            log.info("Refresh token has been expired");
        }

        return ResponseWrapper.fail(3333, "INVALID_REFRESH_TOKEN");
    }

    private boolean checkTimeLeft(Integer expiration) {
        Date expirationDate = new Date((long) expiration * (1000));
        long gap = expirationDate.getTime() - System.currentTimeMillis();
        long leftMin = gap / (1000 * 60);

        return leftMin < REFRESH_TOKEN_THRESHOLD_MINUTES;
    }
}
