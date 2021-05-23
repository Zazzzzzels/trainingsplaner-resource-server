package de.tlg.trainingsplaner.resourceserver.network.provide.user;

import de.tlg.trainingsplaner.resourceserver.config.ApplicationConfiguration;
import de.tlg.trainingsplaner.resourceserver.helper.Helper;
import de.tlg.trainingsplaner.resourceserver.model.entity.User;
import de.tlg.trainingsplaner.resourceserver.model.request.UserUpdateRequest;
import de.tlg.trainingsplaner.resourceserver.model.response.UserInfoResponse;
import de.tlg.trainingsplaner.resourceserver.model.transformer.UserTransformer;
import de.tlg.trainingsplaner.resourceserver.repository.UserRepository;
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
@RequestMapping(path= ApplicationConfiguration.USER_ITEM_BASE_PATH)
public class UserItemController {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserItemController.class);

    @Autowired
    private UserRepository userRepository;

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
        if (Helper.accessTokenInvalid(accessToken)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findUserByUserId(userId);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(UserTransformer.transformUserToUserInfoResponse(user));
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
        if(Helper.accessTokenInvalid(accessToken)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findUserByUserId(userId);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            userRepository.save(UserTransformer.transformUserUpdateRequestToUser(userUpdateRequest, user));
        } catch (Exception exception) {
            LOGGER.error(exception.getLocalizedMessage());
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
        if(Helper.accessTokenInvalid(accessToken)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if(userRepository.findUserByUserId(userId) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User user = userRepository.findUserByUserId(userId);

        try {
            userRepository.deleteById(user.getId());
        } catch (Exception exception) {
            LOGGER.error(exception.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "not implemented")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "501", description = "not implemented", content = @Content)
    })
    @PostMapping
    public ResponseEntity<String> post() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "not implemented")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "501", description = "not implemented", content = @Content)
    })
    @PatchMapping
    public ResponseEntity<String> patch() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
