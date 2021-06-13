package de.tlg.trainingsplaner.resourceserver.model.transformer;

import de.tlg.trainingsplaner.resourceserver.model.entity.GenderEnum;
import de.tlg.trainingsplaner.resourceserver.model.entity.User;
import de.tlg.trainingsplaner.resourceserver.model.request.UserRegisterRequest;
import de.tlg.trainingsplaner.resourceserver.model.request.UserUpdateRequest;
import de.tlg.trainingsplaner.resourceserver.model.response.UserInfoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the UserTransformer class
 */
public class UserTransformerTest {

    UserTransformer userTransformer;

    private UserRegisterRequest userRegisterRequest;
    private User userToUserInfo;
    private User userToUpdate;
    private UserUpdateRequest userUpdateRequest;

    @BeforeEach
    public void setUp() {
        userTransformer = new UserTransformer();

        userRegisterRequest = new UserRegisterRequest.Builder()
                .firstName("Usain")
                .lastName("Bolt")
                .gender(GenderEnum.MALE)
                .email("usain@bolt.lightning")
                .build();

        userToUserInfo = new User.Builder()
                .userId(UUID.randomUUID().toString())
                .firstName("Chris")
                .lastName("Coleman")
                .email("chris.coleman@sprint.er")
                .gender(GenderEnum.MALE)
                .build();

        userToUpdate = new User.Builder()
                .userId(UUID.randomUUID().toString())
                .firstName("Gina")
                .lastName("Lückenkemper")
                .email("sprint@fast.com")
                .gender(GenderEnum.FEMALE)
                .build();

        userUpdateRequest = new UserUpdateRequest.Builder()
                .email("gina.luecke@running.de")
                .build();
    }

    @Test
    @DisplayName("Transformation of a user register request to user db model")
    public void shouldTransformUserRegisterRequestToUser() {
        User user = userTransformer.transformUserRegisterRequestToUser(userRegisterRequest);
        assertEquals(user.getFirstName(), userRegisterRequest.getFirstName());
        assertEquals(user.getLastName(), userRegisterRequest.getLastName());
        assertEquals(user.getEmail(), userRegisterRequest.getEmail());
        assertEquals(user.getGender(), userRegisterRequest.getGender());
        assertNotNull(user.getUserId());
    }

    @Test
    @DisplayName("Transformation of user db model to user info response")
    public void shouldTransformUserToUserInfoResponse() {
        UserInfoResponse userInfoResponse = userTransformer.transformUserToUserInfoResponse(userToUserInfo);
        assertEquals(userInfoResponse.getUserId(), userToUserInfo.getUserId());
        assertEquals(userInfoResponse.getEmail(), userToUserInfo.getEmail());
        assertEquals(userInfoResponse.getGender(), userToUserInfo.getGender());
        assertEquals(userInfoResponse.getFirstName(), userToUserInfo.getFirstName());
        assertEquals(userInfoResponse.getLastName(), userToUserInfo.getLastName());

    }

    @Test
    @DisplayName("Transformation of a user update request to db model")
    public void shouldTransformUserUpdateRequestToUserTest() {
        User user = userTransformer.transformUserUpdateRequestToUser(userUpdateRequest, userToUpdate);
        assertEquals(user.getEmail(), userUpdateRequest.getEmail());
    }
}
