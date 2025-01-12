package toy.masterShareBackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import toy.masterShareBackend.jwt.MessageAccessInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final MessageAccessInterceptor messageAccessInterceptor;

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//            .allowedOrigins("*")
//            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
//    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(messageAccessInterceptor)
                .addPathPatterns("/boards/v1/message/open/**", "/boards/v1/message/delete/**");
    }
}
