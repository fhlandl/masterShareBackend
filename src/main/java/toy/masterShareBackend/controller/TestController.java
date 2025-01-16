package toy.masterShareBackend.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.masterShareBackend.util.IdUtil;

@Tag(name = "Test API", description = "API 호출 테스트용")
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/connect")
    public TestResponse connect() {

        return new TestResponse("Get Connection Test", "Success!");
    }

    @PostMapping("/connect")
    public TestResponse connect(@RequestBody PostRequest dto) {

        String content = "Hi " + dto.getName() + "! Connection Success!!";
        return new TestResponse("Post Connection Test", content);
    }

//    @ApiResponse(content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "MH5tjB2yoshlnMDDbPdM")))
//    @GetMapping("/uniqueid")
//    public String uniqueId() {
//        return IdUtil.generateUniqueId();
//    }

    @AllArgsConstructor
    @Getter
    static class TestResponse {
        private String title;
        private String content;
    }

    @Getter
    static class PostRequest {
        private String name;
    }
}
