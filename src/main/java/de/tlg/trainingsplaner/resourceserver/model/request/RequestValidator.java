package de.tlg.trainingsplaner.resourceserver.model.request;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class RequestValidator {

    /*
    validation of a request to register one user
     */
    public boolean isBadUserRegisterRequest(UserRegisterRequest request) {
        return (request.getFirstName() == null || request.getFirstName().isEmpty()) ||
                (request.getLastName() == null || request.getLastName().isEmpty()) ||
                (request.getEmail() == null || request.getEmail().isEmpty())
                        || isInvalidMailAddress(request.getEmail());

    }

    /*
    validation of a request to update an existing user
     */
    public boolean isBadUserUpdateRequest(UserUpdateRequest request) {
        return (request.getEmail() == null || request.getEmail().isEmpty()) ||
                (request.getGender() == null || request.getGender().name().isEmpty())
                        || isInvalidMailAddress(request.getEmail());
    }

    /*
    validate email format
     */
    private boolean isInvalidMailAddress(String email) {
        final String emailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        return !Pattern.compile(emailRegex).matcher(email).matches();
    }
}
