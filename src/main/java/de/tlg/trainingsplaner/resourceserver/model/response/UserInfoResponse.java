package de.tlg.trainingsplaner.resourceserver.model.response;

import de.tlg.trainingsplaner.resourceserver.model.entity.GenderEnum;

public class UserInfoResponse {
    private String userId;
    private String firstName;
    private String lastName;
    private GenderEnum gender;
    private String email;

    public UserInfoResponse(String userId, String firstName, String lastName, GenderEnum gender, String email) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public void setGender(GenderEnum gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
