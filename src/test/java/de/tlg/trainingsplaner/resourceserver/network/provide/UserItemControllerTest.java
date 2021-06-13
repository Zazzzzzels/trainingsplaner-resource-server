package de.tlg.trainingsplaner.resourceserver.network.provide;

import de.tlg.trainingsplaner.resourceserver.db.UserDatabase;
import de.tlg.trainingsplaner.resourceserver.model.entity.GenderEnum;
import de.tlg.trainingsplaner.resourceserver.model.entity.User;
import de.tlg.trainingsplaner.resourceserver.model.request.RequestValidator;
import de.tlg.trainingsplaner.resourceserver.model.request.UserUpdateRequest;
import de.tlg.trainingsplaner.resourceserver.model.response.UserInfoResponse;
import de.tlg.trainingsplaner.resourceserver.model.transformer.UserTransformer;
import de.tlg.trainingsplaner.resourceserver.network.consume.AuthServerConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserItemControllerTest {
    @Mock
    private AuthServerConsumer authServerConsumer;

    @Mock
    private UserDatabase userDatabase;

    @Mock
    private UserTransformer userTransformer;

    @Mock
    private RequestValidator requestValidator;

    @InjectMocks
    private UserItemController userItemController;

    private String invalidAccessToken;
    private String validAccessToken;

    private String existingUserId;
    private String nonExistingUserId;

    private UserUpdateRequest validUserUpdateRequest;
    private UserUpdateRequest badUserUpdateRequest;

    private User userToSave;
    private User userFromDb;

    private UserInfoResponse userInfoResponse;

    @BeforeEach
    public void setUp() {
        invalidAccessToken = "Bearer invalidAccessToken";
        validAccessToken = "Bearer validAccessToken";
        existingUserId = UUID.randomUUID().toString();
        nonExistingUserId = UUID.randomUUID().toString();

        validUserUpdateRequest = new UserUpdateRequest.Builder()
                .email("florence@nd.machine")
                .gender(GenderEnum.FEMALE)
                .build();

        badUserUpdateRequest = new UserUpdateRequest.Builder()
                .email("9ksq04")
                .gender(null)
                .build();

        userToSave = new User.Builder()
                .firstName("Florence")
                .lastName("and the Machine")
                .gender(GenderEnum.FEMALE)
                .email("themachine@florence.com")
                .build();

        userFromDb = new User.Builder()
                .userId(existingUserId)
                .firstName("Florence")
                .lastName("and the Machine")
                .gender(GenderEnum.FEMALE)
                .email("themachine@florence.com")
                .build();

        userInfoResponse = new UserTransformer().transformUserToUserInfoResponse(userFromDb);
    }

    /* GET */
    @Test
    @DisplayName("get user info for user")
    public void shouldReturnUserWithGivenIdAndOK() {
        when(userDatabase.findUserById(existingUserId)).thenReturn(userFromDb);
        when(authServerConsumer.accessTokenUnauthorized(validAccessToken)).thenReturn(false);
        when(userTransformer.transformUserToUserInfoResponse(userFromDb)).thenReturn(userInfoResponse);

        ResponseEntity<UserInfoResponse> response = userItemController.getUserInfo(validAccessToken, existingUserId);

        assertThat(response.getBody()).isNotNull();

        UserInfoResponse responseBody = response.getBody();

        verify(userDatabase, times(1)).findUserById(existingUserId);
        verify(authServerConsumer, times(1)).accessTokenUnauthorized(validAccessToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseBody.getUserId()).isEqualTo(existingUserId);
        assertThat(responseBody.getEmail()).isEqualTo(userFromDb.getEmail());
        assertThat(responseBody.getFirstName()).isEqualTo(userFromDb.getFirstName());
        assertThat(responseBody.getLastName()).isEqualTo(userFromDb.getLastName());
        assertThat(responseBody.getGender()).isEqualTo(userFromDb.getGender());

    }

    @Test
    @DisplayName("try to get user with invalid access token")
    public void getShouldReturnUnauthorizedCausedByInvalidAccessToken() {
        when(authServerConsumer.accessTokenUnauthorized(invalidAccessToken)).thenReturn(true);

        ResponseEntity<UserInfoResponse> response = userItemController.getUserInfo(invalidAccessToken, existingUserId);

        verify(authServerConsumer, times(1)).accessTokenUnauthorized(invalidAccessToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("try to get user with non existing userId")
    public void getShouldReturnNotFound() {
        when(userDatabase.findUserById(nonExistingUserId)).thenReturn(null);

        ResponseEntity<UserInfoResponse> response = userItemController.getUserInfo(validAccessToken, nonExistingUserId);

        verify(userDatabase, times(1)).findUserById(nonExistingUserId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    /* PUT */
    @Test
    @DisplayName("update user info")
    public void shouldUpdateUserInfoAndReturnNoContent() {
        when(userDatabase.findUserById(existingUserId)).thenReturn(userFromDb);
        when(authServerConsumer.accessTokenUnauthorized(validAccessToken)).thenReturn(false);

        ResponseEntity<String> response = userItemController.updateUserInfo(validAccessToken, existingUserId, validUserUpdateRequest);

        verify(userDatabase, times(1)).findUserById(existingUserId);
        verify(authServerConsumer, times(1)).accessTokenUnauthorized(validAccessToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("try to update user with invalid access token")
    public void updateShouldReturnUnauthorizedCausedByInvalidAccessToken() {
        when(authServerConsumer.accessTokenUnauthorized(invalidAccessToken)).thenReturn(true);

        ResponseEntity<String> response = userItemController.updateUserInfo(invalidAccessToken, existingUserId, validUserUpdateRequest);

        verify(authServerConsumer, times(1)).accessTokenUnauthorized(invalidAccessToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("try to update user with non existing userId")
    public void updateShouldReturnNotFound() {
        when(userDatabase.findUserById(nonExistingUserId)).thenReturn(null);

        ResponseEntity<String> response = userItemController.updateUserInfo(validAccessToken, nonExistingUserId, validUserUpdateRequest);

        verify(userDatabase, times(1)).findUserById(nonExistingUserId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("try to update user with invalid request")
    public void shouldReturnBadRequest() {
        when(authServerConsumer.accessTokenUnauthorized(validAccessToken)).thenReturn(false);
        when(requestValidator.isBadUserUpdateRequest(badUserUpdateRequest)).thenReturn(true);

        ResponseEntity<String> response = userItemController.updateUserInfo(validAccessToken, existingUserId, badUserUpdateRequest);

        verify(authServerConsumer, times(1)).accessTokenUnauthorized(validAccessToken);
        verify(requestValidator, times(1)).isBadUserUpdateRequest(badUserUpdateRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(null);
    }

    /* DELETE */
    @Test
    @DisplayName("delete user")
    public void deleteUserAndReturnOK() {
        when(userDatabase.findUserById(existingUserId)).thenReturn(userFromDb);

        ResponseEntity<String> response = userItemController.deleteUserFromDb(validAccessToken, existingUserId);

        verify(userDatabase, times(1)).findUserById(existingUserId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("try to delete user with invalid access token")
    public void deleteShouldReturnUnauthorizedCausedByInvalidAccessToken() {
        when(authServerConsumer.accessTokenUnauthorized(invalidAccessToken)).thenReturn(true);

        ResponseEntity<String> response = userItemController.deleteUserFromDb(invalidAccessToken, existingUserId);

        verify(authServerConsumer, times(1)).accessTokenUnauthorized(invalidAccessToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("try to delete user with non existing userId")
    public void deleteShouldReturnNotFound() {
        when(userDatabase.findUserById(nonExistingUserId)).thenReturn(null);

        ResponseEntity<String> response = userItemController.deleteUserFromDb(validAccessToken, nonExistingUserId);

        verify(userDatabase, times(1)).findUserById(nonExistingUserId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    /* OTHER */
    @Test
    @DisplayName("try to call post")
    public void postDeleteShouldReturnNotAllowed() {
        ResponseEntity<String> response = userItemController.post();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    @DisplayName("try to call patch")
    public void postDeleteShouldReturnNotImplemented() {
        ResponseEntity<String> response = userItemController.patch();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_IMPLEMENTED);
    }
}
