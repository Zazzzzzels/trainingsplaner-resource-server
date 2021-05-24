package de.tlg.trainingsplaner.resourceserver.network.consume;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Component
public class AuthServerConsument {

    final private RestTemplate restTemplate = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofMillis(3000))
            .setReadTimeout(Duration.ofMillis(3000))
            .build();

    // call Auth Server to check access token
    public boolean accessTokenInvalid(String accessToken) {
        final String uri = "http://localhost:8082/trainingsplaner/auth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization", accessToken);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response;

        try {
            response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);
        } catch (HttpClientErrorException clientErrorException) {
            return true;
        }

        return !HttpStatus.OK.equals(response.getStatusCode());
    }
}