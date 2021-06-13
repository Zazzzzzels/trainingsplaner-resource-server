package de.tlg.trainingsplaner.resourceserver.network.consume;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthServerConsumerTest {

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    AuthServerConsumer authServerConsumer;

    static String authServerEndpoint = "http://localhost:8082/check/token";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(authServerConsumer, "authServerEndpoint", authServerEndpoint);
    }

    @Test
    @DisplayName("access token is valid")
    public void shouldReturnAccessTokenIsValid() {
        final String validAccessToken = "Bearer validAccessToken";
        ResponseEntity<String> expectedResponseEntity = ResponseEntity.ok().build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization", validAccessToken);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        when(restTemplate.exchange(authServerEndpoint, HttpMethod.GET, httpEntity, String.class)).thenReturn(expectedResponseEntity);

        boolean isTokenInvalid = authServerConsumer.accessTokenUnauthorized(validAccessToken);

        verify(restTemplate, times(1)).exchange(authServerEndpoint, HttpMethod.GET, httpEntity, String.class);
        assertThat(isTokenInvalid).isFalse();
    }

    @Test
    @DisplayName("access token is invalid")
    public void shouldReturnAccessTokenIsInValid() {
        final String invalidAccessToken = "Bearer invalidAccessToken";

        HttpClientErrorException clientErrorException = HttpClientErrorException.create(HttpStatus.UNAUTHORIZED,
                "", new HttpHeaders(), null, null);

        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization", invalidAccessToken);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        when(restTemplate.exchange(authServerEndpoint, HttpMethod.GET, httpEntity, String.class)).thenThrow(clientErrorException);

        boolean isTokenInvalid = authServerConsumer.accessTokenUnauthorized(invalidAccessToken);

        verify(restTemplate, times(1)).exchange(authServerEndpoint, HttpMethod.GET, httpEntity, String.class);
        assertThat(isTokenInvalid).isTrue();
    }

}
