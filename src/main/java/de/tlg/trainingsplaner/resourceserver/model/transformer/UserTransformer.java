package de.tlg.trainingsplaner.resourceserver.model.transformer;

import de.tlg.trainingsplaner.resourceserver.model.entity.User;
import de.tlg.trainingsplaner.resourceserver.model.request.UserRegisterRequest;
import de.tlg.trainingsplaner.resourceserver.model.request.UserUpdateRequest;
import de.tlg.trainingsplaner.resourceserver.model.response.UserInfoResponse;

import java.util.UUID;

public class UserTransformer {
    public static User transformUserRegisterRequestToUser(UserRegisterRequest userRegisterRequest) {
        return new User(
                UUID.fromString(userRegisterRequest.getFirstName()).toString(),
                userRegisterRequest.getFirstName(),
                userRegisterRequest.getLastName(),
                userRegisterRequest.getGender(),
                userRegisterRequest.getEmail()
        );
    }

    public static User transformUserUpdateRequestToUser(UserUpdateRequest userUpdateRequest, User user) {
        user.setEmail(userUpdateRequest.getEmail());
        return user;
    }

    public static UserInfoResponse transformUserToUserInfoResponse(User user) {
        return new UserInfoResponse(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getGender(),
                user.getEmail()
        );
    }
}
