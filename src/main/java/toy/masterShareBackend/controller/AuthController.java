package toy.masterShareBackend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.masterShareBackend.domain.User;
import toy.masterShareBackend.dto.LoginRequest;
import toy.masterShareBackend.dto.LoginResponse;
import toy.masterShareBackend.dto.UserInfo;
import toy.masterShareBackend.dto.UserJoinRequest;
import toy.masterShareBackend.dto.UserJoinResponse;
import toy.masterShareBackend.service.UserService;

import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/v1")
public class AuthController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<UserJoinResponse> join(@RequestBody UserJoinRequest dto) {

        try {
            User user = userService.join(dto.getUsername(), dto.getPassword(), dto.getEmail(), dto.getNickname());
            UserInfo userInfo = new UserInfo(user.getUserId(), user.getUsername(), user.getEmail(), user.getNickname());
            return ResponseEntity.ok(new UserJoinResponse(
                true,
                "Join successful!",
                userInfo
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new UserJoinResponse(false, "Join failed. " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest dto) {

        try {
            User user = userService.login(dto.getUsername(), dto.getPassword());
            UserInfo userInfo = new UserInfo(user.getUserId(), user.getUsername(), user.getEmail(), user.getNickname());

            return ResponseEntity.ok(new LoginResponse(
                true,
                "Login successful!",
                userInfo
            ));

        } catch (NoSuchElementException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(false, "Login failed: invalid username or password"));
        }
    }
}
