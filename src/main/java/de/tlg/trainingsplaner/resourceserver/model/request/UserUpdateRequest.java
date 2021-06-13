package de.tlg.trainingsplaner.resourceserver.model.request;

import de.tlg.trainingsplaner.resourceserver.model.entity.GenderEnum;

public class UserUpdateRequest {

    private String email;
    private GenderEnum gender;

    private UserUpdateRequest(String email, GenderEnum gender) {
        this.email = email;
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public void setGender(GenderEnum gender) {
        this.gender = gender;
    }

    public static class Builder {

        private String email;

        private GenderEnum gender;

        public Builder email(String email) {
            this.email = email;
            return this;
        }


        public Builder gender(GenderEnum gender) {
            this.gender = gender;
            return this;
        }

        public UserUpdateRequest build() {
            return new UserUpdateRequest(this.email, this.gender);
        }
    }
}
