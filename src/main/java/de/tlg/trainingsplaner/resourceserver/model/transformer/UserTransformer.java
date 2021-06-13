package de.tlg.trainingsplaner.resourceserver.model.transformer;

import de.tlg.trainingsplaner.resourceserver.model.entity.User;
import de.tlg.trainingsplaner.resourceserver.model.request.UserRegisterRequest;
import de.tlg.trainingsplaner.resourceserver.model.request.UserUpdateRequest;
import de.tlg.trainingsplaner.resourceserver.model.response.UserInfoResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserTransformer {

    public User transformUserRegisterRequestToUser(UserRegisterRequest userRegisterRequest) {
        return new User.Builder()
                .userId(UUID.randomUUID().toString())
                .firstName(userRegisterRequest.getFirstName())
                .lastName(userRegisterRequest.getLastName())
                .gender(userRegisterRequest.getGender())
                .email(userRegisterRequest.getEmail())
                .build();
    }

    public User transformUserUpdateRequestToUser(UserUpdateRequest userUpdateRequest, User user) {
        user.setEmail(userUpdateRequest.getEmail());
        user.setGender(userUpdateRequest.getGender());
        return user;
    }

    public UserInfoResponse transformUserToUserInfoResponse(User user) {
        return new UserInfoResponse(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getGender(),
                user.getEmail()
        );
    }
}
