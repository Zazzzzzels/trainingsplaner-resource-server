package de.tlg.trainingsplaner.resourceserver.model.request;

import de.tlg.trainingsplaner.resourceserver.model.entity.GenderEnum;

public class UserRegisterRequest {

    private String firstName;
    private String lastName;
    private GenderEnum gender;
    private String email;

    private UserRegisterRequest(String firstName, String lastName, GenderEnum gender, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.email = email;
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

    public static class Builder {
        private String firstName;
        private String lastName;
        private GenderEnum gender;
        private String email;

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }
        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder gender(GenderEnum gender) {
            this.gender = gender;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public UserRegisterRequest build() {
            return new UserRegisterRequest(
                  this.firstName,
                  this.lastName,
                  this.gender,
                  this.email
            );
        }
    }
}
