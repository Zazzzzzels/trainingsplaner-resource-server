package de.tlg.trainingsplaner.resourceserver.helper;

public class Helper {
    // TODO: make request to authserver to check if accessToken is valid
    public static boolean accessTokenInvalid(String accessToken) {
        final String validToken = "Bearer authorized";
        // workaround to test if program return UNAUTHORIZED if access token is not valid:
        // userId is currently the email address --> needed to check access token

        return !validToken.equals(accessToken);
    }
}
