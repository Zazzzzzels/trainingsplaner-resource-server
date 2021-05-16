package de.tlg.trainingsplaner.resourceserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class ApplicationConfiguration {

    private static final String APPLICATION_BASE_PATH = "/trainingsplaner/resource-server";

    public static final String USER_COLLECTION_BASE_PATH = APPLICATION_BASE_PATH + "/users";
    public static final String USER_ITEM_BASE_PATH = USER_COLLECTION_BASE_PATH + "/{userId}";

    public static final String USER_RESOURCE_TEST_BASE_PATH = APPLICATION_BASE_PATH + "/test/users";

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(64000);
        filter.setIncludeHeaders(true);
        filter.setBeforeMessagePrefix("[INCOMING REQUEST] ");
        filter.setAfterMessagePrefix("[DATA] ");
        filter.setIncludeClientInfo(true);
        return filter;
    }

}
