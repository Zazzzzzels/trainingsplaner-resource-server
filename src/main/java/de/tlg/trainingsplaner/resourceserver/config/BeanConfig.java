package de.tlg.trainingsplaner.resourceserver.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.time.Duration;

@Configuration
public class BeanConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(3000))
                .build();
    }

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setIncludeHeaders(true);
        filter.setBeforeMessagePrefix("[INCOMING REQUEST] ");
        filter.setAfterMessagePrefix("[DATA] ");
        filter.setIncludeClientInfo(true);
        return filter;
    }

    @Bean

    public OpenAPI resourceServerApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Trainingsplaner Resource Server API")
                        .version("0.1")
                        .description("Spring Resource Server")
                );
    }
}
