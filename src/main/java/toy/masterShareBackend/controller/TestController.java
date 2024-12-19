package toy.masterShareBackend.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.masterShareBackend.util.IdUtil;

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

    @GetMapping("/uniqueid")
    public String uniqueId() {
        return IdUtil.generateUniqueId();
    }

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
