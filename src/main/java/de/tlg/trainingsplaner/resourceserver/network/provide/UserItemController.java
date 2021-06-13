package de.tlg.trainingsplaner.resourceserver.network.provide;

import de.tlg.trainingsplaner.resourceserver.config.URLConfig;
import de.tlg.trainingsplaner.resourceserver.db.UserDatabase;
import de.tlg.trainingsplaner.resourceserver.model.entity.User;
import de.tlg.trainingsplaner.resourceserver.model.request.RequestValidator;
import de.tlg.trainingsplaner.resourceserver.model.request.UserUpdateRequest;
import de.tlg.trainingsplaner.resourceserver.model.response.UserInfoResponse;
import de.tlg.trainingsplaner.resourceserver.model.transformer.UserTransformer;
import de.tlg.trainingsplaner.resourceserver.network.consume.AuthServerConsumer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path= URLConfig.USER_ITEM_PATH)
public class UserItemController {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserItemController.class);

    @Autowired
    private RequestValidator requestValidator;

    @Autowired
    private UserTransformer userTransformer;

    @Autowired
    private UserDatabase userDatabase;

    @Autowired
    AuthServerConsumer authServerConsumer;

    @Operation(summary = "get a users info by its id", operationId = "getUserInfo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "the user was found",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoResponse.class))}),
            @ApiResponse(responseCode = "401", description = "client is unauthorized to request user info", content = @Content),
            @ApiResponse(responseCode = "404", description = "no user for given id was not found", content = @Content)
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserInfoResponse> getUserInfo(@Parameter(description = "access token to validate request")
                                                            @RequestHeader(name = "authorization") String accessToken,
                                                        @Parameter(description = "userId to be searched")
                                                            @PathVariable String userId) {

        // check access token at auth server
        if(authServerConsumer.accessTokenUnauthorized(accessToken)) {
            LOGGER.debug("Access Token '{}' is not valid.", accessToken);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // make sure that the requested user is existing
        User user = this.userDatabase.findUserById(userId);

        if (user == null) {
            LOGGER.info("User with id '{}' was not found.", userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(this.userTransformer.transformUserToUserInfoResponse(user));
    }

    @Operation(summary = "override an existing user", operationId = "updateUserInfo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "user information was updated", content = @Content),
            @ApiResponse(responseCode = "401", description = "client is unauthorized to update user info", content = @Content),
            @ApiResponse(responseCode = "404", description = "no user for given id was not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "unexpected error", content = @Content)
    })
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> updateUserInfo(@Parameter(description = "access token") @RequestHeader(name = "authorization") String accessToken,
                                                 @Parameter(description = "user id to update") @PathVariable String userId,
                                                 @RequestBody UserUpdateRequest userUpdateRequest) {

        // check access token at auth server
        if(authServerConsumer.accessTokenUnauthorized(accessToken)) {
            LOGGER.debug("Access Token '{}' is not valid.", accessToken);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // validate incoming data and return bad request if validation fails
        if(requestValidator.isBadUserUpdateRequest(userUpdateRequest)) {
            LOGGER.info("User update request is bad.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // find user from db to get access to db id for updating the right user
        // and to ensure that there's a user with the given id which can be updated
        User user = this.userDatabase.findUserById(userId);

        if (user == null) {
            LOGGER.info("User with id '{}' was not found and can't be updated.", userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            // save an updated version of the user, currently only updating the email address is supported
            User updatedUser = this.userTransformer.transformUserUpdateRequestToUser(userUpdateRequest, user);
            this.userDatabase.updateUser(updatedUser);
        } catch (Exception exception) {
            // catch any unexpected exception on database operation
            LOGGER.error("Failed to save user {} .", user);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "delete an existing user", operationId = "deleteUserFromDb")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "the user was deleted", content = @Content),
            @ApiResponse(responseCode = "401", description = "client is unauthorized to delete user info", content = @Content),
            @ApiResponse(responseCode = "404", description = "no user for given id was not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "unexpected error", content = @Content)
    })
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteUserFromDb(@Parameter(description = "access token") @RequestHeader(name = "authorization") String accessToken,
                                                   @Parameter(description = "user to delete") @PathVariable String userId) {

        // check access token at auth server
        if(authServerConsumer.accessTokenUnauthorized(accessToken)) {
            LOGGER.debug("Access Token '{}' is not valid.", accessToken);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // find user in database to get technical db id for deletion
        // and to ensure that there's a user with the given id which can be deleted
        User user = this.userDatabase.findUserById(userId);

        if (user == null) {
            LOGGER.info("User with id '{}' was not found and can't be deleted.", userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            // delete user by using the database id
            this.userDatabase.deleteUserById(user.getId());
        } catch (Exception exception) {
            // catch any unexpected exception on database operation
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        LOGGER.info("User with id '{}' was deleted successfully.", userId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> post() {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @PatchMapping
    public ResponseEntity<String> patch() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
