package de.tlg.trainingsplaner.resourceserver.model.transformer;

import de.tlg.trainingsplaner.resourceserver.model.entity.User;
import de.tlg.trainingsplaner.resourceserver.model.request.UserRequest;
import de.tlg.trainingsplaner.resourceserver.model.response.UserInfoResponse;

public class UserTransformer {
    public static User transformUserRequestToUser(UserRequest userRequest) {
        return new User(
                userRequest.getFirstName(),
                userRequest.getLastName(),
                userRequest.getEmail()
        );
    }

    public static UserInfoResponse transformUserToUserInfoResponse(User user) {
        return new UserInfoResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }
}
