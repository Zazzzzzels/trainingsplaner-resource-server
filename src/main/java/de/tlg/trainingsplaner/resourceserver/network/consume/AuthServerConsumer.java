package de.tlg.trainingsplaner.resourceserver.network.consume;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

@Component
public class AuthServerConsumer {

//    @Value("${oauth.server.url}")
    private final String authServerEndpoint = "http://localhost:8082/trainingsplaner/auth/token";

    private final RestTemplate restTemplate = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofMillis(3000))
            .setReadTimeout(Duration.ofMillis(3000))
            .build();

    // call Auth Server to check access token
    public HttpStatus checkToken(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization", accessToken);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response;

        try {
            response = restTemplate.exchange(new URI(authServerEndpoint), HttpMethod.GET, httpEntity, String.class);
        } catch(HttpClientErrorException.Unauthorized unauthorizedClientException) {
            return HttpStatus.UNAUTHORIZED;
        } catch(HttpServerErrorException | URISyntaxException internalServerException) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        if(HttpStatus.OK.equals(response.getStatusCode())) {
            return HttpStatus.OK;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}