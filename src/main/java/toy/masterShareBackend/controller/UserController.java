package toy.masterShareBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import toy.masterShareBackend.domain.User;
import toy.masterShareBackend.dto.ResponseWrapper;
import toy.masterShareBackend.dto.UserInfo;
import toy.masterShareBackend.dto.UserUpdateDto;
import toy.masterShareBackend.service.UserService;

@Tag(name = "User API", description = "회원 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원 정보 조회", description = "userKey에 해당하는 회원의 정보를 가져옴", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200")
    @GetMapping("/users/{userKey}")
    public ResponseEntity<ResponseWrapper<UserInfo>> getUser(
            @Parameter(example = "9fcU9rdGc-wDQ74GiOnc")
            @PathVariable String userKey,
            Authentication authentication) {

        try {
            User user = (User) authentication.getPrincipal();
            if (!userKey.equals(user.getUserKey())) {
                throw new RuntimeException("invalid access");
            }

            UserInfo userInfo = userService.findUser(user.getId());

            return ResponseWrapper.success(userInfo);
        } catch (RuntimeException e) {

            return ResponseWrapper.failAuth(1234, e.getMessage());
        }

    }

    @Operation(summary = "회원 정보 수정", description = "userKey에 해당하는 회원의 정보를 업데이트", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200")
    @PutMapping("/users/{userKey}")
    public ResponseEntity<ResponseWrapper<UserInfo>> updateUser(
            @Parameter(example = "9fcU9rdGc-wDQ74GiOnc")
            @PathVariable String userKey,
            @RequestBody UserUpdateDto dto,
            Authentication authentication) {

        try {
            User user = (User) authentication.getPrincipal();
            if (!userKey.equals(user.getUserKey())) {
                throw new RuntimeException("invalid access");
            }

            UserInfo userInfo = userService.updateUser(user.getId(), dto);

            return ResponseWrapper.success(userInfo);
        } catch (RuntimeException e) {

            return ResponseWrapper.failAuth(1234, e.getMessage());
        }

    }
}
