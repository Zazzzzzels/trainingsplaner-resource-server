package de.tlg.trainingsplaner.resourceserver.model.request;

import de.tlg.trainingsplaner.resourceserver.model.entity.GenderEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestValidatorTest {

    private RequestValidator requestValidator;

    private UserRegisterRequest userRegisterRequest;
    private UserUpdateRequest userUpdateRequest;

    @BeforeEach
    public void setUp() {
        requestValidator = new RequestValidator();
    }

    /* VALIDATION OF REGISTER REQUESTS */

    @Test
    @DisplayName("check valid register request")
    public void shouldBeValidRegisterRequest() {
        userRegisterRequest = new UserRegisterRequest.Builder()
                .firstName("Alex")
                .lastName("Miller")
                .gender(GenderEnum.DIVERSE)
                .email("alex.miller@gmx.net")
                .build();

        assertThat(requestValidator.isBadUserRegisterRequest(userRegisterRequest)).isFalse();
    }

    @Test
    @DisplayName("check invalid register request with missing required values")
    public void shouldBeBadRegisterRequestCausedByNullValues() {
        userRegisterRequest = new UserRegisterRequest.Builder()
                .firstName("Alex")
                .gender(GenderEnum.DIVERSE)
                .build();

        assertThat(requestValidator.isBadUserRegisterRequest(userRegisterRequest)).isTrue();
    }

    @Test
    @DisplayName("check invalid register request with empty required values")
    public void shouldBeBadRegisterRequestCausedByEmptyValues() {
        userRegisterRequest = new UserRegisterRequest.Builder()
                .firstName("Alex")
                .lastName("")
                .gender(GenderEnum.DIVERSE)
                .email("alex@gmail.com")
                .build();

        assertThat(requestValidator.isBadUserRegisterRequest(userRegisterRequest)).isTrue();
    }

    @Test
    @DisplayName("check invalid register request with bad-formed email")
    public void shouldBeBadRegisterRequestCausedByInvalidEmail() {
        userRegisterRequest = new UserRegisterRequest.Builder()
                .firstName("Alex")
                .lastName("Miller")
                .gender(GenderEnum.DIVERSE)
                .email("alexgmail.com")
                .build();

        assertThat(requestValidator.isBadUserRegisterRequest(userRegisterRequest)).isTrue();
    }

    /* VALIDATION OF UPDATE REQUESTS */

    @Test
    @DisplayName("check valid register request")
    public void shouldBeValidUpdateRequest() {
        userUpdateRequest = new UserUpdateRequest.Builder()
                .email("alex.miller@gmx.net")
                .gender(GenderEnum.DIVERSE)
                .build();

        assertThat(requestValidator.isBadUserUpdateRequest(userUpdateRequest)).isFalse();
    }

    @Test
    @DisplayName("check invalid update request with missing required values")
    public void shouldBeBadUpdateRequestCausedByNullValues() {
        userUpdateRequest = new UserUpdateRequest.Builder().build();

        assertThat(requestValidator.isBadUserUpdateRequest(userUpdateRequest)).isTrue();
    }

    @Test
    @DisplayName("check invalid update request with empty required values")
    public void shouldBeBadUpdateRequestCausedByEmptyValues() {
        userUpdateRequest = new UserUpdateRequest.Builder()
                .email("")
                .gender(GenderEnum.DIVERSE)
                .build();

        assertThat(requestValidator.isBadUserUpdateRequest(userUpdateRequest)).isTrue();
    }

    @Test
    @DisplayName("check invalid update request with bad-formed email")
    public void shouldBeBadUpdateRequestCausedByInvalidEmail() {
        userUpdateRequest = new UserUpdateRequest.Builder()
                .email("alex.miller@gmxt")
                .gender(GenderEnum.DIVERSE)
                .build();

        assertThat(requestValidator.isBadUserUpdateRequest(userUpdateRequest)).isTrue();
    }
}
