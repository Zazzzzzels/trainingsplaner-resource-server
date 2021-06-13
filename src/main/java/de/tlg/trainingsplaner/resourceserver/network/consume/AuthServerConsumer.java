package de.tlg.trainingsplaner.resourceserver.network.consume;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthServerConsumer {

    private final static Logger LOGGER = LoggerFactory.getLogger(AuthServerConsumer.class);

    @Value("${app.authServer.tokenValidationEndpoint}")
    private String authServerEndpoint;

    @Autowired
    private RestTemplate restTemplate;

    public boolean accessTokenUnauthorized(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization", accessToken);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        try {
            // at current state not expecting any information within the body
            // the auth server communication only by sending the correct http status
            LOGGER.debug("checking token '{}' at authorization server", accessToken);
            restTemplate.exchange(authServerEndpoint, HttpMethod.GET, httpEntity, String.class);
        } catch (HttpClientErrorException.Unauthorized unauthorizedException) {
            LOGGER.debug("access token '{}' is unauthorized to request data", accessToken);
            return true;
        } catch (Exception serverErrorException)  {
             // catch any other exception and throw it directly back to the client as internal server error
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "an unexpected error happened during access token validation");
        }

        LOGGER.debug("access token '{}' is authorized", accessToken);
        return false;
    }
}