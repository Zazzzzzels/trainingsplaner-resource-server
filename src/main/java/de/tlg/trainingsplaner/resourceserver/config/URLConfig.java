package de.tlg.trainingsplaner.resourceserver.config;

public class URLConfig {

    /**
     * application base path
     */
    private static final String APPLICATION_BASE_PATH = "/trainingsplaner/resource-server";

    /**
     * path to endpoint for user collection
     */
    public static final String USER_COLLECTION_PATH = APPLICATION_BASE_PATH + "/users";

    /**
     * path to endpoint for user items
     */
    public static final String USER_ITEM_PATH = USER_COLLECTION_PATH + "/{userId}";

    /**
     * path to a test endpoint used to fill database with user info
     */
    public static final String USER_RESOURCE_TEST_PATH = APPLICATION_BASE_PATH + "/test/users";

}
