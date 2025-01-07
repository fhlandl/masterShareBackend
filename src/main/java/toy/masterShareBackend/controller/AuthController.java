package toy.masterShareBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        UserInfo userInfo = new UserInfo(user.getUserId(), user.getUsername(), user.getEmail(), user.getNickname());

        String accessToken = jwtUtil.generateToken(user, 60);
        String refreshToken = jwtUtil.generateToken(user, 60 * 24);

        return new UserTokenInfo(userInfo, accessToken, refreshToken);
    }
}
