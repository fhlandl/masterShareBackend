package toy.masterShareBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.masterShareBackend.domain.User;
import toy.masterShareBackend.dto.*;
import toy.masterShareBackend.jwt.JwtUtil;
import toy.masterShareBackend.service.BoardService;
import toy.masterShareBackend.service.UserService;

@Tag(name = "Auth API", description = "로그인, 회원가입, 권한 등 인증 및 인가에 대한 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/v1")
public class AuthController {

    private final UserService userService;
    private final BoardService boardService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Operation(summary = "회원가입")
    @ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = UserJoinResponse.class)
    ))
    @ApiResponse(responseCode = "400", description = "회원가입 실패", content = @Content(
            schema = @Schema(implementation = UserJoinResponse.class),
            examples = @ExampleObject(value = "{\"message\":\"JOIN_FAILED\",\"success\":\"false\"}")
    ))
    @PostMapping("/join")
    public ResponseEntity<UserJoinResponse> join(@RequestBody UserJoinRequest dto) {

        try {
            User user = userService.join(dto.getUsername(), dto.getPassword(), dto.getEmail(), dto.getNickname());
            boardService.createBoard(user, 10);

            UserTokenInfo userTokenInfo = authenticateAndGenerateToken(dto.getUsername(), dto.getPassword());

            return ResponseEntity.ok(UserJoinResponse.builder()
                    .success(true)
                    .message("Join successful!")
                    .userInfo(userTokenInfo.getUserInfo())
                    .accessToken(userTokenInfo.getAccessToken())
                    .refreshToken(userTokenInfo.getRefreshToken())
                    .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(UserJoinResponse.builder()
                    .success(false)
                    .message("Join failed. " + e.getMessage())
                    .build());
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
    @ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = LoginResponse.class)
    ))
    @ApiResponse(responseCode = "400", description = "회원가입 실패", content = @Content(
            schema = @Schema(implementation = LoginResponse.class),
            examples = @ExampleObject(value = "{\"message\":\"LOGIN_FAILED\",\"success\":\"false\"}")
    ))
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest dto) {

        try {
            UserTokenInfo userTokenInfo = authenticateAndGenerateToken(dto.getUsername(), dto.getPassword());

            return ResponseEntity.ok(LoginResponse.builder()
                    .success(true)
                    .message("Login successful!")
                    .userInfo(userTokenInfo.getUserInfo())
                    .accessToken(userTokenInfo.getAccessToken())
                    .refreshToken(userTokenInfo.getRefreshToken())
                    .build()
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(LoginResponse.builder()
                    .success(false)
                    .message("Login failed: invalid username or password")
                    .build());
        }
    }

    private UserTokenInfo authenticateAndGenerateToken(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        User user = (User) authentication.getPrincipal();
        UserInfo userInfo = new UserInfo(user.getUserId(), user.getUsername(), user.getEmail(), user.getNickname());

        String accessToken = jwtUtil.generateToken(user, 10);
        String refreshToken = jwtUtil.generateToken(user, 60);

        return new UserTokenInfo(userInfo, accessToken, refreshToken);
    }
}
