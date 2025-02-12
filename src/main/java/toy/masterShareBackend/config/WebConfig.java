package toy.masterShareBackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import toy.masterShareBackend.jwt.BoardAccessInterceptor;
import toy.masterShareBackend.jwt.MessageAccessInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final BoardAccessInterceptor boardAccessInterceptor;

    private final MessageAccessInterceptor messageAccessInterceptor;

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//            .allowedOrigins("*")
//            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
//    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

//        registry.addInterceptor(boardAccessInterceptor)
//                .addPathPatterns("/api/v1/users/*/boards");

        registry.addInterceptor(messageAccessInterceptor)
                .addPathPatterns("/api/v1/messages/*");
    }
}
